### Download hbase and follow https://hbase.apache.org/book.html#_get_started_with_hbase

```
wget -O hbase-1.4.9-bin.tar.gz https://www-us.apache.org/dist/hbase/1.4.9/hbase-1.4.9-bin.tar.gz
tar xvzf hbase-1.4.9-bin.tar.gz
```

##### Make sure JAVA_HOME is set

##### Make sure to edit hbase-VERSION/conf/hbase-site.xml and fill out with the following
##### the <value> property should reflect your local directory structure.

```
<configuration>
  <property>
    <name>hbase.rootdir</name>
    <value>file:///home/testuser/hbase</value>
  </property>
  <property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>/home/testuser/zookeeper</value>
  </property>
  <property>
    <name>hbase.unsafe.stream.capability.enforce</name>
    <value>false</value>
    <description>
      Controls whether HBase will check for stream capabilities (hflush/hsync).

      Disable this if you intend to run on LocalFileSystem, denoted by a rootdir
      with the 'file://' scheme, but be mindful of the NOTE below.

      WARNING: Setting this to false blinds you to potential data loss and
      inconsistent system state in the event of process and/or node failures. If
      HBase is complaining of an inability to use hsync or hflush it's most
      likely not a false positive.
    </description>
  </property>
</configuration>
```

##### At this point you should be able to start hbase

`<install dir>/bin/start-hbase.sh`

##### This mode of operation is called local, to verify running processes
`jps`

```
HW14038:hbase-1.4.9 aervits$ jps
1921 HMaster
1939 Jps
67071 Main
```

##### At this point it's good to inspect the logs

`<install dir>/logs`

##### it's also recommended to browse the web UI, open browser and navigate to
##### http://localhost:16010, this is called Master UI
