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
 * If a non-default sequence is needed, it can be specified by passing a value as {@code sequence} parameter.
 * <pre>
 *     {@code
 *     @Subscribe(sequence = 1)
 *     public void onMyEvent(MyEvent event) {
 *         // Do anything
 *     }
 *     }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    int sequence() default 0;

}
