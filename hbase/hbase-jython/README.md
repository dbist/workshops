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
