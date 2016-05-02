## Nifi workflow with ExecuteSQL

#### last edit: 04/19/16
#### Sandbox: 2.4.0
#### Apache Nifi: 0.6/HDF 1.2.0.0

#### make sure MySQL server is up and load sample data
```
service mysqld status
mysqld (pid  11835) is running...

su mysql
cd ~
```

#### download sample data with 1000 email addresses
```
curl "https://www.mockaroo.com/12639240/download?count=1000&key=89e294f0" > "email_list.csv"
```

#### login to MySQL and create database and table, then load sample data
```
mysql -u root
create database demo;
use demo;
create table mailinglist (id int, first_name varchar(50), last_name varchar(50), email varchar(50), gender varchar(10));
```

#### load data into MySQL
```
load data local infile 'email_list.csv' into table demo.mailinglist 
	fields terminated by ','
	lines terminated by '\n'
	(id, first_name, last_name, email, gender);
```

#### change password for root user in MySQL and grant priviledges
```
CREATE USER 'root'@'%';
GRANT ALL PRIVILEGES ON *.* to 'root'@'%' WITH GRANT OPTION;
SET PASSWORD FOR 'root'@'%' = PASSWORD('BadPass#1');
SET PASSWORD = PASSWORD('BadPass#1');
FLUSH PRIVILEGES;
exit;
```

#### doublecheck password works
```
mysql -u root -p -e "select count(user) from mysql.user;"
```
```
Enter password:
+-------------+
| count(user) |
+-------------+
|           9 |
+-------------+

exit
```

#### create sample hdfs directory
```
sudo -u hdfs hdfs dfs -mkdir /tmp/rdbms
sudo -u hdfs hdfs dfs -chown -R nifi:nifi /tmp/rdbms
```

#### nifi workflow consists of ExecuteSQL, MergeContent and PutHDFS 

#### then create schema for hive table
```
CREATE DATABASE DEMO;
USE DEMO;
CREATE EXTERNAL TABLE mailinglist
  COMMENT "just drop the schema right into the HQL"
  ROW FORMAT SERDE
  'org.apache.hadoop.hive.serde2.avro.AvroSerDe'
  STORED AS INPUTFORMAT
  'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat'
  OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat'
  LOCATION '/tmp/rdbms'
  TBLPROPERTIES (
    'avro.schema.literal'='{
      "type": "record",
      "name": "mailinglist",
      "namespace": "any.data",
      "fields": [
        {
          "name": "id",
          "type": [
            "null",
            "int"
          ]
        },
        {
          "name": "first_name",
          "type": [
            "null",
            "string"
          ]
        },
        {
          "name": "last_name",
          "type": [
            "null",
            "string"
          ]
        },
        {
          "name": "email",
          "type": [
            "null",
            "string"
          ]
        },
        {
          "name": "gender",
          "type": [
            "null",
            "string"
          ]
        }
      ]
    }');
```

#### now schema is created on top of the landing files and you can query Hive as you wish
```
USE DEMO;
SELECT * FROM MAILINGLIST LIMIT 100;
```

```
[root@sandbox ~]# beeline
WARNING: Use "yarn jar" to launch YARN applications.
Beeline version 1.2.1000.2.4.0.0-169 by Apache Hive
beeline> !connect jdbc:hive2://localhost:10000
Connecting to jdbc:hive2://localhost:10000
Enter username for jdbc:hive2://localhost:10000:
Enter password for jdbc:hive2://localhost:10000:
Connected to: Apache Hive (version 1.2.1000.2.4.0.0-169)
Driver: Hive JDBC (version 1.2.1000.2.4.0.0-169)
Transaction isolation: TRANSACTION_REPEATABLE_READ
0: jdbc:hive2://localhost:10000> use demo;
No rows affected (0.177 seconds)
0: jdbc:hive2://localhost:10000> select * from mailinglist limit 10;
+-----------------+-------------------------+------------------------+--------------------------------+---------------------+--+
| mailinglist.id  | mailinglist.first_name  | mailinglist.last_name  |       mailinglist.email        | mailinglist.gender  |
+-----------------+-------------------------+------------------------+--------------------------------+---------------------+--+
| 0               | first_name              | last_name              | email                          | gender              |
| 1               | Bruce                   | Palmer                 | bpalmer0@accuweather.com       | Male                |
| 2               | Walter                  | Mason                  | wmason1@umich.edu              | Male                |
| 3               | Kelly                   | Burke                  | kburke2@google.nl              | Female              |
| 4               | Juan                    | Barnes                 | jbarnes3@github.com            | Male                |
| 5               | Ashley                  | Hanson                 | ahanson4@1und1.de              | Female              |
| 6               | Antonio                 | Collins                | acollins5@creativecommons.org  | Male                |
| 7               | Maria                   | Green                  | mgreen6@google.nl              | Female              |
| 8               | Heather                 | Carter                 | hcarter7@google.co.jp          | Female              |
| 9               | Kenneth                 | Sanchez                | ksanchez8@indiegogo.com        | Male                |
+-----------------+-------------------------+------------------------+--------------------------------+---------------------+--+
10 rows selected (0.545 seconds)
0: jdbc:hive2://localhost:10000> select email from mailinglist limit 10;
+--------------------------------+--+
|             email              |
+--------------------------------+--+
| email                          |
| bpalmer0@accuweather.com       |
| wmason1@umich.edu              |
| kburke2@google.nl              |
| jbarnes3@github.com            |
| ahanson4@1und1.de              |
| acollins5@creativecommons.org  |
| mgreen6@google.nl              |
| hcarter7@google.co.jp          |
| ksanchez8@indiegogo.com        |
+--------------------------------+--+
10 rows selected (0.565 seconds)
0: jdbc:hive2://localhost:10000>
```
##### finished workflow
![Image](../screenshots/finished_workflow.png?raw=true)
