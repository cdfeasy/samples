package sample;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import static akka.pattern.Patterns.ask;

import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.Member;
import akka.cluster.routing.ClusterRouterGroup;
import akka.cluster.routing.ClusterRouterGroupSettings;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.contrib.pattern.ClusterReceptionistExtension;
import akka.contrib.pattern.ClusterSingletonProxy;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.routing.ConsistentHashingGroup;
import akka.routing.ConsistentHashingPool;
import akka.routing.FromConfig;
import akka.util.Timeout;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import main.messages.Count;
import main.messages.Get;
import static main.SpringExtension.SpringExtProvider;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import scala.Function1;
import scala.concurrent.Await;
import scala.concurrent.CanAwait;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.AbstractFunction1;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * A main class to start up the application.
 */
public class Main {

  public static ActorSystem startSlave(){
    ApplicationContext slave = new ClassPathXmlApplicationContext("/slave.xml");
    final ActorSystem systemSlave = slave.getBean(ActorSystem.class);
    ActorRef workerRouter = systemSlave.actorOf(
            SpringExtProvider.get(systemSlave).props("CountingActor"), "counterid1");
      ActorRef workerRouter2 = systemSlave.actorOf(
              SpringExtProvider.get(systemSlave).props("CountingActor"), "counterid2");
      ActorSelection counter=  systemSlave.actorSelection("/user/*");

      counter.tell("hi!",null);
     // ActorRef workerRouter =systemSlave.actorOf(ClusterSingletonProxy.defaultProps("user/singleton/counterid1", "worker"), "counterid1");
//      int totalInstances = 100;
//      Iterable<String> routeesPaths = Collections
//              .singletonList("/user/counterid1");
//      boolean allowLocalRoutees = true;
//      String useRole = "slave";
//      ActorRef workerRouter = systemSlave.actorOf(
//              new ClusterRouterGroup(new ConsistentHashingGroup(routeesPaths),
//                      new ClusterRouterGroupSettings(totalInstances, routeesPaths,
//                              allowLocalRoutees, useRole)).props(), "counterid1");



//    Cluster cluster=Cluster.get(systemSlave);
//      cluster.registerOnMemberUp(new Runnable() {
//          @Override
//          public void run() {
//              System.out.println("-------------------------------------3");
//              systemSlave.actorOf(
//                      SpringExtProvider.get(systemSlave).props("CountingActor"), "counterid1");
//          }
//      });
//    ActorRef workerRouter = systemSlave.actorOf(new ClusterRouterPool(new ConsistentHashingPool(0),
//            new ClusterRouterPoolSettings(totalInstances, maxInstancesPerNode,
//                    allowLocalRoutees, useRole)).props(Props.create(main.CountingActor.class)), "counterid1");
   // ClusterReceptionistExtension.get(systemSlave).registerService(workerRouter);

    //  ActorRef workerRouter =  systemSlave.actorOf((FromConfig.getInstance().props(SpringExtProvider.get(systemSlave).props("CountingActor")).withDispatcher("akka.actor.stashed-dispatcher")), "counterid1");



      System.out.println("-------------------------------------1");
      System.out.println(workerRouter.path());
    workerRouter.tell(new Count(),null);
    System.out.println("-------------------------------------2");

    return systemSlave;
  }

  public static void main(String[] args) throws Exception {
      ActorSystem systemSlave = startSlave();
    ApplicationContext master = new ClassPathXmlApplicationContext("/master.xml");
    ActorSystem systemMaster = master.getBean(ActorSystem.class);

   // ActorRef  counter=  systemMaster.actorOf(FromConfig.getInstance().props(),"counterid1");
     // ActorRef  counter=  systemMaster.actorOf( SpringExtProvider.get(systemMaster).props("CountingActor"),"counterid1");
     // ActorSelection counter=  systemMaster.actorSelection("/user/counterid1") ;
      ActorSelection counter=  systemMaster.actorSelection("akka.tcp://ClusterSystem@127.0.0.1:20202/user/*");
      Thread.sleep(5000);
      Cluster cluster=Cluster.get(systemMaster);
//      cluster.state().members().foreach(new AbstractFunction1<Member, Object>() {
//          @Override
//          public Object apply(Member member) {
//              System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//              System.out.println(member.address().toString());
//              return null;
//          }
//      });



      // tell it to count three times
    //System.out.println(counter1.path());
    //counter1.tell(new Count(), null);
    FiniteDuration duration = FiniteDuration.create(30, TimeUnit.SECONDS);
//      ask(counter, new Count(),
//            Timeout.durationToTimeout(duration));
//    ask(counter, new Count(),
//            Timeout.durationToTimeout(duration));
//    ask(counter, new Count(),
//            Timeout.durationToTimeout(duration));
   //   ActorSystem systemSlave = startSlave();
    counter.tell(new Count(), null);
    counter.tell(new Count(), null);
    counter.tell(new Count(), null);

  //  ActorSystem systemSlave = startSlave();

    Future result= ask(counter, new Get(),
            Timeout.durationToTimeout(duration));


    try {

        System.out.println("----------------------------------");
        System.out.println("----------------------------------");
      System.out.println("Got back " + Await.result(result, duration));
    } catch (Exception e) {
      System.err.println("Failed getting result: " + e.getMessage());
      throw e;
    } finally {
      systemMaster.shutdown();
      systemMaster.awaitTermination();
      systemSlave.shutdown();
    }
  }
}
