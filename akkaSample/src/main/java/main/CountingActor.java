package main;

import akka.actor.UntypedActor;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import main.messages.Count;
import main.messages.Get;

/**
 * An actor that can count using an injected CountingService.
 *
 * @note The scope here is prototype since we want to create a new actor
 * instance for use of this bean.
 */
@Named("CountingActor")
@Scope("prototype")
public class CountingActor extends UntypedActor {


  // the service that will be automatically injected
  final CountingService countingService;

  @Inject
  public CountingActor(@Named("CountingService") CountingService countingService) {
    this.countingService = countingService;
  }

//  public CountingActor() {
//    countingService=null;
//  }

  private int count = 0;

  @Override
  public void onReceive(Object message) throws Exception {
    System.out.println("--------------------------------"+message);
    if (message instanceof Count) {
      count = countingService.increment(count);
    } else if (message instanceof Get) {
      getSender().tell(count, getSelf());
    } else {
      unhandled(message);
    }
  }
}
