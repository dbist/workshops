# Hive CSV Serde
#### sample data generated via mockaroo, tab-delimited output for sample.txt and csv for sample.csv
#### more info at https://cwiki.apache.org/confluence/display/Hive/CSV+Serde

```
scp -P 2222 -r sample.txt root@127.0.0.1:/home/guest/
scp -P 2222 -r sample.csv root@127.0.0.1:/home/guest/
chown -R guest:guest /home/guest/sample.*
su guest
cd
hdfs dfs -mkdir /tmp/csv_serde/
hdfs dfs -put sample.csv /tmp/csv_serde/
hdfs dfs -chmod -R 777 /tmp/csv_serde
```
#### in Ambari as admin, add guest user rights to hive view


#### login as guest and open Hive view
```
drop table if exists sample;
create external table sample(id int,first_name string,last_name string,email string,gender string,ip_address string)
  row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
  stored as textfile
location '/tmp/csv_serde/';

show tables;

select * from sample limit 10;
```


#### custom delimited
#### in this case tab-delimited
```
hdfs dfs -mkdir /tmp/csv_serde_custom_delim/
hdfs dfs -chmod -R 777 /tmp/csv_serde_custom_delim
hdfs dfs -put sample.txt /tmp/csv_serde_custom_delim/

drop table if exists sample;
create external table sample(id int,first_name string,last_name string,email string,gender string,ip_address string)
  row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
with serdeproperties (
  "separatorChar" = "\t"
  )
  stored as textfile
location '/tmp/csv_serde_custom_delim/';

select * from sample limit 10;
```
