> Java library for event driven applications

# Event system

Design your application with a more modular approach by using events to create hooks and manipulate data from within other components.

## Installation

### Gradle
```groovy
repositories {
    maven { url 'https://registry.provided.space' }
}

dependencies {
    implementation 'space.provided:event-system:TAG'
}
```

### Maven
```xml
<repositories>
    <repository>
        <id>provided</id>
        <name>provided.space</name>
        <url>https://registry.provided.space</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>space.provided</groupId>
        <artifactId>event-system</artifactId>
        <version>TAG</version>
    </dependency>
</dependencies>
```

## Example

The events that are going to be dispatched as well as the listeners, can be any object without the need inheritance.

In this case we want to listen to when a message is being sent. If the context of the event is not required, it can be suppressed by passing the event class to the annotation. Any events that are an instance of `MessageSendListener` will invoke both methods.
```java
public final class MessageSendEvent implements Event {
    
    private MessageSender sender;
    private String message;

    // constructor, getters and setters
}

public final class MessageSendListener {

    @Subscribe
    public void onMessageSend(MessageSendEvent event) {
        System.out.println(String.format("A message is being sent by %1$s.", event.getSender().getUUID()));
    }

    @Subscribe(priority = 1)
    public void onMessageSend(MessageSendEvent event) {
        System.out.println("A message is being sent.");
    }
}
```

The event bus can be obtained with a new instance of `EventBus`. To start listening to events, you have to register a listener. When unregistering a listener, no more events will be dispatched.

To dispatch an event, simply call the `post` method on your EventBus instance and pass your event.
```java
final EventBus<Event> eventBus = new EventBus<>();

final MessageSendListener listener = new MessageSendListener();
eventBus.register(listener);
eventBus.post(new MessageSendEvent(sender, message));

eventBus.unregister(listener);
eventBus.post(new MessageSendEvent(sender, message));
```