package org.apache.spark.examples;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
 
import com.esotericsoftware.minlog.Log;
 
public class SimpleSparkJob {
 
    public static void main(String[] args) throws IOException {
 
        SparkConf conf = new SparkConf()
           .setAppName("Simple print something")
           .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
 
        try (JavaSparkContext context = new JavaSparkContext(conf)) {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            
            JavaRDD<Integer> rdds = context.parallelize(list, 10);
            
            rdds.foreach(new VoidFunction<Integer>() {
                
                private static final long serialVersionUID = 1L;
                
                @Override
                public void call(Integer arg0) throws Exception {
                    Log.info("Value is " + arg0);
                    Configuration conf = new Configuration();
                    Path filenamePath = new Path("/user/joe_analyst" + System.nanoTime() + ".xml");
                    FileSystem fileSystem = FileSystem.get(conf);
                    try (FSDataOutputStream fdos = fileSystem.create(filenamePath)) {
                        fdos.writeUTF(arg0 + "");
                    }
                }
            });
        }
    }
}