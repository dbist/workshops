//Convert to Class in order to run with spark-submit
class Write1mRowsSpark
{
 import org.apache.spark.{SparkConf, SparkContext}
 import org.apache.hadoop.hbase.HBaseConfiguration
 import org.apache.hadoop.hbase.mapreduce.TableInputFormat
 import org.apache.hadoop.hbase.util.Bytes
 import org.apache.hadoop.hbase.client.Put
 import org.apache.hadoop.hbase.client.ConnectionFactory;
 import org.apache.hadoop.hbase.client.TableDescriptorBuilder
 import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder
 import org.apache.hadoop.hbase.TableName
 import scala.collection.mutable.ListBuffer
 import scala.collection.JavaConverters._

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
 val rows = 1000000
 var puts = new ListBuffer[Put]()
 for (x <- 1 to rows) {
  var p = new org.apache.hadoop.hbase.client.Put(Bytes.toBytes("row" + x))
  p.addColumn(cfBytes, qualifierBytes, Bytes.toBytes("value" + x))
  puts += p
 }

 val putsList = puts.toList
 table.put(putsList.asJava)

 conf.set(TableInputFormat.INPUT_TABLE, tn.getNameAsString())
 val rdd = sc.newAPIHadoopRDD(conf,
  classOf[TableInputFormat],
  classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
  classOf[org.apache.hadoop.hbase.client.Result])

 rdd.count()

 // view the first 5 records
 rdd.map(_.toString()).take(5)

 // convert ImmutableBytesWritable to String
 rdd.map(_.toString()).collect().foreach(println)

 // list tables shows truncated list of tables
 val tables = admin.listTables

 // clean up
 admin.disableTable(tn)
 admin.deleteTable(tn)

 for ( table <- tables) {
  println(table);
 }

// admin operations
admin.disableTable(TableName.valueOf("spark1551739989358"))
admin.deleteTable(TableName.valueOf("spark1551739989358"))

//admin.deleteTable(table.getTableName())
}
