import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Created by dmitry on 18.12.2015.
 */
public class ParralelTest {
    @Test
    public void test() throws Exception {
        ExecutorService r = Executors.newFixedThreadPool(1);





        CompletableFuture<Integer> s1 = new CompletableFuture<>();
        CompletableFuture<Void> s2 = s1.
                thenApply((i)->{System.out.println("i+1");return i+1;}).
                thenCompose((i) -> {
                    System.out.println("i+2");
                    return CompletableFuture.supplyAsync(() -> i+2);
                }).thenAccept((i)->System.out.println("i="+i));
        s1.cancel(true);
       // System.out.println(s2.get());

       // bla.complete("400");
    }
}
