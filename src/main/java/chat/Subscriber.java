package chat;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chetan.k on 5/1/15.
 */
public class Subscriber extends UntypedActor {
    private static final Logger log = LoggerFactory.getLogger(Subscriber.class);
    private ActorRef mediator;
    private String userId;

    public Subscriber(String userId) {
        this.userId = userId;
        this.mediator = DistributedPubSubExtension.get(this.getContext().system()).mediator();
    }

    private void subscribeToChat(String chatId) {
        this.mediator.tell(new DistributedPubSubMediator.Subscribe(chatId, this.getSelf()), this.getSelf());
    }

    private void leaveChat(String chatId) {
        this.mediator.tell(new DistributedPubSubMediator.Unsubscribe(chatId, this.getSelf()), this.getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        switch (message.getClass().getSimpleName()) {
            case "TopicMessage":
                TopicMessage topicMessage = TopicMessage.class.cast(message);
                if (topicMessage.getMessageType() == TopicMessage.MessageType.SUBSCRIBE) {
                    subscribeToChat(topicMessage.getChatId());
                } else {
                    leaveChat(topicMessage.getChatId());
                }
                break;
            case "ChatMessage":
                ChatMessage chatMessage = ChatMessage.class.cast(message);
                if (!chatMessage.getSender().equals(this.userId)) {
                    log.info("[{} Incoming topic message] {}:{}. TimeTaken={}ms", this.userId, chatMessage.getSender(),
                            chatMessage.getMessage(), (System.nanoTime() - chatMessage.getTimestamp())/(1e6));
                }
                break;
            default:
                unhandled(message);
        }
    }
}
