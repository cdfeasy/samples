package storage.services;

import com.datastax.driver.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import storage.db.KFile;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Created by d.asadullin on 31.08.2016.
 */
@Component
@Scope("singleton")
public class CassandraClient {
    final Logger logger = LoggerFactory.getLogger(StorageService.class);
    Session session;
    @Autowired
    @Value("${cassandra.host}")
    String host;
    @Autowired
    @Value("${cassandra.keyspace}")
    String keyspace;

    private PreparedStatement insertData;
    private PreparedStatement insertHash;
    private PreparedStatement insertParts;
    private PreparedStatement insertCall;
    private PreparedStatement getData;
    private PreparedStatement getMeta;
    private PreparedStatement getPart;

    public void start() {
        Cluster cluster = null;
        cluster = Cluster.builder()                                                    // (1)
                .addContactPoint(host)
                .build();
        session = cluster.connect(keyspace);
        insertData = session.prepare(
                "INSERT INTO data (hash, created, data, filename, parts,size,type)"
                        + "VALUES (?,?,?,?,?,?,?);");

        insertParts = session.prepare(
                "INSERT INTO data_parts (hash, part_id, data)"
                        + "VALUES (?,?,?);");

        insertHash = session.prepare(
                "BEGIN BATCH" +
                        "   INSERT INTO url_hash (url, created, hash) VALUES (?, ?, ?);" +
                        "   INSERT INTO hash_urls (hash, url, created) VALUES (?, ?, ?);" +
                        "APPLY BATCH"
        );
        insertCall = session.prepare(
                "INSERT INTO calls (url, created,env) VALUES (?, ?, ?);"
        );
        getData = session.prepare(
                "select * from data where hash=?"
        );
        getMeta = session.prepare(
                "select hash, created,filename, parts,size,type from data where hash=?"
        );
        getPart = session.prepare(
                "select * from data_parts where hash=? and part_id=?"
        );

    }

    public static int size = 1024 * 1024*10;

    private void putData(byte[] hash, String filename, byte[] data, Integer partId,Integer dataSize,Integer parts) {
        BoundStatement boundStatement = new BoundStatement(insertData);
        if(partId==0) {
            session.execute(boundStatement.bind(ByteBuffer.wrap(hash), new Date(), ByteBuffer.wrap(data),
                    filename, parts, dataSize, 0));
        } else{
            boundStatement = new BoundStatement(insertParts);
            session.execute(boundStatement.bind(ByteBuffer.wrap(hash), partId, ByteBuffer.wrap(data)));
        }
    }


    private void putData(byte[] hash, String filename, byte[] data) {
        byte[][] parts = new byte[data.length / (size) + 1][];
        for (int i = 0; i < parts.length; i++) {
            parts[i] = Arrays.copyOfRange(data, i * size, i * size + Math.min((data.length - i * size), size));
        }
        putData(hash,filename,parts[0],0,data.length,parts.length);
        for (int i = 1; i < parts.length; i++) {
            putData(hash,filename,parts[i],i,data.length,parts.length);
        }
    }

    private byte[] getData(byte[] hash,Integer partId) {
        ResultSet data = session.execute(getData.bind(ByteBuffer.wrap(hash)));
        if (data.isExhausted()) {
            return null;
        } else {
            if(partId==null) {
                Row row = data.one();
                ByteBuffer array = ByteBuffer.allocate(row.getInt("size"));
                array.put(row.getBytes("data").array());
                if (row.getInt("parts") > 1) {
                    for (int i = 1; i < row.getInt("parts"); i++) {
                        ResultSet part = session.execute(getPart.bind(ByteBuffer.wrap(hash), i));
                        if (part.isExhausted()) {
                            return array.array();
                        } else {
                            array.put(part.one().getBytes("data").array());
                        }
                    }
                }
                return array.array();
            }else {
                Row row = data.one();
                if(partId==0){
                   return row.getBytes("data").array();
                }
                ResultSet part = session.execute(getPart.bind(ByteBuffer.wrap(hash), partId));
                if (part.isExhausted()) {
                    return null;
                } else {
                    return part.one().getBytes("data").array();
                }
            }
        }
    }
    private String getMeta(byte[] hash) {
        ResultSet data = session.execute(getMeta.bind(ByteBuffer.wrap(hash)));
        if (data.isExhausted()) {
            return null;
        } else {
            Row row=data.one();
            ByteBuffer array=ByteBuffer.allocate(row.getInt("size"));
            KFile file=new KFile(hash,row.getString("filename"),new byte[]{},row.getInt("parts"),row.getInt("type"),row.getInt("size"),row.getTimestamp("created"));
            return file.toString();
        }
    }

    public String put(byte[] hash, String filename, byte[] data)  {
        ResultSet rs = session.execute("select hash from data where hash=0x" + DatatypeConverter.printHexBinary(hash));
        if (rs.isExhausted()) {
            putData(hash, filename, data);
            logger.info("put data with hash=" + DatatypeConverter.printHexBinary(hash));
        }
        byte[] url = getRandomName();
        session.execute(insertHash.bind(ByteBuffer.wrap(url), new Date(), ByteBuffer.wrap(hash), ByteBuffer.wrap(hash), ByteBuffer.wrap(url), new Date()));
        return Base58.encode(url);
    }

    public String put(byte[] hash, String filename, byte[] data, Integer partId,Integer dataSize,Integer parts,boolean returnUrl) {
       putData(hash, filename, data,partId,dataSize,parts);
        if(returnUrl){
            byte[] url = getRandomName();
            session.execute(insertHash.bind(ByteBuffer.wrap(url), new Date(), ByteBuffer.wrap(hash), ByteBuffer.wrap(hash), ByteBuffer.wrap(url), new Date()));
            return Base58.encode(url);
        } else{
            logger.info("put data with hash={} partid={} datasize={} parts={}",DatatypeConverter.printHexBinary(hash),partId,dataSize,parts);
            return DatatypeConverter.printHexBinary(hash);
        }
      }

    public byte[] get(String url,String part) throws NoSuchAlgorithmException {
        byte[] urlBytes = Base58.decode(url);
        ResultSet rs = session.execute("select * from url_hash where url=0x" + DatatypeConverter.printHexBinary(urlBytes));
        if (rs.isExhausted()) {
            return null;
        }
        session.execute(insertCall.bind(ByteBuffer.wrap(urlBytes), new Date(), ""));
        ByteBuffer bb = rs.one().getBytes("hash");
        return get(bb.array(),part);
    }

    public byte[] get(byte[] hash,String part) throws NoSuchAlgorithmException {
        Integer parts=null;
        if(part!=null){
            try {
                parts = Integer.parseInt(part);
            }catch (NumberFormatException ex) {
            }
        }

        return getData(hash,parts);
    }

    public String getMeta(String url) throws NoSuchAlgorithmException {
        byte[] urlBytes = Base58.decode(url);
        ResultSet rs = session.execute("select * from url_hash where url=0x" + DatatypeConverter.printHexBinary(urlBytes));
        if (rs.isExhausted()) {
            return null;
        }
        ByteBuffer bb = rs.one().getBytes("hash");
        return getMeta(bb.array());
    }

    private byte[] getRandomName() {
        ByteBuffer bb = ByteBuffer.allocate(16);
        UUID random = UUID.randomUUID();
        bb.putLong(random.getLeastSignificantBits());
        bb.putLong(random.getMostSignificantBits());
        return bb.array();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Cluster cluster = null;
        cluster = Cluster.builder()                                                    // (1)
                .addContactPoint("test-b2b-dev-02.g01.i-free.ru")
                .build();
        Session session = cluster.connect("test1");

        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        instance.reset();
        byte[] digest = instance.digest(UUID.randomUUID().toString().getBytes());
        //  String hash1=new BigInteger(1, digest).toString(16);
        PreparedStatement statement = session.prepare(
                "INSERT INTO data (hash, created, data, filename, parts,size,type)"
                        + "VALUES (?,?,?,?,?,?,?);");

        BoundStatement boundStatement = new BoundStatement(statement);

        session.execute(boundStatement.bind(ByteBuffer.wrap(digest), new Date(), ByteBuffer.wrap("bla".getBytes()),
                "blabla.txt", 0, 3, 0));

        session.execute(boundStatement.bind(ByteBuffer.wrap(digest), new Date(), ByteBuffer.wrap("bla".getBytes()),
                "blabla.txt", 0, 3, 0));


//        Statement select = QueryBuilder.select().all().from("data")
//                .where(QueryBuilder.eq("hash", ByteBuffer.wrap(digest)));
//        ResultSet execute = session.execute(select);

        ResultSet execute1 = session.execute("select * from data where hash=0x" + DatatypeConverter.printHexBinary(digest));
        for (Row row : execute1) {
            System.out.format("%s %d \n", row.getString("filename"),
                    row.getInt("parts"));
        }
        PreparedStatement ps1 = session.prepare(
                "select * from data where hash=?;"
        );
        ResultSet execute2 = session.execute(ps1.bind(ByteBuffer.wrap(digest)));
        for (Row row : execute2) {
            System.out.format("%s %d \n", row.getString("filename"),
                    row.getInt("parts"));
        }

    }


}
