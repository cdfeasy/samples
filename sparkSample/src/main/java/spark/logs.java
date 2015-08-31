package spark;

import com.jcraft.jsch.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by d.asadullin on 10.03.2015.
 */
public class logs {

    public static void main(String[] args){

        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount").setMaster("local[100]");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
     //   sc.textFile()
//        JavaRDD<String> lines = sc.textFile("c:/tmp/big.txt",1);
//        JavaRDD<String> words = lines.flatMap(s-> Arrays.asList(SPACE.split(s)));
//        JavaPairRDD<String, Integer> ones = words.mapToPair(s->new Tuple2<String, Integer>(s,1));
//        JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);
//        List<Tuple2<String, Integer>> output = counts.collect();
//        for (Tuple2<?,?> tuple : output) {
//            System.out.println(tuple._1() + ": " + tuple._2());
//        }
//        sc.stop();

        ScpFrom from=new ScpFrom();
        from.main(new String[]{"d.asadullin@vm-debian-40.i-free.dev:/opt/ibs-adapters-container-server/logs/otp-plugin-bundle.log","c:/tmp/oto.log"});


    }

    static class ScpFrom{
        public static void main(String[] arg){
            if(arg.length!=2){
                System.err.println("usage: java ScpFrom user@remotehost:file1 file2");
                System.exit(-1);
            }

            FileOutputStream fos=null;
            try{

                String user=arg[0].substring(0, arg[0].indexOf('@'));
                arg[0]=arg[0].substring(arg[0].indexOf('@')+1);
                String host=arg[0].substring(0, arg[0].indexOf(':'));
                String rfile=arg[0].substring(arg[0].indexOf(':')+1);
                String lfile=arg[1];

                String prefix=null;
                if(new File(lfile).isDirectory()){
                    prefix=lfile+File.separator;
                }

                JSch jsch=new JSch();
                Session session=jsch.getSession(user, host, 22);

                // username and password will be given via UserInfo interface.
                UserInfo ui=new MyUserInfo("qwerty");
                session.setUserInfo(ui);
                session.connect();

                // exec 'scp -f rfile' remotely
                String command="scp -f "+rfile;
                Channel channel=session.openChannel("exec");
                ((ChannelExec)channel).setCommand(command);

                // get I/O streams for remote scp
                OutputStream out=channel.getOutputStream();
                InputStream in=channel.getInputStream();

                channel.connect();

                byte[] buf=new byte[1024];

                // send '\0'
                buf[0]=0; out.write(buf, 0, 1); out.flush();

                while(true){
                    int c=checkAck(in);
                    if(c!='C'){
                        break;
                    }

                    // read '0644 '
                    in.read(buf, 0, 5);

                    long filesize=0L;
                    while(true){
                        if(in.read(buf, 0, 1)<0){
                            // error
                            break;
                        }
                        if(buf[0]==' ')break;
                        filesize=filesize*10L+(long)(buf[0]-'0');
                    }

                    String file=null;
                    for(int i=0;;i++){
                        in.read(buf, i, 1);
                        if(buf[i]==(byte)0x0a){
                            file=new String(buf, 0, i);
                            break;
                        }
                    }

                    //System.out.println("filesize="+filesize+", file="+file);

                    // send '\0'
                    buf[0]=0; out.write(buf, 0, 1); out.flush();

                    // read a content of lfile
                    fos=new FileOutputStream(prefix==null ? lfile : prefix+file);
                    int foo;
                    while(true){
                        if(buf.length<filesize) foo=buf.length;
                        else foo=(int)filesize;
                        foo=in.read(buf, 0, foo);
                        if(foo<0){
                            // error
                            break;
                        }
                        fos.write(buf, 0, foo);
                        filesize-=foo;
                        if(filesize==0L) break;
                    }
                    fos.close();
                    fos=null;

                    if(checkAck(in)!=0){
                        System.exit(0);
                    }

                    // send '\0'
                    buf[0]=0; out.write(buf, 0, 1); out.flush();
                }

                session.disconnect();

                System.exit(0);
            }
            catch(Exception e){
                System.out.println(e);
                try{if(fos!=null)fos.close();}catch(Exception ee){}
            }
        }

        static int checkAck(InputStream in) throws IOException {
            int b=in.read();
            // b may be 0 for success,
            //          1 for error,
            //          2 for fatal error,
            //          -1
            if(b==0) return b;
            if(b==-1) return b;

            if(b==1 || b==2){
                StringBuffer sb=new StringBuffer();
                int c;
                do {
                    c=in.read();
                    sb.append((char)c);
                }
                while(c!='\n');
                if(b==1){ // error
                    System.out.print(sb.toString());
                }
                if(b==2){ // fatal error
                    System.out.print(sb.toString());
                }
            }
            return b;
        }

        public static class MyUserInfo implements UserInfo{
            public String getPassword(){ return passwd; }
            public boolean promptYesNo(String str){
              return true;
            }

            public MyUserInfo(String passwd) {
                this.passwd = passwd;
            }

            String passwd;
            public String getPassphrase(){ return null; }
            public boolean promptPassphrase(String message){ return true; }
            public boolean promptPassword(String message){
               return true;
            }
            public void showMessage(String message){
            }
        }
    }

}
