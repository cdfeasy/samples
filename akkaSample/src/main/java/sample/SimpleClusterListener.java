package sample;

import akka.actor.ExtendedActorSystem;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;

/**
 * Created by d.asadullin on 03.03.2015.
 */
public class SimpleClusterListener extends UntypedActor {
    Cluster cluster = Cluster.get(getContext().system());

    @Override
    public void preStart() throws Exception {
        super.preStart();
        cluster.subscribe(getSelf(), ClusterEvent.MemberUp.class);

    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ClusterEvent.MemberUp) {
            System.out.println(((ClusterEvent.MemberUp)message).member().toString());
        }
     //   unhandled(message);
    }
}
