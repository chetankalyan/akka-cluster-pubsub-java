import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import chat.ChatUser;
import cluster.ClusterListener;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chetan.k on 5/1/15.
 */
public class MainClass {
    private static final Logger log = LoggerFactory.getLogger(MainClass.class);

    public static void main(String[] args) {
        ActorSystem system1 = ActorSystem.create("ChatApp", ConfigFactory.load());
        // System1 is the seed node of the cluster. Set up the cluster event listener on this
        system1.actorOf(Props.create(ClusterListener.class), "clusterListener");

        system1.actorOf(Props.create(ChatUser.class, "user1"), "user1");
        system1.actorOf(Props.create(ChatUser.class, "user2"), "user2");

        // Create a cluster and make system1 join it
        Cluster cluster = Cluster.get(system1);
        Address joiningAddress = cluster.selfAddress();
        cluster.join(joiningAddress);

        //Repeat the process for other systems now
        for (int i = 3; i < 6; i++) {
            ActorSystem systemi = ActorSystem.create("ChatApp", ConfigFactory.load());
            systemi.actorOf(Props.create(ChatUser.class, "user" + i), "user" + i);
            Cluster clusteri = Cluster.get(systemi);
            clusteri.join(joiningAddress);
        }
        log.info("All actor systems set up. Chats should flow now");
    }
}
