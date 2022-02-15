/*
 * Copyright (c)  2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


public class RegExFunctionExtensionTestCase {
    private static final Logger log = LogManager.getLogger(RegExFunctionExtensionTestCase.class);
    private AtomicInteger count;
    private volatile boolean eventArrived;

    @BeforeMethod
    public void init() {
        count = new AtomicInteger(0);
        eventArrived = false;
    }

    @Test
    public void testFindFunctionExtensionTestCase() throws InterruptedException {
        log.info("FindFunctionExtension TestCase");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                        "from inputStream " +
                        "select symbol, regex:find(regex, symbol) as aboutWSO2 " +
                        "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime =
                siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    if (count.get() == 2) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    if (count.get() == 3) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    if (count.get() == 4) {
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced by WSO2 currently", 60.5f, "\\d\\d(.*)WSO2"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a " +
                                       "middleware company", 60.5f, "WSO2(.*)middleware(.*)"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a " +
                                       "middleware company", 60.5f, "WSO2(.*)middleware"});
        inputHandler.send(new Object[]{"21 products are produced by WSO2 currently", 60.5f, "\\d(.*)WSO22"});
        SiddhiTestHelper.waitForEvents(100, 4, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testFindStartFromIndexFunctionExtensionTestCase() throws InterruptedException {
        log.info("FindStartFromIndexFunctionExtension TestCase");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, " +
                                    "price long, regex string, startingIndex int);";
        String query = ("@info(name = 'query1') " +
                        "from inputStream " +
                        "select symbol , regex:find(regex, symbol, startingIndex) as aboutWSO2 " +
                        "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime =
                siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    if (count.get() == 2) {
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 employees",
                                       60.5f, "\\d\\d(.*)WSO2", 30});
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 employees",
                                       60.5f, "\\d\\d(.*)WSO2", 35});
        SiddhiTestHelper.waitForEvents(100, 2, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testMatchesFunctionExtension() throws InterruptedException {
        log.info("MatchesFunctionExtension TestCase");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                        "from inputStream " +
                        "select symbol , regex:matches(regex, symbol) as aboutWSO2 " +
                        "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime =
                siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    if (count.get() == 2) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    if (count.get() == 3) {
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced by WSO2 currently", 60.5f, "\\d\\d(.*)WSO2"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f,
                                       "WSO2(.*)middleware(.*)"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f,
                                       "WSO2(.*)middleware"});
        SiddhiTestHelper.waitForEvents(100, 3, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testLookingAtFunctionExtension() throws InterruptedException {
        log.info("LookingAtFunctionExtension TestCase");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                        "from inputStream " +
                        "select symbol , regex:lookingAt(regex, symbol) as aboutWSO2 " +
                        "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime =
                siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    if (count.get() == 2) {
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    if (count.get() == 3) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced by " +
                                       "WSO2 currently in Sri Lanka", 60.5f, "\\d\\d(.*)WSO2"});
        inputHandler.send(new Object[]{"sample test string and WSO2 is situated in trace and its a middleware company",
                                       60.5f, "WSO2(.*)middleware(.*)"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its " +
                                       "a middleware company", 60.5f, "WSO2(.*)middleware"});
        SiddhiTestHelper.waitForEvents(100, 3, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testGroupFunctionExtension() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, group int);";
        String query = ("@info(name = 'query1') " +
                        "from inputStream " +
                        "select symbol , regex:group(regex, symbol, group) as aboutWSO2 " +
                        "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime =
                siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        AssertJUnit.assertEquals("WSO2 employees", inEvent.getData(1));
                    }
                    if (count.get() == 2) {
                        AssertJUnit.assertEquals("21", inEvent.getData(1));
                    }
                    if (count.get() == 3) {
                        AssertJUnit.assertEquals(" is situated in trace and its a ", inEvent.getData(1));
                    }
                    if (count.get() == 4) {
                        AssertJUnit.assertEquals(null, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 employees",
                                       60.5f, "(\\d\\d)(.*)(WSO2.*)", 3});
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 employees",
                                       60.5f, "(\\d\\d)(.*)(WSO2.*)", 1});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and " +
                                       "its a middleware company", 60.5f, "WSO2(.*)middleware", 1});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and " +
                                       "its a middleware company", 60.5f, "WSO2(.*)middleware", 2});
        SiddhiTestHelper.waitForEvents(100, 4, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
}
