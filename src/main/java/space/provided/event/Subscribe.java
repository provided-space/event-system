package space.provided.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code Subscribe} annotation is used to mark methods that should
 * be invoked when a specific type of event is posted on an event bus.<br>
 * Methods annotated with {@code Subscribe} take the class type of event they'd like to subscribe to.
 * <pre>
 *     {@code
 *     @Subscribe
 *     public void onMyEvent(MyEvent event) {
 *         // Handle the event
 *     }
 *     }
 * </pre>
 * If the context of the event is not important, it can be suppressed by passing the class type as {@code value} parameter.
 * <pre>
 *     {@code
 *     @Subscribe(MyEvent.class)
 *     public void onMyEvent() {
 *         // Do anything
 *     }
 *     }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    Class<?> value() default void.class;
}
