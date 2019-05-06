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

public class LookingAtFunctionExtensionTestCase {
    private static final Logger log = Logger.getLogger(LookingAtFunctionExtensionTestCase.class);

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void testLookingAtFunctionExtension1() throws InterruptedException {
        log.info("LookingAtFunctionExtension TestCase with invalid number of arguments");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:lookingAt(regex) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void testLookingAtFunctionExtension2() throws InterruptedException {
        log.info("LookingAtFunctionExtension TestCase with invalid datatype");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex int);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:lookingAt(regex, symbol) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void testLookingAtFunctionExtension3() throws InterruptedException {
        log.info("LookingAtFunctionExtension TestCase with invalid datatype");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol int, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:lookingAt(regex, symbol) as aboutWSO2 " +
                "insert into outputStream;");
        siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);
    }

    @Test
    public void testLookingAtFunctionExtension4() throws InterruptedException {
        log.info("LookingAtFunctionExtension TestCase with null value");
        SiddhiManager siddhiManager = new SiddhiManager();

        String inStreamDefinition = "define stream inputStream (symbol string, price long, regex string);";
        String query = ("@info(name = 'query1') " +
                "from inputStream " +
                "select symbol , regex:lookingAt(regex, symbol) as aboutWSO2 " +
                "insert into outputStream;");
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(inStreamDefinition + query);

        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("inputStream");
        siddhiAppRuntime.start();
        inputHandler.send(new Object[]{"WSO2 is situated in trace and its a middleware company", 60.5f,
                                       null});
        siddhiAppRuntime.shutdown();
    }
}
