package space.provided.event;

@FunctionalInterface
public interface Invoker<T> {

    void invoke(T t);

}