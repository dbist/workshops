package com.ervits.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;

public class GetExample {
    private static final byte[] CF = "title".getBytes();
    private static final byte[] ATTR = "attr".getBytes();
    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        // conf.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
        // conf.set("hbase.zookeeper.property.clientPort", "2181");
        // conf.set("zookeeper.znode.parent", "/hbase-unsecure");
        try (Connection connection = ConnectionFactory.createConnection(conf);
                Table table = connection.getTable(TableName.valueOf("wiki"))) {

            Get get = new Get(Bytes.toBytes("row1"));
            Result r = table.get(get);
            byte[] b = r.getValue(CF, ATTR);

            System.out.println(Bytes.toString(b));
        }
    }
}
