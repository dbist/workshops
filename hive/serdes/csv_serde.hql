drop table if exists sample;
create external table sample(id int,first_name string,last_name string,email string,gender string,ip_address string)
  row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
  stored as textfile
location '/tmp/csv_serde/';
