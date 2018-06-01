name := "phoenix-spark-bulk-load"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.3.0",
  "org.apache.spark" %% "spark-sql" % "2.3.0",
  "org.apache.hadoop" %% "hadoop-common" % "2.7.6",
  "org.apache.hbase" %% "hbase-client" % "1.4.4",
  "org.apache.phoenix" %% "phoenix-client" % "1.4.4"
)