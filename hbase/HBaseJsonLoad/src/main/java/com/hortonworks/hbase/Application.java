/*
 * Copyright 2015 aervits.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hortonworks.hbase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author aervits
 */
public class Application extends Configured implements Tool {

    /**
     * The identifier for the application table.
     */
    private static final TableName TABLE_NAME = TableName.valueOf("JsonTable");
    /**
     * The name of the column family used by the application.
     */

    private static final byte[] CF = Bytes.toBytes("cf");

    @Override
    public int run(String[] argv) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "sandbox.hortonworks.com");
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");
        conf.set("hbase.master", "sandbox.hortonworks.com:16000");
        conf.set("hbase.zookeeper.property.clientPort", "2181");

        /**
         * Connection to the cluster. A single connection shared by all
         * application threads.
         */
        Connection connection = null;
        /**
         * A lightweight handle to a specific table. Used from a single thread.
         */
        Table table = null;
        try {
            // establish the connection to the cluster.
            connection = ConnectionFactory.createConnection(conf);
            // retrieve a handle to the target table.
            table = connection.getTable(TABLE_NAME);

            List<Put> puts = new ArrayList<>();
            int count = 0;
            
            try (BufferedReader reader = new BufferedReader(new FileReader("./data.json"))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    line = fixJSON(line);
                    JSONObject jobj = new JSONObject(line);
                    
                    // describe the data we want to write.
                    Put p = new Put(Bytes.toBytes(jobj.getString("id")));
                    p.addColumn(CF, Bytes.toBytes("json"), Bytes.toBytes(line));
                    puts.add(p);
                    count++;

                    if ((count % 1000) == 0) {
                        table.put(puts);
                        puts.clear();
                        System.out.printf("Processed: %d\n", count);
                    }
                }
                table.put(puts);

            } catch (JSONException ex) {
                Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            // close everything down
            if (table != null) {
                table.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return 0;
    }

    public static void main(String[] argv) throws Exception {
        int ret = ToolRunner.run(new Application(), argv);
        System.exit(ret);
    }

    private String fixJSON(String line) {
        return line.replace("][", ",\n").replace("[", "").replace("]", "").replace("}},", "}}");
    }

}
