package space.provided.event;

import java.util.HashMap;
import java.util.Map;

public final class EventManager {

    private static final Map<Object, EventBus> BUSES = new HashMap<>();

    private EventManager() {

    }

    public static EventBus getBus(Object scope) {
        if (BUSES.containsKey(scope)) {
            return BUSES.get(scope);
        }

        final EventBus eventBus = new EventBus();
        BUSES.put(scope, eventBus);
        return eventBus;
    }
}
