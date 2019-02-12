```
kinit -kt /etc/security/keytabs/joe_analyst.keytab joe_analyst/aervits-hortonia0.field.hortonworks.com@HWX.COM
```
```
wget -O book http://www.gutenberg.org/cache/epub/16215/pg16215.txt
```
```
hdfs dfs -put book .
```
```
chmod 777 java-1.0-SNAPSHOT-jar-with-dependencies.jar
```
```
spark-submit --class org.apache.spark.examples.JavaWordCount --master yarn --deploy-mode cluster java-1.0-SNAPSHOT-jar-with-dependencies.jar hdfs://aervits-hortonia0:8020/user/joe_analyst/book hdfs://aervits-hortonia0:8020/user/joe_analyst/output
```
```
hdfs dfs -ls output/part-00000.deflate
```
```
hdfs dfs -text output/part-00000.deflate | hdfs dfs -put - uncompressed_output
```
hdfs dfs -cat uncompressed_output
```
