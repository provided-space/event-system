package space.provided.event;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventBusTest {

    @Test
    void testCycleWithPosts() {
        final EventBus events = new EventBus();
        final TestListener listener = new TestListener();

        events.subscribe(listener);
        events.post(new TestEvent());
        final int firstResult = listener.getCounter().get();

        events.unsubscribe(listener);
        events.post(new TestEvent());
        final int secondResult = listener.getCounter().get();

        events.subscribe(listener);
        events.post(new TestEvent());
        final int thirdResult = listener.getCounter().get();

        assertEquals(1, firstResult, "Counter increase after subscribing.");
        assertEquals(1, secondResult, "Counter does not increase after unsubscribing.");
        assertEquals(2, thirdResult, "Counter increase after resubscribing.");
    }

    private static class TestEvent {

    }

    private static class TestListener {

        private final AtomicInteger counter = new AtomicInteger();

        @Subscribe
        public void onEvent(TestEvent event) {
            counter.incrementAndGet();
        }

        public AtomicInteger getCounter() {
            return counter;
        }
    }
}
