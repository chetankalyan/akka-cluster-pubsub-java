package chat;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by chetan.k on 5/1/15.
 */
public class ChatUser extends UntypedActor {

    private Random random = new Random();
    private ActorRef subscriber;
    private ActorRef publisher;
    private String   userId;
    private List<String> chatIdList = new ArrayList<>();

    private static final List<String> chatPhrases = Arrays.asList("hi", "hello", "what's the time?", "bye", "goodbye");
    private static final Logger       log         = LoggerFactory.getLogger(ChatUser.class);

    public ChatUser(String userId) {
        this.userId = userId;
        log.info("{} actor created",this.self());
    }

    @Override
    public void preStart() throws Exception {
        // send a chat message on every tick. This requires a scheduled "tick" to be set up
        this.getContext().system().scheduler()
                .scheduleOnce(FiniteDuration.create(5, TimeUnit.SECONDS),
                        this.getSelf(),
                        new Tick(),
                        this.getContext().system().dispatcher(),
                        this.getSelf());
        // Also subscribe to the "test" channel for chats now
        this.subscriber = this.getContext().actorOf(Props.create(Subscriber.class, this.userId), "subscriber");
        this.subscriber.tell(new TopicMessage(TopicMessage.MessageType.SUBSCRIBE, "test"), this.getSelf());

        this.publisher = this.getContext().actorOf(Props.create(Publisher.class, this.userId), "publisher");
    }

    public void joinChat(String chatId) {
        log.info("{} joining {}", this.userId, chatId);
        this.chatIdList.add(chatId);
        this.subscriber.tell(new TopicMessage(TopicMessage.MessageType.SUBSCRIBE, chatId), this.getSelf());
    }

    public void leaveChat(String chatId) {
        log.info("{} leaving {}", this.userId, chatId);
        this.chatIdList.remove(chatId);
        this.subscriber.tell(new TopicMessage(TopicMessage.MessageType.UNSUBSCRIBE, chatId), this.getSelf());
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        // do nothing, else the scheduler will get kicked in again.
    }

    @Override
    public void postStop() throws Exception {
        this.subscriber.tell(new TopicMessage(TopicMessage.MessageType.UNSUBSCRIBE, "test"), this.getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        switch (message.getClass().getSimpleName()) {
            case "Tick":
                // Send a message now
                String chatString = chatPhrases.get(this.random.nextInt(chatPhrases.size()));
                this.publisher.tell(new ChatMessage(this.userId, "test", chatString), this.getSelf());
                // schedule the next send at some random interval between 5 and 10 seconds from now
                this.getContext().system().scheduler()
                        .scheduleOnce(FiniteDuration.create(this.random.nextInt(5), TimeUnit.SECONDS),
                                this.getSelf(),
                                new Tick(),
                                this.getContext().system().dispatcher(),
                                this.getSelf());
                break;
            case "ChatMessage":
                ChatMessage chatMessage = ChatMessage.class.cast(message);
                if (!chatMessage.getSender().equals(this.userId)) {
                    // we have received a direct message from another user!
                    log.info("[{} Incoming direct message] {}:{}. TimeTaken={}ms", this.userId, chatMessage.getSender(),
                            chatMessage.getMessage(), (System.nanoTime() - chatMessage.getTimestamp())/(1e6));
                }
        }
    }
}
