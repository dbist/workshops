# HBase Spark Example
#### inspired by https://acadgild.com/blog/apache-spark-hbase
#### tested with:

	1. Hadoop 2.7.7
	2. Spark 2.3.3
	3. HBase 2.1.3

```
HBASE_PATH=`$HBASE_HOME/bin/hbase mapredcp`
$SPARK_HOME/bin/spark-shell --conf spark.driver.extraClassPath=/opt/hbase/hbase-2.1.3/lib/hbase-server-2.1.3.jar:/opt/hbase/hbase-2.1.3/lib/hbase-common-2.1.3.jar:/opt/hbase/hbase-2.1.3/lib/hbase-client-2.1.3.jar:/opt/hbase/hbase-2.1.3/lib/zookeeper-3.4.10.jar:/opt/hbase/hbase-2.1.3/lib/hbase-protocol-2.1.3.jar:/opt/hbase/hbase-2.1.3/lib/hbase-mapreduce-2.1.3.jar:/opt/hbase/hbase-2.1.3/conf:$HBASE_PATH
```

```
 import org.apache.spark.{SparkConf, SparkContext}
 import org.apache.hadoop.hbase.HBaseConfiguration
 import org.apache.hadoop.hbase.mapreduce.TableInputFormat
 import org.apache.hadoop.hbase.client.HBaseAdmin
 import org.apache.hadoop.hbase.util.Bytes
 import org.apache.hadoop.hbase.client.{Put,HTable}
 import org.apache.hadoop.hbase.client.ConnectionFactory;
 import org.apache.hadoop.hbase.client.TableDescriptorBuilder
 import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder

 val conf = HBaseConfiguration.create()
 val connection = ConnectionFactory.createConnection()
 val admin = connection.getAdmin()
 val tn = org.apache.hadoop.hbase.TableName.valueOf("spark" + System.currentTimeMillis())
 val td = TableDescriptorBuilder.newBuilder(tn).
  addColumnFamily(ColumnFamilyDescriptorBuilder.of("cf")).build();
 admin.createTable(td)

 val table = connection.getTable(tn)
 val cfBytes = Bytes.toBytes("cf")
 val qualifierBytes = Bytes.toBytes("q")
 for (x <- 1 to 100) {
  var p = new org.apache.hadoop.hbase.client.Put(Bytes.toBytes("row" + x))
  p.addColumn(cfBytes, qualifierBytes, Bytes.toBytes("value" + x))
  table.put(p)
 }

 conf.set(TableInputFormat.INPUT_TABLE, tn.getNameAsString())
 val rdd = sc.newAPIHadoopRDD(conf,
  classOf[TableInputFormat],
  classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
  classOf[org.apache.hadoop.hbase.client.Result])

// works until here
rdd.count()
```
```
// then errors

java.net.URISyntaxException: Illegal character in path at index 46: spark://hadoop.example.com:29958/classes/HBase Counters.class
	at java.net.URI$Parser.fail(URI.java:2848)
	at java.net.URI$Parser.checkChars(URI.java:3021)
	at java.net.URI$Parser.parseHierarchical(URI.java:3105)
	at java.net.URI$Parser.parse(URI.java:3053)
	at java.net.URI.<init>(URI.java:588)
	at org.apache.spark.rpc.netty.NettyRpcEnv.openChannel(NettyRpcEnv.scala:328)
	at org.apache.spark.repl.ExecutorClassLoader.org$apache$spark$repl$ExecutorClassLoader$$getClassFileInputStreamFromSparkRPC(ExecutorClassLoader.scala:95)
	at org.apache.spark.repl.ExecutorClassLoader$$anonfun$1.apply(ExecutorClassLoader.scala:62)
	at org.apache.spark.repl.ExecutorClassLoader$$anonfun$1.apply(ExecutorClassLoader.scala:62)
	at org.apache.spark.repl.ExecutorClassLoader.findClassLocally(ExecutorClassLoader.scala:167)
	at org.apache.spark.repl.ExecutorClassLoader.findClass(ExecutorClassLoader.scala:85)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
	at java.util.ResourceBundle$Control.newBundle(ResourceBundle.java:2649)
	at java.util.ResourceBundle.loadBundle(ResourceBundle.java:1510)
	at java.util.ResourceBundle.findBundle(ResourceBundle.java:1474)
	at java.util.ResourceBundle.findBundle(ResourceBundle.java:1428)
	at java.util.ResourceBundle.findBundle(ResourceBundle.java:1428)
	at java.util.ResourceBundle.getBundleImpl(ResourceBundle.java:1370)
	at java.util.ResourceBundle.getBundle(ResourceBundle.java:1091)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getBundle(ResourceBundles.java:37)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getValue(ResourceBundles.java:56)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getCounterGroupName(ResourceBundles.java:77)
	at org.apache.hadoop.mapreduce.counters.CounterGroupFactory.newGroup(CounterGroupFactory.java:94)
	at org.apache.hadoop.mapreduce.counters.AbstractCounters.getGroup(AbstractCounters.java:226)
	at org.apache.hadoop.mapreduce.counters.AbstractCounters.findCounter(AbstractCounters.java:153)
	at org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl$DummyReporter.getCounter(TaskAttemptContextImpl.java:110)
	at org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl.getCounter(TaskAttemptContextImpl.java:76)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.updateCounters(TableRecordReaderImpl.java:298)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.updateCounters(TableRecordReaderImpl.java:286)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.nextKeyValue(TableRecordReaderImpl.java:257)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReader.nextKeyValue(TableRecordReader.java:133)
	at org.apache.hadoop.hbase.mapreduce.TableInputFormatBase$1.nextKeyValue(TableInputFormatBase.java:220)
	at org.apache.spark.rdd.NewHadoopRDD$$anon$1.hasNext(NewHadoopRDD.scala:214)
	at org.apache.spark.InterruptibleIterator.hasNext(InterruptibleIterator.scala:37)
	at org.apache.spark.util.Utils$.getIteratorSize(Utils.scala:1837)
	at org.apache.spark.rdd.RDD$$anonfun$count$1.apply(RDD.scala:1168)
	at org.apache.spark.rdd.RDD$$anonfun$count$1.apply(RDD.scala:1168)
	at org.apache.spark.SparkContext$$anonfun$runJob$5.apply(SparkContext.scala:2074)
	at org.apache.spark.SparkContext$$anonfun$runJob$5.apply(SparkContext.scala:2074)
	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:87)
	at org.apache.spark.scheduler.Task.run(Task.scala:109)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:345)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
2019-03-04 21:06:02,703 ERROR [Executor task launch worker for task 0] repl.ExecutorClassLoader: Failed to check existence of class HBase Counters_en on REPL class server at spark://hadoop.example.com:29958/classes
java.net.URISyntaxException: Illegal character in path at index 46: spark://hadoop.example.com:29958/classes/HBase Counters_en.class
	at java.net.URI$Parser.fail(URI.java:2848)
	at java.net.URI$Parser.checkChars(URI.java:3021)
	at java.net.URI$Parser.parseHierarchical(URI.java:3105)
	at java.net.URI$Parser.parse(URI.java:3053)
	at java.net.URI.<init>(URI.java:588)
	at org.apache.spark.rpc.netty.NettyRpcEnv.openChannel(NettyRpcEnv.scala:328)
	at org.apache.spark.repl.ExecutorClassLoader.org$apache$spark$repl$ExecutorClassLoader$$getClassFileInputStreamFromSparkRPC(ExecutorClassLoader.scala:95)
	at org.apache.spark.repl.ExecutorClassLoader$$anonfun$1.apply(ExecutorClassLoader.scala:62)
	at org.apache.spark.repl.ExecutorClassLoader$$anonfun$1.apply(ExecutorClassLoader.scala:62)
	at org.apache.spark.repl.ExecutorClassLoader.findClassLocally(ExecutorClassLoader.scala:167)
	at org.apache.spark.repl.ExecutorClassLoader.findClass(ExecutorClassLoader.scala:85)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
	at java.util.ResourceBundle$Control.newBundle(ResourceBundle.java:2649)
	at java.util.ResourceBundle.loadBundle(ResourceBundle.java:1510)
	at java.util.ResourceBundle.findBundle(ResourceBundle.java:1474)
	at java.util.ResourceBundle.findBundle(ResourceBundle.java:1428)
	at java.util.ResourceBundle.getBundleImpl(ResourceBundle.java:1370)
	at java.util.ResourceBundle.getBundle(ResourceBundle.java:1091)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getBundle(ResourceBundles.java:37)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getValue(ResourceBundles.java:56)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getCounterGroupName(ResourceBundles.java:77)
	at org.apache.hadoop.mapreduce.counters.CounterGroupFactory.newGroup(CounterGroupFactory.java:94)
	at org.apache.hadoop.mapreduce.counters.AbstractCounters.getGroup(AbstractCounters.java:226)
	at org.apache.hadoop.mapreduce.counters.AbstractCounters.findCounter(AbstractCounters.java:153)
	at org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl$DummyReporter.getCounter(TaskAttemptContextImpl.java:110)
	at org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl.getCounter(TaskAttemptContextImpl.java:76)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.updateCounters(TableRecordReaderImpl.java:298)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.updateCounters(TableRecordReaderImpl.java:286)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.nextKeyValue(TableRecordReaderImpl.java:257)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReader.nextKeyValue(TableRecordReader.java:133)
	at org.apache.hadoop.hbase.mapreduce.TableInputFormatBase$1.nextKeyValue(TableInputFormatBase.java:220)
	at org.apache.spark.rdd.NewHadoopRDD$$anon$1.hasNext(NewHadoopRDD.scala:214)
	at org.apache.spark.InterruptibleIterator.hasNext(InterruptibleIterator.scala:37)
	at org.apache.spark.util.Utils$.getIteratorSize(Utils.scala:1837)
	at org.apache.spark.rdd.RDD$$anonfun$count$1.apply(RDD.scala:1168)
	at org.apache.spark.rdd.RDD$$anonfun$count$1.apply(RDD.scala:1168)
	at org.apache.spark.SparkContext$$anonfun$runJob$5.apply(SparkContext.scala:2074)
	at org.apache.spark.SparkContext$$anonfun$runJob$5.apply(SparkContext.scala:2074)
	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:87)
	at org.apache.spark.scheduler.Task.run(Task.scala:109)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:345)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
2019-03-04 21:06:02,708 ERROR [Executor task launch worker for task 0] repl.ExecutorClassLoader: Failed to check existence of class HBase Counters_en_US on REPL class server at spark://hadoop.example.com:29958/classes
java.net.URISyntaxException: Illegal character in path at index 46: spark://hadoop.example.com:29958/classes/HBase Counters_en_US.class
	at java.net.URI$Parser.fail(URI.java:2848)
	at java.net.URI$Parser.checkChars(URI.java:3021)
	at java.net.URI$Parser.parseHierarchical(URI.java:3105)
	at java.net.URI$Parser.parse(URI.java:3053)
	at java.net.URI.<init>(URI.java:588)
	at org.apache.spark.rpc.netty.NettyRpcEnv.openChannel(NettyRpcEnv.scala:328)
	at org.apache.spark.repl.ExecutorClassLoader.org$apache$spark$repl$ExecutorClassLoader$$getClassFileInputStreamFromSparkRPC(ExecutorClassLoader.scala:95)
	at org.apache.spark.repl.ExecutorClassLoader$$anonfun$1.apply(ExecutorClassLoader.scala:62)
	at org.apache.spark.repl.ExecutorClassLoader$$anonfun$1.apply(ExecutorClassLoader.scala:62)
	at org.apache.spark.repl.ExecutorClassLoader.findClassLocally(ExecutorClassLoader.scala:167)
	at org.apache.spark.repl.ExecutorClassLoader.findClass(ExecutorClassLoader.scala:85)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
	at java.util.ResourceBundle$Control.newBundle(ResourceBundle.java:2649)
	at java.util.ResourceBundle.loadBundle(ResourceBundle.java:1510)
	at java.util.ResourceBundle.findBundle(ResourceBundle.java:1474)
	at java.util.ResourceBundle.getBundleImpl(ResourceBundle.java:1370)
	at java.util.ResourceBundle.getBundle(ResourceBundle.java:1091)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getBundle(ResourceBundles.java:37)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getValue(ResourceBundles.java:56)
	at org.apache.hadoop.mapreduce.util.ResourceBundles.getCounterGroupName(ResourceBundles.java:77)
	at org.apache.hadoop.mapreduce.counters.CounterGroupFactory.newGroup(CounterGroupFactory.java:94)
	at org.apache.hadoop.mapreduce.counters.AbstractCounters.getGroup(AbstractCounters.java:226)
	at org.apache.hadoop.mapreduce.counters.AbstractCounters.findCounter(AbstractCounters.java:153)
	at org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl$DummyReporter.getCounter(TaskAttemptContextImpl.java:110)
	at org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl.getCounter(TaskAttemptContextImpl.java:76)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.updateCounters(TableRecordReaderImpl.java:298)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.updateCounters(TableRecordReaderImpl.java:286)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReaderImpl.nextKeyValue(TableRecordReaderImpl.java:257)
	at org.apache.hadoop.hbase.mapreduce.TableRecordReader.nextKeyValue(TableRecordReader.java:133)
	at org.apache.hadoop.hbase.mapreduce.TableInputFormatBase$1.nextKeyValue(TableInputFormatBase.java:220)
	at org.apache.spark.rdd.NewHadoopRDD$$anon$1.hasNext(NewHadoopRDD.scala:214)
	at org.apache.spark.InterruptibleIterator.hasNext(InterruptibleIterator.scala:37)
	at org.apache.spark.util.Utils$.getIteratorSize(Utils.scala:1837)
	at org.apache.spark.rdd.RDD$$anonfun$count$1.apply(RDD.scala:1168)
	at org.apache.spark.rdd.RDD$$anonfun$count$1.apply(RDD.scala:1168)
	at org.apache.spark.SparkContext$$anonfun$runJob$5.apply(SparkContext.scala:2074)
	at org.apache.spark.SparkContext$$anonfun$runJob$5.apply(SparkContext.scala:2074)
	at org.apache.spark.scheduler.ResultTask.runTask(ResultTask.scala:87)
	at org.apache.spark.scheduler.Task.run(Task.scala:109)
	at org.apache.spark.executor.Executor$TaskRunner.run(Executor.scala:345)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
```

```
/*

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

 */

 ```
