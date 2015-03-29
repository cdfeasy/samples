/****************************************************************************\
 File: BerkleyCache.java
 Date: 5/17/13
 Author: Dmitry Asadullin

 Copyright (c) 2012 i-free
 ****************************************************************************/
package db;

import com.sleepycat.je.*;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * BerkleyCache -  однопоточный дисковый кеш
 *
 * @author d.asadullin
 */
public class BerkleyCache {
    private String  path;
    private Transaction curTxn;
    private Environment env;
    private Database cacheDb;
    private DatabaseEntry key=new DatabaseEntry() ;
    private DatabaseEntry value=new DatabaseEntry() ;

    public BerkleyCache(String path){

        this.path=path;
    }

    private void init(boolean compact){
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);
        File dir=new File(path);
        dir.mkdirs();
        env = new Environment(dir, envConfig);

        Transaction txn = env.beginTransaction(null, null);
        DatabaseConfig dbConfig = new DatabaseConfig();
     //   dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);
     //   dbConfig.setSortedDuplicates(true);
        dbConfig.setDeferredWrite(true);
        // dbConfig.set
        // dbConfig.setDuplicateComparator(new Comparator<byte[]>() {
        //     @Override
        //     public int compare(byte[] o1, byte[] o2) {
        //         return o1.
        //     }
        // })
        cacheDb = env.openDatabase(curTxn,
                "cacheDb",
                dbConfig);
        txn.commit();
    }

    public void start() {
        init(true);
    }
    public void stop() {
        if(curTxn!=null && curTxn.isValid()) {
            curTxn.commit();
        }
        sync();
        cacheDb.close();
        cacheDb.getEnvironment().close();
    }

    public void push(Long entityKey, String entityValue) throws IOException {
        ByteBuffer bb=ByteBuffer.allocate(8);
        bb.putLong(entityKey);
        key.setData(bb.array());
        cacheDb.delete(curTxn,key);
        value.setData( entityValue.getBytes());
        cacheDb.put(curTxn,key,value);
    }

//    public void remove(UUID id) throws IOException {
//        ByteBuffer bb=ByteBuffer.allocate(16);
//        bb.putLong(id.getLeastSignificantBits());
//        bb.putLong(id.getMostSignificantBits());
//        key.setData(bb.array());
//        cacheDb.delete(curTxn,key);
//
//    }

//    private byte[] getBytes(MpMsgReportWrapper rep) throws IOException {
//        byte[] obj=msgpack.write(rep);
//        ByteBuffer buf=ByteBuffer.allocate(obj.length+11);
//        buf.putLong(System.currentTimeMillis());
//        buf.put((byte)(rep.isLogArrived() ? 1 : 0));
//        buf.put((byte)(rep.isFinalStateArrived()?1:0));
//        buf.put((byte)(rep.isReadyToBase()?1:0));
//        buf.put(obj);
//        return buf.array();
//    }
//
//    private MpMsgReportWrapper fromBytes(byte[] rep) throws IOException {
//        ByteBuffer buf=ByteBuffer.wrap(rep);
//        buf.getLong();
//        buf.get();
//        buf.get();
//        buf.get();
//        buf=buf.compact();
//        return msgpack.read(buf.array(), MpMsgReportWrapper.class);
//    }



    public String get(Long id) throws IOException {
        ByteBuffer bb=ByteBuffer.allocate(8);
        bb.putLong(id);
        key.setData(bb.array());
        OperationStatus status=cacheDb.get(curTxn,key,value,LockMode.DEFAULT);
        if(OperationStatus.NOTFOUND.equals(status)){
            return null;
        }else{
            //Decoder d=DecoderFactory.get().binaryDecoder(value.getData(),null);
            // MPMsgStateReport rep= gs.fromJson(new String(value.getData()), MPMsgStateReport.class);
            String rep=new String(value.getData());
            return rep;
        }
    }

//    public void getAll(messageListener listener) throws IOException {
//        Cursor cursor = cacheDb.openCursor(curTxn, null);
//        DatabaseEntry curkey=new DatabaseEntry() ;
//        DatabaseEntry curvalue=new DatabaseEntry() ;
//        while (cursor.getNext(curkey, curvalue, LockMode.DEFAULT) ==
//                OperationStatus.SUCCESS) {
//            try{
//              if(!listener.onMessage(fromBytes(curvalue.getData()))){
//                  break;
//              }
//            }catch (Exception ex){
//                ex.printStackTrace();
//                logger.error("error while get all cache objects",ex);
//            }
//        }
//        cursor.close();
//    }

//    public void getAllFlags(messageListenerFlags listener) throws IOException {
//        Cursor cursor = cacheDb.openCursor(curTxn, null);
//        DatabaseEntry curkey=new DatabaseEntry() ;
//        DatabaseEntry curvalue=new DatabaseEntry() ;
//        while (cursor.getNext(curkey, curvalue, LockMode.DEFAULT) ==
//                OperationStatus.SUCCESS) {
//            try{
//                ByteBuffer keyBuf=ByteBuffer.wrap(curkey.getData());
//                long sign=keyBuf.getLong();
//                long most=keyBuf.getLong();
//                UUID key=new UUID(most,sign);
//
//                ByteBuffer buf=ByteBuffer.wrap(curvalue.getData());
//                Date d=new Date(buf.getLong());
//                boolean isLogArrived= (buf.get()==1);
//                boolean isFinalStateArrived= (buf.get()==1);
//                boolean isReadyToBase= (buf.get()==1);
//
//                if(!listener.onMessage(key,d,isLogArrived,isFinalStateArrived,isReadyToBase)){
//                    break;
//                }
//            }catch (Exception ex){
//                logger.error("error while get all cache objects",ex);
//            }
//        }
//        cursor.close();
//    }

    public void openTransaction(){
        //if(curTxn!=null && curTxn.isValid()) {
        //    curTxn .commit();
        //}
        //curTxn = env.beginTransaction(null, null);
    }

    public void commitTransaction(){
        //if(curTxn!=null && curTxn.isValid()) {
        //    curTxn .commit();
        //}
        sync();
    }

    public void sync(){
        try{
            cacheDb.sync();
        }catch (Exception tx){
            tx.printStackTrace();
        }
    }
    private static Long msisdn(int i){
        return 70000000000l+i;

    }
    public static void main(String[] args) throws IOException, InterruptedException {
       // Long[] key=new Long[100000000];
        Random r=new Random();
//        for(long i=0;i<100000000;i++){
//            key[i]= r.nextInt(2100000000);
//        }

        BerkleyCache cache1=new BerkleyCache("c:\\tmp\\db1");
        cache1.start();
        long left=0;
        long d1=System.currentTimeMillis();
        for(long i=0;i<100000000;i++){
            Long msisdn=msisdn(r.nextInt(2100000000));
//            while(cache1.get(msisdn)!=null){
//                msisdn=msisdn(r.nextInt(2100000000));
//            }
            cache1.push(msisdn, UUID.randomUUID().toString());

            if(i%1000000==0){
                System.out.println(i);
            }
        }
        long d2=System.currentTimeMillis();
        int j=0;
        for(long i=0;i<100000000;i++){
            Long msisdn=msisdn(r.nextInt(1000000000));
            if(cache1.get(msisdn)!=null){
                j++;
            }
          //  System.out.println(j);
        }
        long d3=System.currentTimeMillis();
        System.out.print(String.format("%d push %d search %d found",d2-d1,d3-d2,j));
        cache1.stop();



    }

}
