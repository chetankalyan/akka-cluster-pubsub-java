package chat;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple actor that sends messages to the mediator. This guy can be collapsed into chatUser if required.
 * Created by chetan.k on 5/1/15.
 */
public class Publisher extends UntypedActor {
    private static final Logger log = LoggerFactory.getLogger(Subscriber.class);
    private ActorRef mediator;
    private String userId ;

    public Publisher(String userId) {
        this.userId = userId;
        this.mediator = DistributedPubSubExtension.get(this.getContext().system()).mediator();
    }

    private void publishChat(ChatMessage chatMessage) {
        log.info("{} sending message:{}",this.userId,chatMessage);
        chatMessage.setTimestamp(System.nanoTime());
        this.mediator.tell(new DistributedPubSubMediator.Publish(chatMessage.getChatId(), chatMessage), this.getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        switch (message.getClass().getSimpleName()) {
            case "ChatMessage":
                ChatMessage chatMessage = ChatMessage.class.cast(message);
                publishChat(chatMessage);
                break;
            default:
                unhandled(message);
        }
    }
}
