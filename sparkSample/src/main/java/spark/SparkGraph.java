package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.Graph;

/**
 * Created by d.asadullin on 10.03.2015.
 */
public class SparkGraph {
    public static void main(String[] args){
        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount").setMaster("local[100]");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        //List<>;
      //  sc.parallelize()
        Graph g=null;
        //g.

       // sc.stop();

    }
}
