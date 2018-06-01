#### Apache Spark and Apache Phoenix Bulk-load example
#### taken from [https://medium.com/hashmapinc/3-steps-for-bulk-loading-1m-records-in-20-seconds-into-apache-phoenix-99b77ad87387](3 Steps for Bulk Loading 1M Records in 20 Seconds Into Apache Phoenix)

#### create Phoenix table in sqlline

```
CREATE TABLE HTOOL_P (
U_ID BIGINT NOT NULL,
TIME_IN_ISO VARCHAR,
VAL VARCHAR,
BATCH_ID VARCHAR,
JOB_ID VARCHAR,
POS_ID VARCHAR,
CONSTRAINT pk PRIMARY KEY (U_ID));
```

#### generate csv data
```
cd CSVDataGenerator/
mvn clean package
java -jar target/CSVDataGenerator-1.0-SNAPSHOT-jar-with-dependencies.jar
```
