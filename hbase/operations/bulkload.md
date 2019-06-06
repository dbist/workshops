# create HFiles for loading, options exist to create table and load or create files first
# upload the data.csv file to hdfs
`hdfs dfs -put -f data.csv .`

# prepare files with the following command
`hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.bulk.output=/user/vagrant/hfileoutput -Dimporttsv.columns=HBASE_ROW_KEY,cf:q -Dimporttsv.separator=, stocks_table data.csv`

# Note: this dataset may contain commas and {{ImportTsv}} may interpret it as new columns and will report Bad lines=412, ignore
# it just means those records will be ignored.

# load the files when ready with the following command
`hbase completebulkload -Dcreate.table=no hfileoutput stocks_table`

# TODO: figure out how to pre-split the table as this approach creates 1 region
