# Mapping the flow of Uber traffic in San Francisco with Magellan
##### http://hortonworks.com/blog/magellan-geospatial-analytics-in-spark/

##### last edit: 02/25/16

##### Prerequisites
##### Sandbox: 2.3.2
##### Magellan 1.0.3
##### Spark 1.4.1

##### download data
```
unzip planning_neighborhoods.zip
sudo -u hdfs hdfs dfs -mkdir /user/root
sudo -u hdfs hdfs dfs -chown -R root:hdfs /user/root
hdfs dfs -put planning_neighborhoods* /user/root/

wget https://raw.githubusercontent.com/dima42/uber-gps-analysis/master/gpsdata/all.tsv
hdfs dfs -put all.tsv /user/root/
```

##### Magellan is a Spark Package, and can be included while launching the spark shell as follows
```
/usr/hdp/current/spark-client/bin/spark-shell --packages harsha2010:magellan:1.0.3-s_2.10
```

##### import required libraries
```
import magellan.{Point, Polygon, PolyLine}
import magellan.coord.NAD83
import org.apache.spark.sql.magellan.MagellanContext
import org.apache.spark.sql.magellan.dsl.expressions._
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
```

##### Let us create a case class to attach the schema to this Uber Dataset so we can use the DataFrame abstraction to deal with the data
```
case class UberRecord(tripId: String, timestamp: String, point: Point)
```

##### Now we can read the dataset into a dataframe and cache the resulting dataframe.
```
val uber = sc.textFile("/user/root/all.tsv").map { line =>
val parts = line.split("\t" )
val tripId = parts(0)
val timestamp = parts(1)
val point = Point(parts(3).toDouble, parts(2).toDouble)
UberRecord(tripId, timestamp, point)
}.
repartition(100).
toDF().
cache()
```

##### Magellan has a Data Source implementation that understands how to parse ESRI Shapefiles into Shapes and Metadata.
```
val magellanContext = new MagellanContext(sc)

val neighborhoods = magellanContext.read.format("magellan").
load("/user/root").
select($"polygon", $"metadata").
cache()
```

##### Magellan has a Polygon data structure to capture the spatial geometry of a Polygon. A Polygon in Magellan stands for a Polygonal object with zero or more holes.
```
neighborhoods.select(explode($"metadata").as(Seq("k", "v"))).show(5)
```

##### In Magellan, to join the Uber dataset with the San Francisco neighborhood dataset, you would issue the following Spark SQL query
```
neighborhoods.
join(uber).
where($"point" within $"polygon").
select($"tripId", $"timestamp", explode($"metadata").as(Seq("k", "v"))).
withColumnRenamed("v", "neighborhood").
drop("k").
show(5)
```

##### to translate between WGS84, the GPS standard coordinate system used in the Uber dataset, and NAD83 Zone 403 (state plane), we can use the following in built transformer
```
val transformer: Point => Point = (point: Point) => {
val from = new NAD83(Map("zone" -> 403)).from()
val p = point.transform(from)
new Point(3.28084 * p.x, 3.28084 * p.y)
}
```

##### enhance the uber dataset by adding a new column, the scaled column representing the coordinates in the NAD83 State Plane Coordinate System
```
val uberTransformed = uber.
withColumn("nad83", $"point".transform(transformer)).
cache()
```

##### try join again
```
val joined = neighborhoods.
join(uberTransformed).
where($"nad83" within $"polygon").
select($"tripId", $"timestamp", explode($"metadata").as(Seq("k", "v"))).
withColumnRenamed("v", "neighborhood").
drop("k").
cache()

joined.show(5)
```

##### What are the top few neighborhoods where most Uber trips pass through?
```
joined.
groupBy($"neighborhood").
agg(countDistinct("tripId").
as("trips")).
orderBy(col("trips").desc).
show(5)
```
