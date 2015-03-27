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
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by d.asadullin on 06.03.2015.
 */
public class SparkCount {

    public static void main(String[] args) throws IOException {
        SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount").setMaster("local[100]");
        final JavaSparkContext sc = new JavaSparkContext(sparkConf);
        final List<JavaRDD<String>> lines=new ArrayList<>();
        Files.walk(Paths.get("C:\\work\\ibs-git"), FileVisitOption.FOLLOW_LINKS)
                .filter(p->!p.toFile().isDirectory() && p.toString().endsWith("java"))
                .forEach(p->lines.add(sc.textFile(p.toAbsolutePath().toString())));
       // sc.
        JavaRDD<String> scLines=sc.union(lines.toArray(new JavaRDD[0])).filter(p -> !((String) p).isEmpty());
        System.out.println(scLines.count());
        sc.stop();

    }



}
