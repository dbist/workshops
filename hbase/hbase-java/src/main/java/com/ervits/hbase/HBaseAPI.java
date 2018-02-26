/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ervits.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author aervits
 */
public class HBaseAPI {

    private static final Logger LOG = Logger.getLogger(HBaseAPI.class.getName());

    private static final String TABLE_NAME = "table1";
    private static final String CF_DEFAULT = "cf";

    public static void createOrOverwrite(Admin admin, HTableDescriptor table) throws IOException {
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        admin.createTable(table);
    }

    public static void createSchemaTables(Configuration config) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
                Admin admin = connection.getAdmin()) {

            HTableDescriptor table = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
            table.addFamily(new HColumnDescriptor(CF_DEFAULT).setCompressionType(Algorithm.SNAPPY));

            System.out.print("Creating table. ");
            createOrOverwrite(admin, table);
            System.out.println(" Done.");
        }
    }

    public static void modifySchema(Configuration config) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
                Admin admin = connection.getAdmin()) {

            TableName tableName = TableName.valueOf(TABLE_NAME);
            if (admin.tableExists(tableName)) {
                System.out.println("Table does not exist.");
                System.exit(-1);
            }

            HTableDescriptor table = new HTableDescriptor(tableName);

            // Update existing table
            HColumnDescriptor newColumn = new HColumnDescriptor("NEWCF");
            newColumn.setCompactionCompressionType(Algorithm.GZ);
            newColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            admin.addColumn(tableName, newColumn);

            // Update existing column family
            HColumnDescriptor existingColumn = new HColumnDescriptor(CF_DEFAULT);
            existingColumn.setCompactionCompressionType(Algorithm.GZ);
            existingColumn.setMaxVersions(HConstants.ALL_VERSIONS);
            table.modifyFamily(existingColumn);
            admin.modifyTable(tableName, table);

            // Disable an existing table
            admin.disableTable(tableName);

            // Delete an existing column family
            admin.deleteColumn(tableName, CF_DEFAULT.getBytes("UTF-8"));

            // Delete a table (Need to be disabled first)
            admin.deleteTable(tableName);
        }
    }

    public static void write(Configuration config) {

        TableName tableName = TableName.valueOf(TABLE_NAME);
        /**
         * a callback invoked when an asynchronous write fails.
         */
        final BufferedMutator.ExceptionListener listener = 
                (RetriesExhaustedWithDetailsException e, BufferedMutator mutator) -> {
            for (int i = 0; i < e.getNumExceptions(); i++) {
                LOG.log(Level.INFO, "Failed to send put {0}.", e.getRow(i));
            }
        };
        BufferedMutatorParams params = new BufferedMutatorParams(tableName)
                .listener(listener);
        try (Connection connection = ConnectionFactory.createConnection(config);
                final BufferedMutator mutator = connection.getBufferedMutator(params)) {

            List<Put> puts = new ArrayList<>();
            int count = 0;
            Put put;

            for (int i = 0; i < 1000000; i++) {
                String rowKey = UUID.randomUUID().toString();

                put = new Put(Bytes.toBytes(rowKey));
                put.addColumn(Bytes.toBytes(CF_DEFAULT), Bytes.toBytes("cnt"), Bytes.toBytes(count));
                puts.add(put);
                count++;
                
                if((count % 10000) == 0) {
                    mutator.mutate(puts);
                    LOG.log(Level.INFO, "Count: {0}", count);
                    puts.clear();
                }
            }
            mutator.mutate(puts);
            
//            String rowKey = UUID.randomUUID().toString();
//            Put put = new Put(Bytes.toBytes(rowKey));
//            put.addColumn(Bytes.toBytes(CF_DEFAULT), Bytes.toBytes("cnt"), Bytes.toBytes(""));

//            try(Table table = connection.getTable(tableName)) {
//                LOG.info("WRITING");
//                
//                
//            }
//            mutator.mutate(put);
            

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    public static void main(String... args) throws IOException {
        Configuration config = HBaseConfiguration.create();

//        config.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
//        config.set("hbase.zookeeper.property.clientPort", "2181");
//        config.set("zookeeper.znode.parent", "/hbase-unsecure");
        
        //Add any necessary configuration files (hbase-site.xml, core-site.xml)
        //  config.addResource(new Path(System.getenv("HBASE_CONF_DIR"), "hbase-site.xml"));
        //  config.addResource(new Path(System.getenv("HADOOP_CONF_DIR"), "core-site.xml"));
        //createSchemaTables(config);
        //modifySchema(config);
        
        long start = System.currentTimeMillis();
        write(config);
        
        long end = System.currentTimeMillis();
        LOG.log(Level.INFO, "Time: {0}", (end - start)/1000);
    }
}
