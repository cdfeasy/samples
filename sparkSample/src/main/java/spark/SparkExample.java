package spark;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.rdd.RDD;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by d.asadullin on 05.03.2015.
 */
public class SparkExample {
    private static final Pattern SPACE = Pattern.compile(" ");
    public static void main(String[] args){
        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount").setMaster("local[100]");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        JavaRDD<String> lines = sc.textFile("c:/tmp/big.txt",1);
        JavaRDD<String> words = lines.flatMap(s->Arrays.asList(SPACE.split(s)));
        JavaPairRDD<String, Integer> ones = words.mapToPair(s->new Tuple2<String, Integer>(s,1));
        JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);
        List<Tuple2<String, Integer>> output = counts.collect();
        for (Tuple2<?,?> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }
        sc.stop();

    }

}
