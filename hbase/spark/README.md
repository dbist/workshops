# HBase Spark Example
#### inspired by https://acadgild.com/blog/apache-spark-hbase
#### tested with:

	1. Hadoop 2.7.7
	2. Spark 2.4.0
	3. HBase 1.5.0

```
HBASE_PATH=`$HBASE_HOME/bin/hbase classpath`
$SPARK_HOME/bin/spark-shell --driver-class-path $HBASE_PATH
```

```
  import org.apache.spark.{SparkConf, SparkContext}
 import org.apache.hadoop.hbase.HBaseConfiguration
 import org.apache.hadoop.hbase.mapreduce.TableInputFormat
 import org.apache.hadoop.hbase.client.HBaseAdmin
 import org.apache.hadoop.hbase.{HTableDescriptor,HColumnDescriptor}
 import org.apache.hadoop.hbase.util.Bytes
 import org.apache.hadoop.hbase.client.{Put,HTable}

 val conf = HBaseConfiguration.create()
 val tablename = "t1"
 conf.set(TableInputFormat.INPUT_TABLE,tablename)
 val admin = new HBaseAdmin(conf)

 if(!admin.isTableAvailable(tablename)){
	 print("creating table:"+tablename+"\t")
	 val tableDescription = new HTableDescriptor(tablename)
	 tableDescription.addFamily(new HColumnDescriptor("cf".getBytes()));
	 admin.createTable(tableDescription);
 } else {
	 print("table already exists")
 }

 val table = new HTable(conf,tablename);
 for(x <- 1 to 10){
	 var p = new Put(new String("row" + x).getBytes());
	 p.add("cf".getBytes(),"column1".getBytes(),new String("value" + x).getBytes());
	 table.put(p);
 }

 val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat],
	 classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
	 classOf[org.apache.hadoop.hbase.client.Result])

 val count = hBaseRDD.count()
 count.show(5)
 ```
