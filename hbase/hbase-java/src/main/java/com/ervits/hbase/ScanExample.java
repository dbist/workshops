package com.ervits.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

public class ScanExample {

    private static final byte[] CF = "title".getBytes();
    private static final byte[] ATTR = "attr".getBytes();

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        // conf.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
        // conf.set("hbase.zookeeper.property.clientPort", "2181");
        // conf.set("zookeeper.znode.parent", "/hbase-unsecure");
        try (Connection connection = ConnectionFactory.createConnection(conf);
                Table table = connection.getTable(TableName.valueOf("wiki"))) {

            Scan scan = new Scan();
            scan.addColumn(CF, ATTR);
            try (ResultScanner rs = table.getScanner(scan) // always close the ResultScanner!
            ) {
                for (Result r = rs.next(); r != null; r = rs.next()) {
                    System.out.println(Arrays.toString(r.getValue(CF, ATTR)));
                }
            }
        }
    }
}
