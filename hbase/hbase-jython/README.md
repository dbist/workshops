#### Jython Example
```
sudo mkdir /opt/jython
export JYTHON_HOME=/opt/jython
HBASE_CLASSPATH=$JYTHON_HOME/jython.jar hbase org.python.util.jython
```

```
create 'wiki', 'title'
put 'wiki', 'r1', 'title:attr', 'val1'
```
```
import java.lang
from org.apache.hadoop.hbase import TableName, HBaseConfiguration
from org.apache.hadoop.hbase.client import Connection, ConnectionFactory, Result, ResultScanner, Table, Admin
from org.apache.hadoop.conf import Configuration
conf = HBaseConfiguration.create()
connection = ConnectionFactory.createConnection(conf)
admin = connection.getAdmin()
tableName = TableName.valueOf('wiki')
table = connection.getTable(tableName)

# just to check whether table exists
admin.tableExists(tableName)

col = "title"
scanner = table.getScanner(col)
while 1:
    result = scanner.next()
    if not result:
       break
    print java.lang.String(result.row), java.lang.String(result.getValue('title', 'attr'))
```


#### Example 2
```
import java.lang
from org.apache.hadoop.hbase import HBaseConfiguration, HTableDescriptor, HColumnDescriptor, TableName
from org.apache.hadoop.hbase.client import Admin, Connection, ConnectionFactory, Get, Put, Result, Table
from org.apache.hadoop.conf import Configuration

# First get a conf object.  This will read in the configuration
# that is out in your hbase-*.xml files such as location of the
# hbase master node.
conf = HBaseConfiguration.create()
connection = ConnectionFactory.createConnection(conf)
admin = connection.getAdmin()

# Create a table named 'test' that has two column families,
# one named 'content, and the other 'anchor'.  The colons
# are required for column family names.
tableName = TableName.valueOf("test")
table = connection.getTable(tableName)

desc = HTableDescriptor(tableName)
cDesc = HColumnDescriptor("content")
desc.addFamily(cDesc)

# Drop and recreate if it exists
if admin.tableExists(tableName):
    admin.disableTable(tableName)
    admin.deleteTable(tableName)

admin.createTable(desc)

# Add content to 'column:' on a row named 'row_x'
row = 'row_x'
put = Put(row)
put.addColumn("content", "qual", "some content")
table.put(put)

# Now fetch the content just added, returns a byte[]
get = Get(row)

result = table.get(get)
data = java.lang.String(result.getValue("content", "qual"))

print "The fetched row contains the value '%s'" % data
```
