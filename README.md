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
    implementation 'space.provided:event-system:VERSION'
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
        <version>VERSION</version>
    </dependency>
</dependencies>
```

## Example

The events that are going to be dispatched as well as the listeners, can be any object without the need inheritance.

In this case we want to listen to when a message is being sent. Only methods with exact class matches will be invoked.<br>
If you need to control how early/late a method should be executed, you can take advantage of the `sequence` parameter on the Annotation. The higher the sequence, the later the method will be invoked.
```java
public final class MessageSendEvent {
    
    private MessageSender sender;
    private String message;

    // constructor, getters and setters
}

public final class MessageSendListener {

    @Subscribe
    public void onMessageSend(MessageSendEvent event) {
        System.out.println(String.format("A message is being sent by %1$s.", event.getSender().getUUID()));
    }

    @Subscribe(sequence = 1)
    public void onMessageSend(MessageSendEvent event) {
        System.out.println("A message is being sent.");
    }
}
```

The event bus can be obtained with a new instance of `EventBus`. To start listening to events, you have to register a listener. When unregistering a listener, no more events will be dispatched.

To dispatch an event, simply call the `post` method on your EventBus instance and pass your event.
```java
final EventBus events = new EventBus<>();

final MessageSendListener listener = new MessageSendListener();
events.subscribe(listener);
events.post(new MessageSendEvent(sender, message));

events.unsubscribe(listener);
events.post(new MessageSendEvent(sender, message));
```