package storage;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.NavigableSet;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by d.asadullin on 22.07.2016.
 */
public class StorageTest {

    @Test
    public void test1() throws UnsupportedEncodingException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        byte[] data = new byte[1024 * 1024 * 20 + 200];
        Random random = new Random();
        random.nextBytes(data);
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(data, headers);
        ResponseEntity exchange = null;
        String url = null;
        try {
            exchange = restTemplate.exchange("http://localhost:8080/storage/put", HttpMethod.POST, entity, String.class);
            url = (String) exchange.getBody();
            System.out.println("url=" + url);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        }

        ResponseEntity<byte[]> forEntity = restTemplate.getForEntity("http://localhost:8080/storage/get?url={url}", byte[].class, url);
        System.out.println(forEntity.getBody().length);
        ResponseEntity<String> forEntity1 = restTemplate.getForEntity("http://localhost:8080/storage/meta?url={url}", String.class, url);
        System.out.println(forEntity1.getBody());
        ResponseEntity<byte[]> forEntity21 = restTemplate.getForEntity("http://localhost:8080/storage/getPart?url={url}&partid={part}", byte[].class, url, 0);
        System.out.println(forEntity21.getBody().length);
        ResponseEntity<byte[]> forEntity22 = restTemplate.getForEntity("http://localhost:8080/storage/getPart?url={url}&partid={part}", byte[].class, url, 1);
        System.out.println(forEntity22.getBody().length);
        ResponseEntity<byte[]> forEntity23 = restTemplate.getForEntity("http://localhost:8080/storage/getPart?url={url}&partid={part}", byte[].class, url, 2);
        System.out.println(forEntity23.getBody().length);
    }


    @Test
    public void test5() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type","application/octet-stream; charset=utf-8");
        headers.add("Content-Type", "application/json; charset=utf-8");
        byte[] data1 = new byte[1024 * 1024 * 10];
        byte[] data2 = new byte[1024 * 1024 * 10];
        byte[] data3 = new byte[200];
        Random random = new Random();
        random.nextBytes(data1);
        random.nextBytes(data2);
        random.nextBytes(data3);
        int count = data1.length + data2.length + data3.length;
        ByteBuffer bb = ByteBuffer.allocate(count);
        bb.put(data1);
        bb.put(data2);
        bb.put(data3);
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        instance.reset();
        byte[] digest = instance.digest(bb.array());

        bb.clear();
        bb = null;

        HttpEntity<byte[]> entity1 = new HttpEntity<byte[]>(data1, headers);
        HttpEntity<byte[]> entity2 = new HttpEntity<byte[]>(data2, headers);
        HttpEntity<byte[]> entity3 = new HttpEntity<byte[]>(data3, headers);
        ResponseEntity exchange = null;
        String url = null;
        try {
            exchange = restTemplate.exchange("http://localhost:8080/storage/putPart?hash={hash}&parts={parts}&size={size}&partid={partid}&returnUrl={returnUrl}", HttpMethod.POST, entity1, String.class, DatatypeConverter.printHexBinary(digest), 3, count, 0, true);
            url = (String) exchange.getBody();
            System.out.println("hash=" + url);
            exchange = restTemplate.exchange("http://localhost:8080/storage/putPart?hash={hash}&parts={parts}&size={size}&partid={partid}", HttpMethod.POST, entity2, String.class, DatatypeConverter.printHexBinary(digest), 3, count, 1);
            System.out.println("hash=" + (String) exchange.getBody());
            exchange = restTemplate.exchange("http://localhost:8080/storage/putPart?hash={hash}&parts={parts}&size={size}&partid={partid}", HttpMethod.POST, entity3, String.class, DatatypeConverter.printHexBinary(digest), 3, count, 2);
            System.out.println("hash=" + (String) exchange.getBody());
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        }

        try {
            ResponseEntity<byte[]> forEntity = restTemplate.getForEntity("http://localhost:8080/storage/getPart?hash={hash}&partid={partid}", byte[].class, DatatypeConverter.printHexBinary(digest), 0);
            System.out.println(forEntity.getBody().length);
            ResponseEntity<byte[]> forEntity1 = restTemplate.getForEntity("http://localhost:8080/storage/getPart?hash={hash}&partid={partid}", byte[].class, DatatypeConverter.printHexBinary(digest), 1);
            System.out.println(forEntity1.getBody().length);
            ResponseEntity<byte[]> forEntity2 = restTemplate.getForEntity("http://localhost:8080/storage/getPart?hash={hash}&partid={partid}", byte[].class, DatatypeConverter.printHexBinary(digest), 2);
            System.out.println(forEntity2.getBody().length);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            System.out.println(e.getStatusCode());
            System.out.println(e.getResponseBodyAsString());
        }
        ResponseEntity<String> forEntity1 = restTemplate.getForEntity("http://localhost:8080/storage/meta?url={url}", String.class, url);
        System.out.println(forEntity1.getBody());
//        ResponseEntity<String> forEntity1 = restTemplate.getForEntity("http://localhost:8080/storage/meta/{url}", String.class, url);
//        System.out.println(forEntity1.getBody());
//        ResponseEntity<byte[]> forEntity21 = restTemplate.getForEntity("http://localhost:8080/storage/part/{url}/{part}", byte[].class, url,0);
//        System.out.println(forEntity21.getBody().length);
//        ResponseEntity<byte[]> forEntity22 = restTemplate.getForEntity("http://localhost:8080/storage/part/{url}/{part}", byte[].class, url,1);
//        System.out.println(forEntity22.getBody().length);
//        ResponseEntity<byte[]> forEntity23 = restTemplate.getForEntity("http://localhost:8080/storage/part/{url}/{part}", byte[].class, url,2);
//        System.out.println(forEntity23.getBody().length);
    }

    private static byte[] getRandomName() {
        ByteBuffer bb = ByteBuffer.allocate(16);
        UUID random = UUID.randomUUID();
        bb.putLong(random.getLeastSignificantBits());
        bb.putLong(random.getMostSignificantBits());
        return bb.array();
    }

    @Test
    public void test2() throws UnsupportedEncodingException {
        ConcurrentSkipListSet<Long> set = new ConcurrentSkipListSet();
        for (Long i = 0l; i < 10; i++) {
            set.add(i);
        }
        set.remove(3l);
        System.out.println(set);
        NavigableSet<Long> longs = set.headSet(7l, true);
        System.out.println(longs);
        longs.clear();
        System.out.println(set);
    }

    int size = 10;

    private void putData(byte[] hash, String filename, byte[] data) {
        byte[][] parts = new byte[data.length / (size) + 1][];
        for (int i = 0; i < parts.length; i++) {
            parts[i] = Arrays.copyOfRange(data, i * size, i * size + Math.min((data.length - i * size), size));
        }
        System.out.println(Arrays.toString(data));
        for (int i = 0; i < parts.length; i++) {
            System.out.println(Arrays.toString(parts[i]));
        }
        ByteBuffer bb = ByteBuffer.wrap(data);
        //  bb.get()
//        while (data.length>size) {
//            BoundStatement boundStatement = new BoundStatement(insertData);
//            session.execute(boundStatement.bind(ByteBuffer.wrap(hash), new Date(), ByteBuffer.wrap(data),
//                    filename, 1, data.length, 0));
//        }
    }


    @Test
    public void test3() throws UnsupportedEncodingException {
        byte[] data = new byte[size * 3 + 1];
        Random random = new Random();
        random.nextBytes(data);
        putData(null, "aa", data);


    }

}
