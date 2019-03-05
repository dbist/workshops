# HBase Spark Example
#### inspired by https://acadgild.com/blog/apache-spark-hbase
#### tested with:

	1. Hadoop 2.7.7
	2. Spark 2.3.3
	3. HBase 2.1.3

Requires: [HBASE-21452](https://issues.apache.org/jira/browse/HBASE-21452)

```
// old
HBASE_PATH=`$HBASE_HOME/bin/hbase mapredcp`
$SPARK_HOME/bin/spark-shell --conf spark.driver.extraClassPath=/opt/hbase/hbase-2.1.3/lib/hbase-server-2.1.3.jar:/opt/hbase/hbase-2.1.3/lib/hbase-common-2.1.3.jar:/opt/hbase/hbase-2.1.3/lib/hbase-client-2.1.3.jar:/opt/hbase/hbase-2.1.3/lib/zookeeper-3.4.10.jar:/opt/hbase/hbase-2.1.3/lib/hbase-protocol-2.1.3.jar:/opt/hbase/hbase-2.1.3/conf:$HBASE_PATH
```

```
// new
$SPARK_HOME/bin/spark-shell --conf spark.driver.extraClassPath=/opt/hbase/hbase-2.1.3/conf:`hbase mapredcp`
```

```
 import org.apache.spark.{SparkConf, SparkContext}
 import org.apache.hadoop.hbase.HBaseConfiguration
 import org.apache.hadoop.hbase.mapreduce.TableInputFormat
 import org.apache.hadoop.hbase.util.Bytes
 import org.apache.hadoop.hbase.client.Put
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

 rdd.count()

 // list tables shows truncated list of tables
 val tables = admin.listTables

 for ( table <- tables) {
  println(table);
 }

// admin operations
import org.apache.hadoop.hbase.TableName

admin.disableTable(TableName.valueOf("spark1551739989358"))
admin.deleteTable(TableName.valueOf(spark1551739989358))

//admin.deleteTable(table.getTableName())
```
