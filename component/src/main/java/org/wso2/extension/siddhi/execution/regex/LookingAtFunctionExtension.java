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
 * lookingAt(regex, input.sequence)
 * This method attempts to match the 'inputSequence', starting at the beginning, against the 'regex' pattern.
 * regex - regular expression. eg: "\d\d(.*)WSO2"
 * input.sequence - input sequence to be matched with the regular expression eg: "21 products are produced by WSO2
 * currently Accept Type(s) for lookingAt(regex, inputSequence);
 * regex : STRING
 * input.sequence : STRING
 * Return Type(s): BOOLEAN
 */

/**
 * Class representing the Regex LookingAt implementation.
 */
@Extension(
        name = "lookingAt",
        namespace = "regex",
        description = "This method attempts to match the 'inputSequence', starting at the beginning, against the "
                + "'regex' pattern.",
        parameters = {
                @Parameter(name = "regex",
                        description = "regular expression. eg: \\d\\d(.*)WSO2.",
                        type = {DataType.STRING}),
                @Parameter(name = "input.sequence",
                        description = "input sequence to be matched with the regular expression "
                                + "eg: 21 products are produced by WSO2.",
                        type = {DataType.STRING})
        },
        returnAttributes = @ReturnAttribute(
                description = "Returned type will be boolean.",
                type = {DataType.BOOL}),
        examples = {
                @Example(
                        syntax = "define stream inputStream (inputSequence string, price long, regex string,"
                                + " group int);\n"
                                + "\n"
                                + "from inputStream select inputSequence, regex:lookingAt(\\d\\d(.*)(WSO2.*), "
                                + "21 products are produced by WSO2 currently in Sri Lanka)",
                        description = "This method attempts to match the inputSequence against \\d\\d(.*)(WSO2.*) "
                                + "regex pattern starting at the beginning. Since it matches, returns true."
                ),
                @Example(
                        syntax = "define stream inputStream (inputSequence string, price long, regex string,"
                                + " group int);\n"
                                + "\n"
                                + "from inputStream select inputSequence, regex:lookingAt(WSO2(.*)middleware(.*), "
                                + "sample test string and WSO2 is situated in trace and its a middleware company)",
                        description = "This method attempts to match the inputSequence against WSO2(.*)middleware(.*) "
                                + "regex pattern starting at the beginning. Since it does not match, returns false."
                )
        }
)
public class LookingAtFunctionExtension extends FunctionExecutor {
    private Attribute.Type returnType = Attribute.Type.BOOL;
    private static final Logger log = Logger.getLogger(LookingAtFunctionExtension.class);

    //state-variables
    private boolean isRegexConstant = false;
    private String regexConstant;
    private Pattern patternConstant;

    @Override
    protected void init(ExpressionExecutor[] expressionExecutors, ConfigReader configReader,
                        SiddhiAppContext siddhiAppContext) {
        if (attributeExpressionExecutors.length != 2) {
            throw new SiddhiAppValidationException("Invalid no of arguments passed to regex:lookingAt() " +
                                                   "function, " + "required 2, " + "but found " +
                                                   attributeExpressionExecutors.length);
        }
        if (attributeExpressionExecutors[0].getReturnType() != Attribute.Type.STRING) {
            throw new SiddhiAppValidationException("Invalid parameter type found for the first argument of " +
                                                   "regex:lookingAt() function, " + "required " +
                                                   Attribute.Type.STRING + ", but found " +
                                                   attributeExpressionExecutors[0].getReturnType().toString());
        }
        if (attributeExpressionExecutors[1].getReturnType() != Attribute.Type.STRING) {
            throw new SiddhiAppValidationException("Invalid parameter type found for the second argument of " +
                                                   "regex:lookingAt() function, " + "required " +
                                                   Attribute.Type.STRING + ", but found " +
                                                   attributeExpressionExecutors[1].getReturnType().toString());
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
            throw new SiddhiAppRuntimeException("Invalid input given to regex:lookingAt() function. " +
                                                "First argument cannot be null");
        }
        if (data[1] == null) {
            if (log.isDebugEnabled()) {
                log.warn("Invalid input given to regex:lookingAt() function. Second argument cannot be null, " +
                         "returning false");
            }
            return false;
        }
        String source = (String) data[1];

        if (!isRegexConstant) {
            regex = (String) data[0];
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(source);
            return matcher.lookingAt();

        } else {
            matcher = patternConstant.matcher(source);
            return matcher.lookingAt();
        }
    }

    @Override
    protected Object execute(Object data) {
        return null;  //Since the lookingAt function takes in 2 parameters, this method does not
        // get called. Hence, not implemented.
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
