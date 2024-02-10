package space.provided.event;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

/**
 * The {@code EventBus} is used to post events through an entire application, inorder to keep everything modular.<br>
 * There is no limit in event buses. Keeping track of the instance is up to the developer.
 * <pre>
 *     {@code
 *     // Create a new instance of the event bus
 *     final EventBus events = new EventBus();
 *
 *     // Subscribe to events
 *     events.subscribe(myEventListener);
 *
 *     // Publish an event
 *     events.post(new SampleEvent());
 *
 *     // Unsubscribe from events
 *     events.unsubscribe(myEventListener);
 *     }
 * </pre>
 */
public final class EventBus {

    private static final Predicate<Method> CAN_BE_TARGETED = method -> method.isAnnotationPresent(Subscribe.class) && method.getParameterCount() == 1 && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers());

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodType INVOKE_TYPE = MethodType.methodType(void.class, Object.class);

    private final Map<Class<?>, List<Subscriber>> callTree;

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
                final Class<?> event = method.getParameterTypes()[0];
                final CallSite callSite = LambdaMetafactory.metafactory(LOOKUP, "invoke", factoryType, INVOKE_TYPE, LOOKUP.unreflect(method), MethodType.methodType(void.class, event));
                final Invoker invoker = (Invoker) callSite.getTarget().invoke(listener);

                callTree.computeIfAbsent(event, o -> new LinkedList<>()).add(new Subscriber(invoker, listener, method.getAnnotation(Subscribe.class).priority()));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        for (Iterator<Class<?>> iterator = callTree.keySet().iterator(); iterator.hasNext(); ) {
            callTree.get(iterator.next()).sort(Comparator.comparingInt(value -> -value.priority));
        }
    }

    /**
     * Remove a listener from subscribed events.
     *
     * @param listener The listener object
     */
    public void unsubscribe(Object listener) {
        final List<Class<?>> removableEvents = new LinkedList<>();
        for (Iterator<Class<?>> iterator = callTree.keySet().iterator(); iterator.hasNext(); ) {
            final Class<?> event = iterator.next();
            final List<Subscriber> subscribers = callTree.get(event);

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
    public void post(Object event) {
        final List<Subscriber> subscribers = callTree.get(event.getClass());
        if (subscribers == null) {
            return;
        }

        for (int i = 0; i < subscribers.size(); i++) {
            subscribers.get(i).invoker.invoke(event);
        }
    }

    private static class Subscriber {

        private final Invoker invoker;
        private final Object listener;
        private final int priority;

        private Subscriber(Invoker invoker, Object listener, int priority) {
            this.invoker = invoker;
            this.listener = listener;
            this.priority = priority;
        }
    }
}
