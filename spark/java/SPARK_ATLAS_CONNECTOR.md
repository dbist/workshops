#### following this doc https://docs.google.com/document/d/1zpCX6CB6BB-O-aJZTl4yEMJMt-3c9-T_0SmLvd6brQE/edit
#### hortonia machine, run with export enable_knox_sso_proxy=false or command "sudo ambari-server setup-sso" and then say no
#### in atlas ambari config, turn off proxy by unchecking "Enable Atlas Knox SSO" 

```
knit -kt /etc/security/keytabs/joe_analyst.keytab joe_analyst/$(hostname -f)@HWX.COM
wget -O book http://www.gutenberg.org/cache/epub/16215/pg16215.txt
hdfs dfs -put book .
chmod 777 java-1.0-SNAPSHOT-jar-with-dependencies.jar
```
```
/usr/hdp/current/spark2-client/bin/spark-submit --master yarn --deploy-mode client \
--class org.apache.spark.examples.JavaWordCountSaveMode \
--packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.3.0 \
--conf spark.extraListeners=com.hortonworks.spark.atlas.SparkAtlasEventTracker \
--conf spark.sql.queryExecutionListeners=com.hortonworks.spark.atlas.SparkAtlasEventTracker  \
--principal joe_analyst/$(hostname -f)@HWX.COM --keytab /etc/security/keytabs/joe_analyst.keytab \
--jars /tmp/spark-atlas-connector-assembly_2.11-0.1.0-SNAPSHOT.jar \
java-1.0-SNAPSHOT-jar-with-dependencies.jar \
hdfs://aervits-hortonia0:8020/user/joe_analyst/book \
hdfs://aervits-hortonia0:8020/user/joe_analyst/output2
```

```
hdfs dfs -ls output4/part-00000.deflate
hdfs dfs -text output4/part-00000.deflate | hdfs dfs -put - uncompressed_output4
hdfs dfs -cat uncompressed_output4
```
