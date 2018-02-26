package com.ervits.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class PutExample {

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        // conf.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
        // conf.set("hbase.zookeeper.property.clientPort", "2181");
        // conf.set("zookeeper.znode.parent", "/hbase-unsecure");
        try (Connection connection = ConnectionFactory.createConnection(conf);
                Table table = connection.getTable(TableName.valueOf("table1"))) {

            Put put = new Put(Bytes.toBytes("row1"));

            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("qual1"),
                    Bytes.toBytes("val1"));
            put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("qual2"),
                    Bytes.toBytes("val2"));

            table.put(put);
        }
    }
}
