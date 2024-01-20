package space.provided.event;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code EventManager} class provides a simple event bus implementation
 * for managing communication between different components in a system.<br>
 * <pre>
 *     {@code
 *     // Get the EventBus for a specific scope
 *     final EventBus appEvents = EventManager.getBus(Application.class);
 *
 *     // Subscribe to events
 *     appEvents.subscribe(myEventListener);
 *
 *     // Publish an event
 *     appEvents.post(new SampleEvent());
 *
 *     // Unsubscribe from events
 *     appEvents.unsubscribe(myEventListener);
 *     }
 * </pre>
 */
public final class EventManager {

    private static final Map<Object, EventBus> BUSES = new HashMap<>();

    private EventManager() {

    }

    /**
     * Get an EventBus for the supplied scope. A new instance will be created if it does not exist already.
     *
     * @param scope Identifier to handle events in different channels.
     * @return Singleton EventBus for the scope
     */
    public static EventBus getBus(String scope) {
        return getBus((Object) scope);
    }

    /**
     * Get an EventBus for the supplied scope. A new instance will be created if it does not exist already.
     *
     * @param scope Identifier to handle events in different channels.
     * @return Singleton EventBus for the scope
     */
    public static EventBus getBus(Class<?> scope) {
        return getBus((Object) scope);
    }

    private static EventBus getBus(Object scope) {
        if (BUSES.containsKey(scope)) {
            return BUSES.get(scope);
        }

        final EventBus eventBus = new EventBus();
        BUSES.put(scope, eventBus);
        return eventBus;
    }
}
