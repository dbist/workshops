-- example of csv serde with non-default separator, in this case tab delimited
-- sample generated via mockaroo, saved as tab-delimited output
drop table if exists sample;
create external table sample(id int,first_name string,last_name string,email string,gender string,ip_address string)
  row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
with serdeproperties (
  "separatorChar" = "\t"
  )
  stored as textfile
location '/tmp/csv_serde_custom_delim/';
