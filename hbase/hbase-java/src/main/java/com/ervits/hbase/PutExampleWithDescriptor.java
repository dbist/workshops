package com.ervits.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;

public class PutExampleWithDescriptor {

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        // conf.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
        // conf.set("hbase.zookeeper.property.clientPort", "2181");
        // conf.set("zookeeper.znode.parent", "/hbase-unsecure");
        try (Connection connection = ConnectionFactory.createConnection(conf);
                Admin admin = connection.getAdmin();) {

            TableName tableName = TableName.valueOf("table1");
            Table table = connection.getTable(tableName);
            HTableDescriptor desc = new HTableDescriptor(tableName);
            HColumnDescriptor cDesc = new HColumnDescriptor("content");
            desc.addFamily(cDesc);

            if (admin.tableExists(tableName)) {
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            }
            admin.createTable(desc);
            byte[] key = Bytes.toBytes("row_x");
            byte[] family = Bytes.toBytes("content");
            byte[] qualifier = Bytes.toBytes("qual");
            byte[] value = Bytes.toBytes("some content");
            Put put = new Put(key);
            put.addColumn(family, qualifier, value);
            table.put(put);

            Get get = new Get(key);
            Result result = table.get(get);
            byte[] output = result.getValue(family, qualifier);
            System.out.println(String.format("Output is: %s", Bytes.toString(output)));
        }
    }
}
