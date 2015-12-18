package zoo;

/**
 * Created by d.asadullin on 20.10.2015.
 */
public class Test {
    public static void main(String[] args){
       StringBuilder sb=new StringBuilder(Long.toString(234235423));

        for(int i=1;i<sb.length();i+=4){
            sb.insert(i,',');
        }
        System.out.println(sb.toString());
    }
}
