#### https://docs.hortonworks.com/HDPDocuments/HDP3/HDP-3.1.0/phoenix-data-access/content/saving_phoenix_tables.html
#### https://phoenix.apache.org/phoenix_spark.html

requires PHOENIX-5149

```
$SPARK_HOME/bin/spark-shell \
    --master yarn \
    --deploy-mode client \
    --driver-memory 512m \
    --executor-memory 512m \
    --executor-cores 1 \
    --queue default \
    --jars $PHOENIX_HOME/*.jar \
    --driver-class-path $PHOENIX_HOME/phoenix-4.14.1-HBase-1.4-client.jar:/etc/hbase/conf
```

# Load as a DataFrame using the Data Source API

```
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.phoenix.spark._

//val sc = new SparkContext("local", "phoenix-test")
val sqlContext = new SQLContext(sc)

val df = sqlContext.load(
"org.apache.phoenix.spark",
  Map("table" -> "TABLE1", "zkUrl" -> "localhost:2181")
)

df.filter(df("COL1") === "test_row_1" && df("ID") === 1L).select(df("ID")).show
```

# Load as a DataFrame directly using a Configuration object

```
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.phoenix.spark._

val configuration = new Configuration()
// Can set Phoenix-specific settings, requires 'hbase.zookeeper.quorum'

//val sc = new SparkContext("local", "phoenix-test")
val sqlContext = new SQLContext(sc)

// Load the columns 'ID' and 'COL1' from TABLE1 as a DataFrame
val df = sqlContext.phoenixTableAsDataFrame(
"TABLE1", Array("ID", "COL1"), conf = configuration)
df.show
```

# Load as an RDD, using a Zookeeper URL

```
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.phoenix.spark._
import org.apache.spark.rdd.RDD

//val sc = new SparkContext("local", "phoenix-test")

// Load the columns 'ID' and 'COL1' from TABLE1 as an RDD
val rdd: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
  "TABLE1", Seq("ID", "COL1"), zkUrl = Some("localhost:2181")
)

rdd.count()

val firstId = rdd.first()("ID").asInstanceOf[Long]
val firstCol = rdd.first()("COL1").asInstanceOf[String]
```

# Saving RDDs

```
import org.apache.spark.SparkContext
import org.apache.phoenix.spark._

//val sc = new SparkContext("local", "phoenix-test")
val dataSet = List((1L, "1", 1), (2L, "2", 2), (3L, "3", 3))

sc.parallelize(dataSet).saveToPhoenix(
    "OUTPUT_TEST_TABLE",
    Seq("ID","COL1","COL2"),
    zkUrl = Some("localhost:2181")
  )
```

# Saving DataFrames

```
import org.apache.spark.SparkContext
import org.apache.spark.sql._
import org.apache.phoenix.spark._

// Load INPUT_TABLE
// val sc = new SparkContext("local", "phoenix-test")
val sqlContext = new SQLContext(sc)
val df = sqlContext.load("org.apache.phoenix.spark", Map("table" -> "INPUT_TABLE",
  "zkUrl" -> "localhost:2181"))

// Save to OUTPUT_TABLE
df.saveToPhoenix(Map("table" -> "OUTPUT_TABLE", "zkUrl" -> "localhost:2181"))
```

or

```
df.write
.format("org.apache.phoenix.spark")
.mode("overwrite")
.option("table", "OUTPUT_TABLE")
.option("zkUrl", "localhost:2181")
.save()
```
