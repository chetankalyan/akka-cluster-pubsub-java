package chat;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.FiniteDuration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by chetan.k on 5/1/15.
 */
public class ChatUser extends UntypedActor {
    private static final List<String> chatPhrases = Arrays.asList("hi", "hello", "what's the time?", "bye", "goodbye");
    private              Random       random      = new Random();
    private ActorRef subscriber;
    private ActorRef publisher;
    private String   userId;

    public ChatUser(String userId) {
        this.userId = userId;
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
                // schedule the next send at some random interval between 5 and 20 seconds from now
                this.getContext().system().scheduler()
                        .scheduleOnce(FiniteDuration.create(this.random.nextInt(15) + 5, TimeUnit.SECONDS),
                                this.getSelf(),
                                new Tick(),
                                this.getContext().system().dispatcher(),
                                this.getSelf());
                break;
        }
    }
}
