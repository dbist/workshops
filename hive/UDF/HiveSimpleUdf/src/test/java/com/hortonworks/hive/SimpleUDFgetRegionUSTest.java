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

import junit.framework.TestCase;
import org.apache.hadoop.io.Text;
import static org.junit.Assert.assertNotEquals;

/**
 *
 * @author aervits
 */
public class SimpleUDFgetRegionUSTest extends TestCase {

    public SimpleUDFgetRegionUSTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of evaluate method, of class SimpleUDFgetRegionUS.
     */
    public void testEvaluateWest() {
        System.out.println("evaluate");
        Text input = new Text("California");
        SimpleUDFgetRegionUS instance = new SimpleUDFgetRegionUS();
        Text expResult = new Text("West");
        Text result = instance.evaluate(input);
        assertEquals(expResult, result);
    }

    /**
     * Test of evaluate method, of class SimpleUDFgetRegionUS.
     */
    public void testEvaluateNorthEast() {
        System.out.println("evaluate");
        Text input = new Text("New Jersey");
        SimpleUDFgetRegionUS instance = new SimpleUDFgetRegionUS();
        Text expResult = new Text("NorthEast");
        Text result = instance.evaluate(input);
        assertEquals(expResult, result);
    }

    /**
     * Test of evaluate method, of class SimpleUDFgetRegionUS.
     */
    public void testEvaluateFailureMidWest() {
        System.out.println("evaluate");
        Text input = new Text("New Jersey");
        SimpleUDFgetRegionUS instance = new SimpleUDFgetRegionUS();
        Text expResult = new Text("MidWest");
        Text result = instance.evaluate(input);
        assertNotEquals(expResult.toString(), result.toString());
    }

    /**
     * Test of evaluate method, of class SimpleUDFgetRegionUS.
     */
    public void testEvaluateFailureSouth() {
        System.out.println("evaluate");
        Text input = new Text("Washington");
        SimpleUDFgetRegionUS instance = new SimpleUDFgetRegionUS();
        Text expResult = new Text("South");
        Text result = instance.evaluate(input);
        assertNotEquals(expResult.toString(), result.toString());
    }

    /**
     * Test of evaluate method, of class SimpleUDFgetRegionUS.
     */
    public void testEvaluateAllCapitals() {
        System.out.println("evaluate");
        Text input = new Text("IOWA");
        SimpleUDFgetRegionUS instance = new SimpleUDFgetRegionUS();
        Text expResult = new Text("MidWest");
        Text result = instance.evaluate(input);
        assertEquals(expResult.toString(), result.toString());
    }

    /**
     * Test of evaluate method, of class SimpleUDFgetRegionUS.
     */
    public void testEvaluateAllLowerLetters() {
        System.out.println("evaluate");
        Text input = new Text("Idaho");
        SimpleUDFgetRegionUS instance = new SimpleUDFgetRegionUS();
        Text expResult = new Text("West");
        Text result = instance.evaluate(input);
        assertEquals(expResult.toString(), result.toString());
    }

}
