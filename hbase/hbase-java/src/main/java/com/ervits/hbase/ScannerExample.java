/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ervits.hbase;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Table;

/**
 *
 * @author aervits
 */
public class ScannerExample {

    public static void main(String[] args) throws IOException {
        Configuration conf = HBaseConfiguration.create();

        // conf.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
        // conf.set("hbase.zookeeper.property.clientPort", "2181");
        // conf.set("zookeeper.znode.parent", "/hbase-unsecure");
        try (Connection connection = ConnectionFactory.createConnection(conf);
                Table table = connection.getTable(TableName.valueOf("wiki"))) {
            String col = "title";
            ResultScanner scanner = table.getScanner(col.getBytes());
            while (true) {
                if (scanner.next() != null) {
                    Result result = scanner.next();
                    System.out.println(result.toString());
                }

            }

        }

    }
}
