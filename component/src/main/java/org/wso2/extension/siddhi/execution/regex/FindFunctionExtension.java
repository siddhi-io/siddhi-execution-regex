/*
 * Copyright (c)  2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.ReturnAttribute;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.function.FunctionExecutor;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.SiddhiAppValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * find(regex, input.sequence)
 * find(regex, input.sequence, starting.index)
 * These methods attempts to find the next sub-sequence of the 'inputSequence' that matches the 'regex' pattern.
 * regex - regular expression. eg: "\d\d(.*)WSO2"
 * inputSequence - input sequence to be matched with the regular expression eg: "21 products are produced by WSO2
 * currently startingIndex - starting index of the input sequence to start matching the given regex pattern eg: 1, 2
 * Accept Type(s) for find(regex, input.sequence);
 * regex : STRING
 * input.sequence : STRING
 * Accept Type(s) for find(regex, input.sequence, starting.index);
 * regex : STRING
 * input.sequence : STRING
 * starting.index : INT
 * Return Type(s): BOOLEAN
 */

/**
 * Class representing the Regex Find implementation.
 */
@Extension(
        name = "find",
        namespace = "regex",
        description = "These methods attempts to find the next sub-sequence of the 'inputSequence' that matches "
                + "the 'regex' pattern.",
        parameters = {
                @Parameter(name = "regex",
                        description = "regular expression. eg: \\d\\d(.*)WSO2.",
                        type = {DataType.STRING}),
                @Parameter(name = "input.sequence",
                        description = "input sequence to be matched with the regular expression "
                                + "eg: 21 products are produced by WSO2.",
                        type = {DataType.STRING}),
                @Parameter(name = "starting.index",
                        description = "starting index of the input sequence to start matching the given regex pattern"
                                + " eg: 1, 2.",
                        type = {DataType.INT})
        },
        returnAttributes = @ReturnAttribute(
                description = "Returned type will be boolean. Either true or false",
                type = {DataType.BOOL}),
        examples = {
                @Example(
                        syntax = "define stream inputStream (inputSequence string, price long, regex string);\n"
                                + "\n"
                                + "from inputStream select inputSequence , "
                                + "regex:find(\\d\\d(.*)WSO2, 21 products are produced by WSO2 currently) as aboutWSO2 "
                                + "insert into outputStream;\n",
                        description = "This method attempts to find the next sub-sequence of the inputSequence "
                                + "that matches \\d\\d(.*)WSO2 regex  pattern. It returns true as the sub sequence "
                                + "exists."
                ),
                @Example(
                        syntax = "define stream inputStream (inputSequence string, price long, regex string);\n"
                                + "\n"
                                + "from inputStream select inputSequence , "
                                + "regex:find(\\d\\d(.*)WSO2, 21 products are produced currently) as aboutWSO2 "
                                + "insert into outputStream;\n",
                        description = "This method attempts to find the next sub-sequence of the inputSequence "
                                + "that matches \\d\\d(.*)WSO2 regex  pattern. It returns false as the sub sequence "
                                + "does not exists."
                ),
                @Example(
                        syntax = "define stream inputStream (inputSequence string, price long, regex string);\n"
                                + "\n"
                                + "from inputStream select inputSequence , "
                                + "regex:find(\\d\\d(.*)WSO2, 21 products are produced within 10 years by WSO2 "
                                + "currently by WSO2 employees, 30) as aboutWSO2 "
                                + "insert into outputStream;\n",
                        description = "This method attempts to find the next sub-sequence of the inputSequence "
                                + "that matches \\d\\d(.*)WSO2 regex  pattern starting from index 30. "
                                + "It returns true since such a sub sequence exists."
                )
        }
)
public class FindFunctionExtension extends FunctionExecutor {
    private static final Logger log = Logger.getLogger(FindFunctionExtension.class);
    private Attribute.Type returnType = Attribute.Type.BOOL;
    private boolean isRegexConstant = false;
    private String regexConstant;
    private Pattern patternConstant;

    @Override
    protected void init(ExpressionExecutor[] expressionExecutors, ConfigReader configReader,
                        SiddhiAppContext siddhiAppContext) {
        if (attributeExpressionExecutors.length != 2 && attributeExpressionExecutors.length != 3) {
            throw new SiddhiAppValidationException("Invalid no of arguments passed to regex:find() function, " +
                                                   "required 2 or 3, " + "but found " +
                                                   attributeExpressionExecutors.length);
        } else {
            if (attributeExpressionExecutors[0].getReturnType() != Attribute.Type.STRING) {
                throw new SiddhiAppValidationException("Invalid parameter type found for the first argument of " +
                                                       "regex:find() function, " + "required " +
                                                       Attribute.Type.STRING + ", but found " +
                                                       attributeExpressionExecutors[0].getReturnType().toString());
            }
            if (attributeExpressionExecutors[1].getReturnType() != Attribute.Type.STRING) {
                throw new SiddhiAppValidationException("Invalid parameter type found for the second argument of " +
                                                       "regex:find() function, " + "required " +
                                                       Attribute.Type.STRING + ", but found " +
                                                       attributeExpressionExecutors[1].getReturnType().toString());
            }
            if (attributeExpressionExecutors.length == 3) {
                if (attributeExpressionExecutors[2].getReturnType() != Attribute.Type.INT) {
                    throw new SiddhiAppValidationException("Invalid parameter type found for the " + "third " +
                                                           "argument of str:find() function, " +
                                                           "required " + Attribute.Type.INT + ", but found " +
                                                           attributeExpressionExecutors[1].
                                                                   getReturnType().toString());
                }
            }
        }

        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor) {
            isRegexConstant = true;
            regexConstant = (String) ((ConstantExpressionExecutor) attributeExpressionExecutors[0]).getValue();
            patternConstant = Pattern.compile(regexConstant);
        }
    }

    @Override
    protected Object execute(Object[] data) {
        String regex;
        Pattern pattern;
        Matcher matcher;

        if (data[0] == null) {
            throw new SiddhiAppRuntimeException("Invalid input given to regex:find() function. " +
                                                "First argument cannot be null");
        }
        if (data[1] == null) {
            if (log.isDebugEnabled()) {
                log.warn("Invalid input given to regex:find() function. " +
                         "Second argument cannot be null, returning false");
            }
            return false;
        }

        String source = (String) data[1];

        if (!isRegexConstant) {
            regex = (String) data[0];
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(source);

        } else {
            matcher = patternConstant.matcher(source);

        }


        if (data.length == 2) {
            return matcher.find();
        } else {
            if (data[2] == null) {
                if (log.isDebugEnabled()) {
                    log.warn("Invalid input given to regex:find() function. " +
                             "Second argument cannot be null, returning false");
                }
                return false;
            }
            int startingIndex;
            try {
                startingIndex = (Integer) data[2];
            } catch (ClassCastException ex) {
                throw new SiddhiAppRuntimeException("Invalid input given to regex:group() function. " +
                                                    "Third argument should be an integer");
            }
            return matcher.find(startingIndex);
        }
    }

    @Override
    protected Object execute(Object data) {
        return null;  //Since the find function takes in 2 parameters,
        // this method does not get called. Hence, not implemented.
    }

    @Override
    public Attribute.Type getReturnType() {
        return returnType;
    }

    @Override
    public Map<String, Object> currentState() {
        return new HashMap<String, Object>() {
            {
                put("isRegexConstant", isRegexConstant);
                put("regexConstant", regexConstant);
                put("patternConstant", patternConstant);
            }
        };
    }

    @Override
    public void restoreState(Map<String, Object> state) {
        isRegexConstant = (Boolean) state.get("isRegexConstant");
        regexConstant = (String) state.get("regexConstant");
        patternConstant = (Pattern) state.get("patternConstant");
    }
}
