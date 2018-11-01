## datasets from https://github.com/AbsaOSS/spline/tree/master/sample/data/input/batch

# Example 1:
import org.apache.spark.sql.{SaveMode, SparkSession}
val sourceDS = spark.read.option("header","true").option("inferSchema","true").csv("/user/joe_analyst/wikidata.csv").as("source").filter($"total_response_size" > 1000).filter($"count_views" > 10)
val domainMappingDS =spark.read.option("header","true").option("inferSchema","true").csv("/user/joe_analyst/domain.csv").as("mapping")
val joinedDS = sourceDS.join(domainMappingDS, $"domain_code" ===$"d_code","left_outer").select($"page_title".as("page"),$"d_name".as("domain"), $"count_views")
joinedDS.write.mode(SaveMode.Overwrite).format("orc").save("/user/joe_analyst/sparkoutput")


# Example 2:
# data from https://www.tutorialspoint.com/spark_sql/spark_sql_hive_tables.htm
...
1201, satish, 25
1202, krishna, 28
1203, amith, 39
1204, javed, 23
1205, prudvi, 23
...

...
val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)
sqlContext.sql("CREATE TABLE IF NOT EXISTS employee(id INT, name STRING, age INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'")
sqlContext.sql("LOAD DATA LOCAL INPATH 'employee.txt' INTO TABLE employee")
//val result = sqlContext.sql("FROM employee SELECT id, name, age")
//result.show()
sql("select * from employee").show()
...

# Example 3:

...
import org.apache.spark.sql.SparkSession
import spark.implicits._
import org.apache.spark.sql.{SaveMode, SparkSession}

spark.sql("CREATE TABLE IF NOT EXISTS training_table (text STRING)  USING hive")

val trainData = Seq(
 ("Hortonworks is a big data software company based in Santa Clara, California."),
 ("Temporary views in Spark SQL are session-scoped and will disappear if the session that creates it terminates."),
 ("Datasets are similar to RDDs, however, instead of using Java serialization or Kryo they use a specialized Encoder.")
).toDF("text")

trainData.write.mode(SaveMode.Overwrite).format("csv").save("/tmp/training_table.csv")

spark.sql("LOAD DATA INPATH '/tmp/training_table.csv' INTO TABLE training_table")
spark.sql("select * from training_table").show()
...
