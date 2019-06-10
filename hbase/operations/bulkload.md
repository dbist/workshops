# Example to prepare and import HFiles

##### create HFiles for loading, options exist to create table and load or create files first
##### upload the data.csv file to hdfs

`hdfs dfs -put -f data.csv .`

#### prepare files with the following command

`hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.bulk.output=/user/vagrant/hfileoutput -Dimporttsv.columns=HBASE_ROW_KEY,cf:q -Dimporttsv.separator=, stocks data.csv`

#### Note: this dataset may contain commas and _ImportTsv_ may interpret it as new columns and will report Bad lines=412, ignore
#### it just means those records will be ignored.

#### table will be pre-created with utility but if user wants to perhaps pre-split, create table like so
create 'stocks', 'cf', {SPLITS => ['aaaaaaaa', 'bbbbbbbb', 'cccccccc', 'dddddddd', 'eeeeeeee', 'ffffffff']}

#### load the files when ready with the following command

`hbase completebulkload -Dcreate.table=no hfileoutput stocks`

#### table at this point should have 588 records

```
hbase(main):004:0> count 'stocks', INTERVAL => 100
Current count: 100, row: 2d8e972e-1aaf-4e56-8012-ec249be7b49a
Current count: 200, row: 57938f42-36bb-4215-96ee-4454a2cf0940
Current count: 300, row: 814692c9-5a8f-45f9-91dc-b17c2eca34df
Current count: 400, row: aa828db9-de2b-416b-b96f-8b82a96c5ff5
Current count: 500, row: d7abaf3e-493f-4d15-896d-cc52a2e26c01
588 row(s)
Took 0.8229 seconds
=> 588
```

#### scan the table

```
hbase(main):005:0> scan 'stocks', LIMIT => 5
ROW                                COLUMN+CELL
 014ff67e-7065-4bc7-8eb7-7bb5c2d2e column=cf:q, timestamp=1559788188418, value=Nuveen All Cap Energy MLP Opportunities Fund
 865
 01867ab3-b9ff-4778-a265-c5a6e8725 column=cf:q, timestamp=1559788188418, value=Dollar General Corporation
 a46
 022c4389-ff67-4832-ab76-5cf40450c column=cf:q, timestamp=1559788188418, value=Forestar Group Inc
 f93
 02500f87-d48b-477f-b529-d5f87ce59 column=cf:q, timestamp=1559788188418, value=Inseego Corp.
 0ec
 02c5086d-7eea-41a9-839e-def5489f1 column=cf:q, timestamp=1559788188418, value=E.I. du Pont de Nemours and Company
 1d4
5 row(s)
Took 0.2513 seconds
```
#### TODO: figure out how to pre-split the table as this approach creates 1 region
