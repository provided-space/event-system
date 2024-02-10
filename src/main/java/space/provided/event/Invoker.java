package space.provided.event;

@FunctionalInterface
public interface Invoker {

    void invoke(Object event);

}
