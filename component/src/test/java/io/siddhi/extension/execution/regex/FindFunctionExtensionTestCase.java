/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.siddhi.extension.execution.regex;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.exception.SiddhiAppCreationException;
import io.siddhi.core.query.output.callback.QueryCallback;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.util.EventPrinter;
import io.siddhi.core.util.SiddhiTestHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class FindFunctionExtensionTestCase {
    private static final Logger log = LogManager.getLogger(FindFunctionExtensionTestCase.class);
    private AtomicInteger count;
    private volatile boolean eventArrived;

    @BeforeMethod
    public void init() {
        count = new AtomicInteger(0);
        eventArrived = false;
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void testFindFunctionExtensionTestCase1() throws InterruptedException {
        log.info("FindFunctionExtension TestCase with invalid number of arguments");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:find(regex) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void testFindFunctionExtensionTestCase2() throws InterruptedException {
        log.info("FindFunctionExtension TestCase with invalid datatype");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:find(regex, symbol) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void testFindFunctionExtensionTestCase3() throws InterruptedException {
        log.info("FindFunctionExtension TestCase with invalid datatype");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol int, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:find(regex, symbol) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void testFindFunctionExtensionTestCase4() throws InterruptedException {
        log.info("FindFunctionExtension TestCase with invalid datatype");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, "
                + "startingIndex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:find(regex, symbol, startingIndex) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test
    public void testFindFunctionExtensionTestCase5() throws InterruptedException {
        log.info("FindFunctionExtension TestCase with null value");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:find(regex, symbol) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("inputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced by WSO2 currently", 60.5f, null});
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testFindFunctionExtensionTestCase6() throws InterruptedException {
        log.info("FindFunctionExtension TestCase with null value");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, "
                + "startingIndex int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:find(regex, symbol, startingIndex) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    if (count.get() == 2) {
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("inputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{null, 60.5f, "\\d\\d(.*)WSO2", 30});
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently "
                                               + "by WSO2 employees", 60.5f, "\\d\\d(.*)WSO2", null});
        SiddhiTestHelper.waitForEvents(100, 2, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testFindFunctionExtensionTestCase7() throws InterruptedException {
        log.info("FindStartFromIndexFunctionExtension TestCase invalid input value");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, "
                + "startingIndex int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:find(regex, symbol, startingIndex) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime
                (inStreamDefinition + query);

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("inputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently "
                                               + "by WSO2 employees", 60.5f, "\\d\\d(.*)WSO2", "WSO2"});
        siddhiAppRuntime.shutdown();
    }
}
