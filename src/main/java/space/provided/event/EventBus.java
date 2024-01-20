package space.provided.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class EventBus {

    private static final Predicate<Method> IS_SUBSCRIBED = method -> method.isAnnotationPresent(Subscribe.class);
    private static final Predicate<Method> CAN_BE_CALLED = method -> method.getParameterCount() < 2;
    private static final Predicate<Method> CAN_BE_TARGETED = IS_SUBSCRIBED.and(CAN_BE_CALLED);

    private static final Map<Class<?>, List<Method>> CLASS_METHODS = new HashMap<>();

    private final List<Object> listeners;

    EventBus() {
        listeners = new ArrayList<>();
    }

    /**
     * Add a listener for the events it subscribed to.
     *
     * @param listener The listener object (can only be registered once).
     */
    public void register(Object listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a listener from subscribed events.
     *
     * @param listener The listener object
     */
    public void unregister(Object listener) {
        listeners.remove(listener);
    }

    /**
     * Send an {@code event} to all registered listeners.
     *
     * @param event The event object which will be handled by the registered listeners.
     */
    public void post(Object event) {
        for (int i = 0; i < listeners.size(); i++) {
            execute(listeners.get(i), event);
        }
    }

    private void execute(Object listener, Object event) {
        final Class<?> clazz = listener.getClass();
        if (!CLASS_METHODS.containsKey(clazz)) {
            CLASS_METHODS.put(clazz, Arrays.stream(clazz.getMethods()).filter(CAN_BE_TARGETED).collect(Collectors.toList()));
        }

        CLASS_METHODS.get(clazz).stream()
                .filter(method -> canMethodBeInvoked(method, event))
                .forEach(method -> execute(listener, method, event));
    }

    private boolean canMethodBeInvoked(Method method, Object event) {
        if (method.getParameterCount() == 0) {
            return method.getAnnotation(Subscribe.class).value().isAssignableFrom(event.getClass());
        }

        return method.getParameterTypes()[0].isAssignableFrom(event.getClass());
    }

    private void execute(Object listener, Method method, Object event) {
        try {
            if (method.getParameterCount() == 0) {
                method.invoke(listener);
            } else if (method.getParameterCount() == 1) {
                method.invoke(listener, event);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
