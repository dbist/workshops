// https://medium.com/hashmapinc/3-steps-for-bulk-loading-1m-records-in-20-seconds-into-apache-phoenix-99b77ad87387

import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.phoenix.schema.types.PLong
import org.apache.hadoop.fs.Path


val TableSchema = StructType(Array(
  StructField("U_ID", StringType, false),
  StructField("TIME_IN_ISO", StringType, false),
  StructField("VAL", StringType, false),
  StructField("BATCH_ID", StringType, true),
  StructField("JOB_ID", StringType, true),
  StructField("POS_ID", StringType, true)
))

val csvPath = "hdfs://hadoop.example.com:9000/user/vagrant/sample.csv"
val df = spark.read.format("com.databricks.spark.csv").option("header", "false").schema(TableSchema).load(csvPath)

val cols = df.columns.sorted
val colSortedDf = df.select(cols.map(x => col(x)): _*)
val valCols = cols.filterNot(x => x.equals("U_ID"))

val pdd = colSortedDf.map(row => {
  (row(0).toString, (row(1).toString, row(2).toString, row(3).toString, row(5).toString))
})

val tdd = pdd.flatMap(x => {
  val rowKey = PLong.INSTANCE.toBytes(x._1)
  for (i <- 0 until valCols.length - 1) yield {
    val colName = valCols(i).toString
    val colValue = x._2.productElement(i)
    val colFam = "0"

    (rowKey, (colFam, colName, colValue))
  }
})

val output = tdd.map(x => {
  val rowKey: Array[Byte] = x._1
  val immutableRowKey = new ImmutableBytesWritable(rowKey)

  val colFam = x._2._1
  val colName = x._2._2
  val colValue = x._2._3

  val kv = new KeyValue(rowKey,
    colFam.getBytes(),
    colName.getBytes,
    Bytes.toBytes(colValue.toString)
  )

  (immutableRowKey, kv)
})

val hbConf = HBaseConfiguration.create()
val hbaseSitePath = "/opt/hbase/hbase-1.4.4/conf/hbase-site.xml"
hbConf.addResource(new Path(hbaseSitePath))
val job: Job = Job.getInstance(hbConf, "Bulkload")
job.setMapOutputKeyClass(classOf[ImmutableBytesWritable])
job.setMapOutputValueClass(classOf[KeyValue])

val htableName = "HTOOL_P"
TableMapReduceUtil.initCredentials(job)
val htable: HTable = new HTable(hbConf, htableName)

val tableName: TableName = TableName.valueOf(htableName)
val conn = ConnectionFactory.createConnection(hbConf)
val table: Table = conn.getTable(tableName)
val tableDescriptor: HTableDescriptor = table.getTableDescriptor
val regionLocator: RegionLocator = conn.getRegionLocator(tableName)
HFileOutputFormat2.configureIncrementalLoad(job, tableDescriptor, regionLocator)

val hfileOutput = "tmp/phoenix_files"

// writing HFile to HDFS through Spark
output.saveAsNewAPIHadoopFile(
  hfileOutput,
  classOf[ImmutableBytesWritable],
  classOf[KeyValue],
  classOf[HFileOutputFormat2],
  hbConf)

val bulkLoader = new LoadIncrementalHFiles(hbConf)
bulkLoader.doBulkLoad(new Path("tmp/phoenix_files"), htable)

