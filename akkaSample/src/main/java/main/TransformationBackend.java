package main;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;

/**
 * Created by d.asadullin on 05.03.2015.
 */
public class TransformationBackend extends UntypedActor {
    public static final String BACKEND_REGISTRATION = "BackendRegistration";
    Cluster cluster = Cluster.get(getContext().system());

    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), ClusterEvent.MemberUp.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof ClusterEvent.CurrentClusterState) {
            ClusterEvent.CurrentClusterState state = (ClusterEvent.CurrentClusterState) message;
            for (Member member : state.getMembers()) {
                if (member.status().equals(MemberStatus.up())) {
                    register(member);
                }
            }

        } else if (message instanceof ClusterEvent.MemberUp) {
            ClusterEvent.MemberUp mUp = (ClusterEvent.MemberUp) message;
            register(mUp.member());

        } else {
            unhandled(message);
        }
    }

    void register(Member member) {
        if (member.hasRole("master"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    BACKEND_REGISTRATION, getSelf());
    }
}