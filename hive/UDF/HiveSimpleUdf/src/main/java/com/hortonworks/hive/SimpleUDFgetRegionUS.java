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
package com.hortonworks.hive;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 *
 * @author aervits
 */
public class SimpleUDFgetRegionUS extends UDF {

    private final Set NORTHEAST = new HashSet(Arrays.asList("Connecticut",
            "Maine", "Massachusetts", "New Hampshire", "Rhode Island",
            "Vermont", "New Jersey", "New York", "Pennsylvania"));

    private final Set MIDWEST = new HashSet(Arrays.asList("Illinois", "Indiana",
            "Michigan", "Ohio", "Wisconsin", "Iowa", "Kansas", "Minnesota",
            "Missouri", "Nebraska", "North Dakota", "South Dakota"));

    private final Set SOUTH = new HashSet(Arrays.asList("Delaware", "Florida",
            "Georgia", "Maryland", "North Carolina", "South Carolina",
            "Virginia", "Washington D.C.", "West Virginia", "Alabama",
            "Kentucky", "Mississippi", "Tennessee", "Arkansas", "Louisiana",
            "Oklahoma", "Texas"));

    private final Set WEST = new HashSet(Arrays.asList("Arizona",
            "Colorado", "Idaho", "Montana", "Nevada", "New Mexico", "Utah",
            "Wyoming", "Alaska", "California", "Hawaii", "Oregon",
            "Washington"));

    private final HashMap<Set, String> regions = new HashMap<>();

    private void fillMap() {
        regions.put(NORTHEAST, "NorthEast");
        regions.put(MIDWEST, "MidWest");
        regions.put(SOUTH, "South");
        regions.put(WEST, "West");
    }

    public Text evaluate(Text input) {
        String result = null;
        
        Objects.requireNonNull(input.toString(), "input cannot be null");
        
        fillMap();
        for (Entry<Set, String> region : regions.entrySet()) {
            if (region.getKey().contains(WordUtils.capitalize(input.toString().toLowerCase()))) {
                result = region.getValue();
            }
        }
        return new Text(Objects.requireNonNull(result, "region cannot be null"));
    }
}
