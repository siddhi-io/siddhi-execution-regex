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
import io.siddhi.core.exception.SiddhiAppCreationException;
import io.siddhi.core.stream.input.InputHandler;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class GroupFunctionExtensionTestCase {
    private static final Logger log = Logger.getLogger(GroupFunctionExtensionTestCase.class);

    @Test (expectedExceptions = SiddhiAppCreationException.class)
    public void testGroupFunctionExtension1() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase with invalid number of arguments");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, group int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group(regex, symbol) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test (expectedExceptions = SiddhiAppCreationException.class)
    public void testGroupFunctionExtension2() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase with invalid datatype");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex int, group int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group(regex, symbol, group) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test (expectedExceptions = SiddhiAppCreationException.class)
    public void testGroupFunctionExtension3() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase with invalid datatype");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol int, price long, regex string, group int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group(regex, symbol, group) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test (expectedExceptions = SiddhiAppCreationException.class)
    public void testGroupFunctionExtension4() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase with invalid datatype");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, "
                + "group string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group(regex, symbol, group) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test
    public void testGroupFunctionExtension5() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase with null value");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, group int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group(regex, symbol, group) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 "
                                               + "employees", 60.5f, null, 3});
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testGroupFunctionExtension6() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase with null value");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, group int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group(regex, symbol, group) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[]{null, 60.5f, "(\\d\\d)(.*)(WSO2.*)", 3});
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testGroupFunctionExtension7() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase with null value");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, group int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group(regex, symbol, group) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("inputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 "
                                               + "employees", 60.5f, "(\\d\\d)(.*)(WSO2.*)", null});
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void testGroupFunctionExtension8() throws InterruptedException {
        log.info("GroupFunctionExtensionTestCase TestCase invalid input value");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string, group int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:group(regex, symbol, group) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("inputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{"21 products are produced within 10 years by WSO2 currently by WSO2 "
                                               + "employees", 60.5f, "(\\d\\d)(.*)(WSO2.*)", "WSO2"});
        siddhiAppRuntime.shutdown();
    }

}
