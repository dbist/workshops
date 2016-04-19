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

#### create sample hdfs directory
```
sudo -u hdfs hdfs dfs -mkdir /tmp/rdbms
sudo -u hdfs hdfs dfs -chown -R nifi:nifi /tmp/rdbms
```

#### nifi workflow consists of ExecuteSQL, MergeContent and PutHDFS 
