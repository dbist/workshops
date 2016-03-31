/*
 * Copyright 2016 aervits.
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
package com.ervits.mapreduce;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author aervits
 */
public class XmlToJsonMapper extends Mapper<Object, Text, Text, NullWritable> {

    private static final Logger LOG = Logger.getLogger(XmlToJsonMapper.class.getName());

    @Override
    public void map(Object key, Text value, Context context
    ) throws IOException, InterruptedException {
        final String xml_data = value.toString();

        try {
            JSONObject xml_to_json = XML.toJSONObject(xml_data);
            String json_string = xml_to_json.toString();
            context.write(new Text(json_string), NullWritable.get());
        } catch (JSONException ex) {
            LOG.log(Level.SEVERE, "ERROR in Mapper", ex);
        }
    }
}