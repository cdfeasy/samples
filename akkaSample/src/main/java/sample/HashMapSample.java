package sample;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dmitry on 29.03.2015.
 */
public class HashMapSample {
    private int dubIndex = 0;
    HashMap<Long,String> map=new HashMap<>(100000000);
    int i=0;
    public void consume(String s) {
        String key = s.substring(0, 11);
        String data = s.substring(11);
        map.put(Long.parseLong(key),data);
        i++;
        if(i%1000000==0){
            System.out.println(i);
        }
    }
    public void print() throws InterruptedException {

        System.out.println("-----------------");
        try (BufferedReader reader = new BufferedReader(new FileReader("d:/tmp/ids100000000.txt"))) {
            final AtomicInteger i = new AtomicInteger();
            reader.lines().forEach(s -> consume(s));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        System.out.println("-----------------");
        System.gc();
        System.out.println("-----------------");
        Random r=new Random();
        try (BufferedReader reader = new BufferedReader(new FileReader("d:/tmp/ids100000000.txt"))) {
            final AtomicInteger i = new AtomicInteger();
            reader.lines().filter(s->r.nextInt(1000)==1).forEach(s -> test(s));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        System.gc();
//        System.out.print("size:"+(2*byteBuffer1.capacity()/1024/1024)+"  dub="+dubIndex);
        Thread.sleep(100000);
    }




    public void test(String s){
        Long key = Long.parseLong(s.substring(0, 11));
        System.out.println(map.get(key) + "/" +s);
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        final HashMapSample count = new HashMapSample();
        // count.fill();
        count.print();



    }
}
