/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.extension.siddhi.execution.regex;

import org.apache.log4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.core.util.SiddhiTestHelper;
import org.wso2.siddhi.core.util.persistence.InMemoryPersistenceStore;
import org.wso2.siddhi.core.util.persistence.PersistenceStore;

import java.util.concurrent.atomic.AtomicInteger;


public class RegexPersistanceTestCase {
    private static final Logger log = Logger.getLogger(RegexPersistanceTestCase.class);
    private AtomicInteger count;
    private volatile boolean eventArrived;

    @BeforeMethod
    public void init() {
        count = new AtomicInteger(0);
        eventArrived = false;
    }

    @Test
    public void testFindFunctionExtensionPersistenceTestCase() throws InterruptedException {
        log.info("FindFunctionExtension TestCase");
        PersistenceStore persistenceStore = new InMemoryPersistenceStore();
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setPersistenceStore(persistenceStore);

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:find('\\d\\d(.*)WSO2', symbol) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        QueryCallback queryCallback = new QueryCallback() {
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
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    if (count.get() == 4) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        };
        executionPlanRuntime.addCallback("query1", queryCallback);
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced by WSO2 currently", 60.5f, "WSO2"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f, "WSO2"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f, "WSO2"});

        //persisting
        SiddhiTestHelper.waitForEvents(500, 3, count, 60000);
        executionPlanRuntime.persist();
        //restarting execution plan
        executionPlanRuntime.shutdown();
        inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        //loading
        executionPlanRuntime.restoreLastRevision();
        inputHandler.send(new Object[]{"21 products are produced by WSO2 currently", 60.5f, "\\d(.*)WSO22"});
        SiddhiTestHelper.waitForEvents(500, 4, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testGroupFunctionExtensionPersistence() throws InterruptedException {
        log.info("GroupFunctionExtension Persistence testcase");
        PersistenceStore persistenceStore = new InMemoryPersistenceStore();
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setPersistenceStore(persistenceStore);

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, group int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group('(\\d\\d)(.*)(WSO2.*)', symbol, group) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        QueryCallback queryCallback = new QueryCallback() {
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
                        AssertJUnit.assertEquals(null, inEvent.getData(1));
                    }
                    if (count.get() == 4) {
                        AssertJUnit.assertEquals(null, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        };
        executionPlanRuntime.addCallback("query1", queryCallback);
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 "
                                               + "employees", 60.5f, "WSO2", 3});
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 "
                                               + "employees", 60.5f, "WSO2", 1});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f,
                                       "WSO2", 1});
        SiddhiTestHelper.waitForEvents(500, 3, count, 60000);
        executionPlanRuntime.persist();
        //restarting execution plan
        executionPlanRuntime.shutdown();
        inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        //loading
        executionPlanRuntime.restoreLastRevision();
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f,
                                       "WSO2", 2});
        SiddhiTestHelper.waitForEvents(500, 4, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testLookingAtFunctionExtensionPersistence() throws InterruptedException {
        log.info("LookingAtFunctionExtension Persistence TestCase");
        PersistenceStore persistenceStore = new InMemoryPersistenceStore();
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setPersistenceStore(persistenceStore);

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:lookingAt('WSO2(.*)middleware', symbol) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        QueryCallback queryCallback = new QueryCallback() {
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
                    if (count.get() == 3) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    if (count.get() == 4) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        };
        executionPlanRuntime.addCallback("query1", queryCallback);
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced by WSO2 currently in Sri Lanka", 60.5f, "WSO2"});
        inputHandler.send(new Object[]{null , 60.5f, "WSO2"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f, "WSO2"});
        SiddhiTestHelper.waitForEvents(500, 3, count, 60000);
        executionPlanRuntime.persist();
        //restarting execution plan
        executionPlanRuntime.shutdown();
        inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        //loading
        executionPlanRuntime.restoreLastRevision();
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f, "WSO2"});
        SiddhiTestHelper.waitForEvents(500, 4, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testMatchesFunctionExtensionPersistence() throws InterruptedException {
        log.info("MatchesFunctionExtension Persistence TestCase");
        PersistenceStore persistenceStore = new InMemoryPersistenceStore();
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.setPersistenceStore(persistenceStore);

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:matches('WSO2(.*)middleware', symbol) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        QueryCallback queryCallback = new QueryCallback() {
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
                    if (count.get() == 3) {
                        AssertJUnit.assertEquals(true, inEvent.getData(1));
                    }
                    if (count.get() == 4) {
                        AssertJUnit.assertEquals(false, inEvent.getData(1));
                    }
                    eventArrived = true;
                }
            }
        };
        executionPlanRuntime.addCallback("query1", queryCallback);
        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{null, 60.5f, "WSO2"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f, "WSO2"});
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware", 60.5f, "WSO2"});
        SiddhiTestHelper.waitForEvents(500, 3, count, 60000);
        executionPlanRuntime.persist();
        //restarting execution plan
        executionPlanRuntime.shutdown();
        inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        //loading
        executionPlanRuntime.restoreLastRevision();
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f, "WSO2"});
        SiddhiTestHelper.waitForEvents(500, 4, count, 60000);
        AssertJUnit.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
}
