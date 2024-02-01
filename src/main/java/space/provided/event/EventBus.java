package space.provided.event;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class EventBus<T> {

    private static final Predicate<Method> CAN_BE_TARGETED = method -> method.isAnnotationPresent(Subscribe.class) && method.getParameterCount() == 1 && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodType INVOKE_TYPE = MethodType.methodType(void.class, Object.class);

    private final Map<Class<? extends T>, List<Subscriber<T>>> callTree;

    public EventBus() {
        callTree = new HashMap<>();
    }

    /**
     * Add a listener for the events it subscribed to.
     *
     * @param listener The listener object.
     */
    public void subscribe(Object listener) {
        final MethodType factoryType = MethodType.methodType(Invoker.class, listener.getClass());

        Arrays.stream(listener.getClass().getMethods()).filter(CAN_BE_TARGETED).forEach(method -> {
            try {
                final Class<T> event = (Class<T>) method.getParameterTypes()[0];
                final CallSite callSite = LambdaMetafactory.metafactory(LOOKUP, "invoke", factoryType, INVOKE_TYPE, LOOKUP.unreflect(method), MethodType.methodType(void.class, event));
                final Invoker<T> invoker = (Invoker<T>) callSite.getTarget().invoke(listener);

                callTree.computeIfAbsent(event, o -> new LinkedList<>()).add(new Subscriber<>(invoker, listener, method.getAnnotation(Subscribe.class).priority()));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        for (Iterator<Class<? extends T>> iterator = callTree.keySet().iterator(); iterator.hasNext(); ) {
            callTree.get(iterator.next()).sort(Comparator.comparingInt(value -> value.priority));
        }
    }

    /**
     * Remove a listener from subscribed events.
     *
     * @param listener The listener object
     */
    public void unsubscribe(Object listener) {
        final List<Class<? extends T>> removableEvents = new LinkedList<>();
        for (Iterator<Class<? extends T>> iterator = callTree.keySet().iterator(); iterator.hasNext(); ) {
            final Class<? extends T> event = iterator.next();
            final List<Subscriber<T>> subscribers = callTree.get(event);

            subscribers.removeIf(subscriber -> subscriber.listener.equals(listener));

            if (subscribers.isEmpty()) {
                removableEvents.add(event);
            }
        }
        removableEvents.forEach(callTree::remove);
    }

    /**
     * Send an {@code event} to all registered listeners.
     *
     * @param event The event object which will be handled by the registered listeners.
     */
    public void post(T event) {
        final List<Subscriber<T>> subscribers = callTree.get(event.getClass());
        if (subscribers == null) {
            return;
        }

        for (int i = 0; i < subscribers.size(); i++) {
            subscribers.get(i).invoker.invoke(event);
        }
    }

    private static class Subscriber<T> {

        private final Invoker<T> invoker;
        private final Object listener;
        private final int priority;

        private Subscriber(Invoker<T> invoker, Object listener, int priority) {
            this.invoker = invoker;
            this.listener = listener;
            this.priority = priority;
        }
    }
}
