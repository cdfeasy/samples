package spark;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by d.asadullin on 05.03.2015.
 */
public class Fill {
    public static void main(String[] args) throws IOException {
        List<String> names=new ArrayList<>();
        Random r=new Random();
        for(int i=0;i<1000;i++){
           names.add("randomword"+Integer.toString(i)+Integer.toString(r.nextInt())+" ");
        }

        try(FileWriter writer=new FileWriter(new File("c:/tmp/big.txt"));){
            for(int i=0;i<1000000;i++){
                writer.write(names.get(r.nextInt(1000)));
                if(r.nextInt(10)%9==0){
                    writer.write("\n");
                }
            }
        }

    }
}
