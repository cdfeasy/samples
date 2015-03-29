package sample;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by d.asadullin on 27.03.2015.
 */
public class Count {
    private int dubIndex = 0;
    private int bufferSize = 0;
    private int packetSize = 34;
    private int cnt = 100000000;
    final ByteBuffer byteBuffer1 = ByteBuffer.allocate((cnt/2)*(packetSize + 4));
    final ByteBuffer byteBuffer2 = ByteBuffer.allocate((cnt/2)*(packetSize + 4));
    final ByteBuffer dubBuffer1 = ByteBuffer.allocate(((cnt/2))*(packetSize + 4));
    final ByteBuffer dubBuffer2 = ByteBuffer.allocate(((cnt/2))*(packetSize + 4));

    private String msisdn(int i) {
        return "9" + Long.toString(1000000000l + i);
    }

    private Random r = new Random();
    private ByteBuffer getBuffer(int index){
        if(index>=cnt/2){
            return byteBuffer2;
        }else{
            return byteBuffer1;
        }
    }

    private ByteBuffer getDub(int index){
        if(index>=cnt/2){
            return dubBuffer2;
        }else{
            return dubBuffer1;
        }
    }

    private byte[] genRandom(String msisdn) {
        ByteBuffer bb = ByteBuffer.allocate(packetSize + 1);
        bb.put(msisdn.getBytes());
        while (bb.position() < bb.capacity() - 1) {
            bb.put((byte) ('a' + r.nextInt(25)));
        }

        bb.put((byte) '\n');
        return bb.array();

    }

    public void fill() throws IOException {
        Random random = new Random();
        FileOutputStream fos = new FileOutputStream("d:/tmp/ids100000000.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        for (int i = 0; i < cnt; i++) {
            bos.write(genRandom(msisdn(random.nextInt(999999999))));
        }
        bos.flush();
        bos.close();
        fos.close();
    }

    private int getPlace(int index) {
        if(index>=cnt/2){
            return (index-(cnt/2)) * (packetSize + 4);
        } else {
            return index * (packetSize + 4);
        }
    }

    public void put(String s) {
        String key = s.substring(0, 11);
        String data = s.substring(11);
      //  System.out.println(key + "/" + data);
        int hash = Math.abs(key.hashCode());
        int index = hash % (cnt);
        ByteBuffer buff=getBuffer(index);
        buff.position(getPlace(index));
        byte[] msisdn = new byte[11];
        buff.get(msisdn);
        if (msisdn[0] == 0) {
            buff.position(getPlace(index));
            buff.put(key.getBytes());
            buff.put(data.getBytes());
        } else {
            if (new String(msisdn).equals(key)) {
              //  System.out.println("rewrite" + key);
                buff.position(getPlace(index));
                buff.put(key.getBytes());
                buff.put(data.getBytes());
            } else {
               // System.out.println("dub " + key + "/" + new String(msisdn) + "/new index" + dubIndex);
                buff.position(getPlace(index) +packetSize);
                buff.putInt(dubIndex);
                ByteBuffer dub=getDub(dubIndex);
                dub.position(getPlace(dubIndex));
                dub.put(key.getBytes());
                dub.put(data.getBytes());
                dubIndex++;
            }
        }
    }

    public void consume(String s) {
        put(s);
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
        System.out.println("-----------------");
        Random r=new Random();
        try (BufferedReader reader = new BufferedReader(new FileReader("d:/tmp/ids100000000.txt"))) {
            final AtomicInteger i = new AtomicInteger();
            reader.lines().filter(s->r.nextInt(1000)==1).forEach(s -> test(s));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        System.gc();
        System.out.print("size:"+(2*byteBuffer1.capacity()/1024/1024)+"  dub="+dubIndex);
        Thread.sleep(1000000);
    }

    public String get(String key) {
        int hash = Math.abs(key.hashCode());
        int index = hash % (cnt);
        ByteBuffer buff=getBuffer(index);
        buff.position(getPlace(index));
        byte[] msisdn = new byte[11];
        buff.get(msisdn);
        if (msisdn[0] == 0) {
            return "";
        } else {
            if (new String(msisdn).equals(key)) {
                byte[] data = new byte[packetSize - 11];
                buff.get(data);
                return key+new String(data);
            } else {
                buff.position(getPlace(index) + packetSize);
                int dub = buff.getInt();
                ByteBuffer dubB=getDub(dub);
              //  System.out.println("dub"+dub+" key "+key);
                dubB.position(getPlace(dub));
                byte[] data = new byte[packetSize];

                dubB.get(data);
                return new String(data);
            }
        }
    }

    public void test(String s){
        String key = s.substring(0, 11);
        System.out.println(get(key) + "/" +s);
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        final Count count = new Count();
       // count.fill();
        count.print();



    }
}
