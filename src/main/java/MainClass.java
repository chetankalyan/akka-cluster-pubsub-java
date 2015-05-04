import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import chat.ChatUser;
import cluster.ClusterListener;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chetan.k on 5/1/15.
 */
public class MainClass {
    private static final Logger log = LoggerFactory.getLogger(MainClass.class);

    public static void main(String[] args) {
        if (args.length != 3) {
            log.error("Invalid command line arguments! Usage: portNumber startIndex numUsers");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        int startIndex = Integer.parseInt(args[1]);
        int numUsers = Integer.parseInt(args[2]);

        // Override the configuration of the port
        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port).withFallback(
                ConfigFactory.load());

        String actorSystemName = "ChatApp";
        ActorSystem system = ActorSystem.create(actorSystemName, config);
        // Get cluster events on a separate actor. No-op except for logging for now
        system.actorOf(Props.create(ClusterListener.class), "clusterListener");


        //Join the cluster. Assumption that the seed node is on port 9090
        Cluster cluster = Cluster.get(system);
        Address address = new Address(cluster.selfAddress().protocol(), cluster.selfAddress().system(), cluster
                .selfAddress().host().get(), 9090);
        cluster.join(address);
        for (int i = startIndex; i < startIndex+numUsers; i++) {
            String actorName = "user" + i;
            system.actorOf(Props.create(ChatUser.class, actorName), actorName);
        }
        log.info("{} actors created", system.lookupRoot());
    }
}
