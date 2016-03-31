## Convert XML to JSON using MapReduce
##### last edit: 03/31/16
##### Sandbox: 2.3.2

##### This is a map-only job that converts XML to JSON using org.json library. Inspired by article from https://acadgild.com/blog/quick-way-convert-xml/

##### Create xml directory, upload xml data and run the MR job, finally view output
```
hdfs dfs -mkdir xml

hdfs dfs -put /etc/hadoop/conf/*.xml xml/

yarn jar ConvertXmlToJsonMR-1.0-jar-with-dependencies.jar ./xml jsonoutput

hdfs dfs -cat jsonoutput/part-m-00000
