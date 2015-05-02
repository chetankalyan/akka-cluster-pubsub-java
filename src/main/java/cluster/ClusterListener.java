package cluster;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chetan.k on 5/1/15.
 */
public class ClusterListener extends UntypedActor {
    private static final Logger log = LoggerFactory.getLogger(ClusterListener.class);
    Cluster cluster = Cluster.get(getContext().system());

    @Override
    public void postStop() throws Exception {
        // unsubscribe from events
        this.cluster.unsubscribe(getSelf());
    }

    @Override
    public void preStart() throws Exception {
        // subscribe to state change events
        this.cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ClusterEvent.MemberUp) {
            log.info("Member {} is up", ClusterEvent.MemberUp.class.cast(message).member());
        } else if (message instanceof ClusterEvent.UnreachableMember) {
            ClusterEvent.UnreachableMember unreachableMember = ClusterEvent.UnreachableMember.class.cast(message);
            log.info("Member {} is unreachable", unreachableMember.member());
        } else if (message instanceof ClusterEvent.MemberRemoved) {
            ClusterEvent.MemberRemoved memberRemoved = ClusterEvent.MemberRemoved.class.cast(message);
            log.info("Member {} has been removed", memberRemoved.member());
        } else if (message instanceof ClusterEvent.MemberEvent) {
            log.info("Member event = {}", ClusterEvent.MemberEvent.class.cast(message));
        } else {
            unhandled(message);
        }
    }
}
