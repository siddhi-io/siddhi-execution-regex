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


package org.wso2.extension.siddhi.execution.regex;

import io.siddhi.annotation.Example;
import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.ReturnAttribute;
import io.siddhi.annotation.util.DataType;
import io.siddhi.core.config.SiddhiQueryContext;
import io.siddhi.core.exception.SiddhiAppRuntimeException;
import io.siddhi.core.executor.ConstantExpressionExecutor;
import io.siddhi.core.executor.ExpressionExecutor;
import io.siddhi.core.executor.function.FunctionExecutor;
import io.siddhi.core.util.config.ConfigReader;
import io.siddhi.core.util.snapshot.state.State;
import io.siddhi.core.util.snapshot.state.StateFactory;
import io.siddhi.query.api.definition.Attribute;
import io.siddhi.query.api.exception.SiddhiAppValidationException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * matches(regex, input.sequence)
 * This method attempts to match the entire 'inputSequence' against the 'regex' pattern.
 * regex - regular expression. eg: "\d\d(.*)WSO2"
 * input.sequence - input sequence to be matched with the regular expression eg: "21 products are produced by WSO2
 * currently Accept Type(s) for matches(regex, inputSequence);
 * regex : STRING
 * inputSequence : STRING
 * Return Type(s): BOOLEAN
 */

/**
 * Class representing the Regex Matches implementation.
 */
@Extension(
        name = "matches",
        namespace = "regex",
        description = "This method attempts to match the entire 'inputSequence' against the 'regex' pattern.",
        parameters = {
                @Parameter(name = "regex",
                        description = "A regular expression. For example, \\d\\d(.*)WSO2.",
                        type = {DataType.STRING}),
                @Parameter(name = "input.sequence",
                        description = "The input sequence to be matched with the regular expression. "
                                + "For example, 21 products are produced by WSO2.",
                        type = {DataType.STRING})
        },
        returnAttributes = @ReturnAttribute(
                description = "The value returned is of 'boolean' type, i.e., either 'true' or 'false'.",
                type = {DataType.BOOL}),
        examples = {
                @Example(
                        syntax = "define stream InputStream (inputSequence string, price long, regex string,"
                                + " group int);\n"
                                + "\n"
                                + "from InputStream select inputSequence, regex:matches(WSO2(.*)middleware(.*), "
                                + "WSO2 is situated in trace and its a middleware company)",
                        description = "This method attempts to match the entire 'inputSequence' against " +
                                "WSO2(.*)middleware(.*) regex pattern. Since it matches, it returns 'true'."
                ),
                @Example(
                        syntax = "define stream inputStream (inputSequence string, price long, regex string,"
                                + " group int);\n"
                                + "\n"
                                + "from inputStream select inputSequence, regex:matches(WSO2(.*)middleware, "
                                + "WSO2 is situated in trace and its a middleware company)",
                        description = "This method attempts to match the entire 'inputSequence' against " +
                                "WSO2(.*)middleware regex pattern. Since it does not match, it returns 'false'."
                )
        }
)
public class MatchesFunctionExtension extends FunctionExecutor<MatchesFunctionExtension.ExtensionState> {

    private Attribute.Type returnType = Attribute.Type.BOOL;
    private static final Logger log = Logger.getLogger(MatchesFunctionExtension.class);

    //state-variables
    private boolean isRegexConstant = false;
    private String regexConstant;
    private Pattern patternConstant;

    @Override
    protected StateFactory<ExtensionState> init(ExpressionExecutor[] attributeExpressionExecutors,
                                                ConfigReader configReader,
                                                SiddhiQueryContext siddhiQueryContext) {
        if (attributeExpressionExecutors.length != 2) {
            throw new SiddhiAppValidationException("Invalid no of arguments passed to regex:matches() " +
                    "function, required 2, " +
                    "but found " + attributeExpressionExecutors.length);
        }
        if (attributeExpressionExecutors[0].getReturnType() != Attribute.Type.STRING) {
            throw new SiddhiAppValidationException("Invalid parameter type found for the first argument of " +
                    "regex:matches() function, " +
                    "required " + Attribute.Type.STRING + ", but found " +
                    attributeExpressionExecutors[0].getReturnType().toString());
        }
        if (attributeExpressionExecutors[1].getReturnType() != Attribute.Type.STRING) {
            throw new SiddhiAppValidationException("Invalid parameter type found for the second argument of " +
                    "regex:matches() function, " +
                    "required " + Attribute.Type.STRING + ", but found " +
                    attributeExpressionExecutors[1].getReturnType().toString());
        }
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor) {
            isRegexConstant = true;
            regexConstant = (String) ((ConstantExpressionExecutor) attributeExpressionExecutors[0]).getValue();
            patternConstant = Pattern.compile(regexConstant);
        }
        return () -> new ExtensionState();
    }

    @Override
    protected Object execute(Object[] data, ExtensionState extensionState) {
        String regex;
        Pattern pattern;
        Matcher matcher;

        if (data[0] == null) {
            throw new SiddhiAppRuntimeException("Invalid input given to regex:matches() function. " +
                    "First argument cannot be null");
        }
        if (data[1] == null) {
            if (log.isDebugEnabled()) {
                log.warn("Invalid input given to regex:matches() function. Second argument " +
                        "cannot be null, returning false");
            }
            return false;
        }
        String source = (String) data[1];

        if (!isRegexConstant) {
            regex = (String) data[0];
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(source);
            return matcher.matches();

        } else {
            matcher = patternConstant.matcher(source);
            return matcher.matches();
        }
    }

    @Override
    protected Object execute(Object o, ExtensionState extensionState) {
        return null;  //Since the matches function takes in 2 parameters, this method does
        // not get called. Hence, not implemented.
    }

    @Override
    public Attribute.Type getReturnType() {
        return returnType;
    }

    class ExtensionState extends State {

        @Override
        public boolean canDestroy() {
            return false;
        }

        @Override
        public Map<String, Object> snapshot() {
            Map<String, Object> stateMap = new HashMap<>(3);
            stateMap.put("isRegexConstant", isRegexConstant);
            stateMap.put("regexConstant", regexConstant);
            stateMap.put("patternConstant", patternConstant);
            return stateMap;
        }

        @Override
        public void restore(Map<String, Object> state) {
            isRegexConstant = (Boolean) state.get("isRegexConstant");
            regexConstant = (String) state.get("regexConstant");
            patternConstant = (Pattern) state.get("patternConstant");
        }
    }
}
