#### Last edit: 03/06/16
#### Sandbox 2.3.2

## Demo changing compression with ALTER command and CONCATENATE

##### Create table
```
DROP TABLE IF EXISTS patients_text;
CREATE TABLE patients_text (
	id int,
	first_name string,
	last_name string,
	gender string,
	dob string,
	language string,
	icd9_short_desc string,
	icd9_proc_desc string,
	icd9_long_desc string,
	ssn string
) 
	COMMENT 'text table'
	ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
```	

##### Generate sample data
```
curl "https://www.mockaroo.com/00118c50/download?count=1000&key=89e294f0" > "patients.csv"
```

##### Load data using hive shell as beeline is throwing SemanticException in current version of Sandbox
```
LOAD DATA LOCAL INPATH 'patients.csv' OVERWRITE INTO TABLE patients_text;
```

##### Create an ORC table from Text table
```
DROP TABLE IF EXISTS patients_orc;
CREATE TABLE patients_orc STORED AS ORC TBLPROPERTIES ("orc.compress"="NONE") AS SELECT * FROM patients_text;
```

##### Describe table
```
DESCRIBE FORMATTED patients_orc;
```

```
hive> DESCRIBE FORMATTED patients_orc;
OK
# col_name            	data_type           	comment

id                  	int
first_name          	string
last_name           	string
gender              	string
dob                 	string
language            	string
icd9_short_desc     	string
icd9_proc_desc      	string
icd9_long_desc      	string
ssn                 	string

# Detailed Table Information
Database:           	default
Owner:              	root
CreateTime:         	Mon Mar 07 02:17:19 UTC 2016
LastAccessTime:     	UNKNOWN
Protect Mode:       	None
Retention:          	0
Location:           	hdfs://sandbox.hortonworks.com:8020/apps/hive/warehouse/patients_orc
Table Type:         	MANAGED_TABLE
Table Parameters:
	COLUMN_STATS_ACCURATE	true
	numFiles            	1
	numRows             	1001
	orc.compress        	NONE
	rawDataSize         	873869
	totalSize           	100351
	transient_lastDdlTime	1457317039

# Storage Information
SerDe Library:      	org.apache.hadoop.hive.ql.io.orc.OrcSerde
InputFormat:        	org.apache.hadoop.hive.ql.io.orc.OrcInputFormat
OutputFormat:       	org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat
Compressed:         	No
Num Buckets:        	-1
Bucket Columns:     	[]
Sort Columns:       	[]
Storage Desc Params:
	serialization.format	1
Time taken: 0.553 seconds, Fetched: 41 row(s)
```

##### Doing ls on the Hive directory for the patients_orc table yields one file
```
hdfs dfs -ls /apps/hive/warehouse/patients_orc
```
```
Found 1 items
-rwxrwxrwx   3 root hdfs     100351 2016-03-07 02:29 /apps/hive/warehouse/patients_orc/000000_0
```

##### Either create a new table or truncate this one to load more data (may not be necessary as loading new data into an existing table may overwrite old data, need to confirm)
```
TRUNCATE TABLE patients_text;
```

##### Load another 1000 rows
```
curl "https://www.mockaroo.com/00118c50/download?count=1000&key=89e294f0" > "patients2.csv"
```
```
LOAD DATA LOCAL INPATH 'patients2.csv' OVERWRITE INTO TABLE patients_text;
```

##### Insert new data into current Orc table
```
INSERT INTO patients_orc SELECT * FROM patients_text;
```

##### Describe the table again, notice new row count and number of files
```
hive> DESCRIBE FORMATTED patients_orc;
OK
# col_name            	data_type           	comment

id                  	int
first_name          	string
last_name           	string
gender              	string
dob                 	string
language            	string
icd9_short_desc     	string
icd9_proc_desc      	string
icd9_long_desc      	string
ssn                 	string

# Detailed Table Information
Database:           	default
Owner:              	root
CreateTime:         	Mon Mar 07 02:29:08 UTC 2016
LastAccessTime:     	UNKNOWN
Protect Mode:       	None
Retention:          	0
Location:           	hdfs://sandbox.hortonworks.com:8020/apps/hive/warehouse/patients_orc
Table Type:         	MANAGED_TABLE
Table Parameters:
	COLUMN_STATS_ACCURATE	true
	numFiles            	2
	numRows             	2002
	orc.compress        	NONE
	rawDataSize         	1746737
	totalSize           	199610
	transient_lastDdlTime	1457317893

# Storage Information
SerDe Library:      	org.apache.hadoop.hive.ql.io.orc.OrcSerde
InputFormat:        	org.apache.hadoop.hive.ql.io.orc.OrcInputFormat
OutputFormat:       	org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat
Compressed:         	No
Num Buckets:        	-1
Bucket Columns:     	[]
Sort Columns:       	[]
Storage Desc Params:
	serialization.format	1
Time taken: 0.532 seconds, Fetched: 41 row(s)
```

##### There are now two files in the directory
```
Found 2 items
-rwxrwxrwx   3 root hdfs     100351 2016-03-07 02:29 /apps/hive/warehouse/patients_orc/000000_0
-rwxrwxrwx   3 root hdfs      99259 2016-03-07 02:31 /apps/hive/warehouse/patients_orc/000000_0_copy_1
```

##### Issue concatenate on the table
```
ALTER TABLE patients_orc CONCATENATE;
```

##### If you get IllegalArgumentException error, concatenate succeeds but error still shows then follow the workaround in the following Hortonworks Community entry https://community.hortonworks.com/questions/6359/error-on-concatenating-orc-hive-table-merge-files.html
##### The workaround solution is to remove "org.apache.atlas.hive.hook.HiveHook" from "hive.exec.post.hooks" in Hive Advanced Configs
```
hive> ALTER TABLE patients_orc CONCATENATE;


Status: Running (Executing on YARN cluster with App id application_1457314154742_0009)

--------------------------------------------------------------------------------
        VERTICES      STATUS  TOTAL  COMPLETED  RUNNING  PENDING  FAILED  KILLED
--------------------------------------------------------------------------------
File Merge .....   SUCCEEDED      1          1        0        0       0       0
--------------------------------------------------------------------------------
VERTICES: 01/01  [==========================>>] 100%  ELAPSED TIME: 3.25 s
--------------------------------------------------------------------------------
Loading data to table default.patients_orc
Moved: 'hdfs://sandbox.hortonworks.com:8020/apps/hive/warehouse/patients_orc/000000_0' to trash at: hdfs://sandbox.hortonworks.com:8020/user/root/.Trash/Current
Table default.patients_orc stats: [numFiles=1, numRows=0, totalSize=297838, rawDataSize=0]
FAILED: Hive Internal Error: java.lang.IllegalArgumentException(No enum constant org.apache.hadoop.hive.ql.plan.HiveOperation.ALTER_TABLE_MERGE)
java.lang.IllegalArgumentException: No enum constant org.apache.hadoop.hive.ql.plan.HiveOperation.ALTER_TABLE_MERGE
	at java.lang.Enum.valueOf(Enum.java:236)
	at org.apache.hadoop.hive.ql.plan.HiveOperation.valueOf(HiveOperation.java:23)
	at org.apache.atlas.hive.hook.HiveHook.run(HiveHook.java:151)
	at org.apache.hadoop.hive.ql.Driver.execute(Driver.java:1522)
	at org.apache.hadoop.hive.ql.Driver.runInternal(Driver.java:1195)
	at org.apache.hadoop.hive.ql.Driver.run(Driver.java:1059)
	at org.apache.hadoop.hive.ql.Driver.run(Driver.java:1049)
	at org.apache.hadoop.hive.cli.CliDriver.processLocalCmd(CliDriver.java:213)
	at org.apache.hadoop.hive.cli.CliDriver.processCmd(CliDriver.java:165)
	at org.apache.hadoop.hive.cli.CliDriver.processLine(CliDriver.java:376)
	at org.apache.hadoop.hive.cli.CliDriver.executeDriver(CliDriver.java:736)
	at org.apache.hadoop.hive.cli.CliDriver.run(CliDriver.java:681)
	at org.apache.hadoop.hive.cli.CliDriver.main(CliDriver.java:621)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at org.apache.hadoop.util.RunJar.run(RunJar.java:221)
	at org.apache.hadoop.util.RunJar.main(RunJar.java:136)
```

##### Now running the same command will not throw an error
```
hive> ALTER TABLE patients_orc CONCATENATE;


Status: Running (Executing on YARN cluster with App id application_1457314154742_0010)

--------------------------------------------------------------------------------
        VERTICES      STATUS  TOTAL  COMPLETED  RUNNING  PENDING  FAILED  KILLED
--------------------------------------------------------------------------------
File Merge .....   SUCCEEDED      1          1        0        0       0       0
--------------------------------------------------------------------------------
VERTICES: 01/01  [==========================>>] 100%  ELAPSED TIME: 3.25 s
--------------------------------------------------------------------------------
Loading data to table default.patients_orc
Moved: 'hdfs://sandbox.hortonworks.com:8020/apps/hive/warehouse/patients_orc/000000_0' to trash at: hdfs://sandbox.hortonworks.com:8020/user/root/.Trash/Current
Table default.patients_orc stats: [numFiles=1, numRows=0, totalSize=297838, rawDataSize=0]
OK
Time taken: 11.979 seconds
```

##### Listing the table directory will now show one file
```
Found 1 items
-rwxrwxrwx   3 root hdfs     297838 2016-03-07 02:55 /apps/hive/warehouse/patients_orc/000000_0
```

##### Describing the table again, notice number of files
```
hive> DESCRIBE FORMATTED patients_orc;
OK
# col_name            	data_type           	comment

id                  	int
first_name          	string
last_name           	string
gender              	string
dob                 	string
language            	string
icd9_short_desc     	string
icd9_proc_desc      	string
icd9_long_desc      	string
ssn                 	string

# Detailed Table Information
Database:           	default
Owner:              	root
CreateTime:         	Mon Mar 07 02:29:08 UTC 2016
LastAccessTime:     	UNKNOWN
Protect Mode:       	None
Retention:          	0
Location:           	hdfs://sandbox.hortonworks.com:8020/apps/hive/warehouse/patients_orc
Table Type:         	MANAGED_TABLE
Table Parameters:
	COLUMN_STATS_ACCURATE	true
	numFiles            	1
	numRows             	0
	orc.compress        	NONE
	rawDataSize         	0
	totalSize           	297838
	transient_lastDdlTime	1457319339

# Storage Information
SerDe Library:      	org.apache.hadoop.hive.ql.io.orc.OrcSerde
InputFormat:        	org.apache.hadoop.hive.ql.io.orc.OrcInputFormat
OutputFormat:       	org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat
Compressed:         	No
Num Buckets:        	-1
Bucket Columns:     	[]
Sort Columns:       	[]
Storage Desc Params:
	serialization.format	1
Time taken: 6.397 seconds, Fetched: 41 row(s)
```

##### Changing fileformat on the fly to TEXTFILE
```
ALTER TABLE patients_orc SET FILEFORMAT TEXTFILE;
```

##### Describe the table, notice format
```
hive> DESCRIBE FORMATTED patients_orc;
OK
# col_name            	data_type           	comment

id                  	int
first_name          	string
last_name           	string
gender              	string
dob                 	string
language            	string
icd9_short_desc     	string
icd9_proc_desc      	string
icd9_long_desc      	string
ssn                 	string

# Detailed Table Information
Database:           	default
Owner:              	root
CreateTime:         	Mon Mar 07 02:29:08 UTC 2016
LastAccessTime:     	UNKNOWN
Protect Mode:       	None
Retention:          	0
Location:           	hdfs://sandbox.hortonworks.com:8020/apps/hive/warehouse/patients_orc
Table Type:         	MANAGED_TABLE
Table Parameters:
	COLUMN_STATS_ACCURATE	false
	last_modified_by    	root
	last_modified_time  	1457319700
	numFiles            	1
	numRows             	-1
	orc.compress        	NONE
	rawDataSize         	-1
	totalSize           	297838
	transient_lastDdlTime	1457319700

# Storage Information
SerDe Library:      	org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe
InputFormat:        	org.apache.hadoop.mapred.TextInputFormat
OutputFormat:       	org.apache.hadoop.hive.ql.io.IgnoreKeyTextOutputFormat
Compressed:         	No
Num Buckets:        	-1
Bucket Columns:     	[]
Sort Columns:       	[]
Storage Desc Params:
	serialization.format	1
Time taken: 0.418 seconds, Fetched: 43 row(s)
```

##### Let's switch back to ORC
```
ALTER TABLE patients_orc SET FILEFORMAT ORC;
```

##### Let's change compression
```
ALTER TABLE patients_orc SET TBLPROPERTIES ("orc.compress"="Zlib");
```

##### Notice the compression changed from None to Zlib
```
hive> DESCRIBE FORMATTED patients_orc;
OK
# col_name            	data_type           	comment

id                  	int
first_name          	string
last_name           	string
gender              	string
dob                 	string
language            	string
icd9_short_desc     	string
icd9_proc_desc      	string
icd9_long_desc      	string
ssn                 	string

# Detailed Table Information
Database:           	default
Owner:              	root
CreateTime:         	Mon Mar 07 02:29:08 UTC 2016
LastAccessTime:     	UNKNOWN
Protect Mode:       	None
Retention:          	0
Location:           	hdfs://sandbox.hortonworks.com:8020/apps/hive/warehouse/patients_orc
Table Type:         	MANAGED_TABLE
Table Parameters:
	COLUMN_STATS_ACCURATE	false
	last_modified_by    	root
	last_modified_time  	1457320010
	numFiles            	1
	numRows             	-1
	orc.compress        	Zlib
	rawDataSize         	-1
	totalSize           	297838
	transient_lastDdlTime	1457320010

# Storage Information
SerDe Library:      	org.apache.hadoop.hive.ql.io.orc.OrcSerde
InputFormat:        	org.apache.hadoop.hive.ql.io.orc.OrcInputFormat
OutputFormat:       	org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat
Compressed:         	No
Num Buckets:        	-1
Bucket Columns:     	[]
Sort Columns:       	[]
Storage Desc Params:
	serialization.format	1
Time taken: 0.321 seconds, Fetched: 43 row(s)
```

##### let's change to SNAPPY
```
ALTER TABLE patients_orc SET TBLPROPERTIES ("orc.compress"="SNAPPY");
```

##### Describe the table and notice compression is switched to Snappy, this is because ALTER command changes metadata about table, not the actual data
```
Table Parameters:
	COLUMN_STATS_ACCURATE	false
	last_modified_by    	root
	last_modified_time  	1457320079
	numFiles            	1
	numRows             	-1
	orc.compress        	SNAPPY
	rawDataSize         	-1
	totalSize           	297838
	transient_lastDdlTime	1457320079
```
