package sample;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

/**
 * Created by d.asadullin on 03.03.2015.
 */
public class Simple {
    public static void main(String[] str){
        ActorSystem system = ActorSystem.create("localhost", ConfigFactory.parseString(
                "akka {\n" +
                        "  actor.provider = \"akka.cluster.ClusterActorRefProvider\"\n" +
                        "  remote.netty.tcp {\n" +
                        "    hostname = \"127.0.0.1\"\n" +
                        "    port = 20201" +
                        "  }\n" +
                        "  cluster.seed-nodes = [\"akka.tcp://ClusterSystem@127.0.0.1:20201\"]\n" +
                        "}"));
    }
}
