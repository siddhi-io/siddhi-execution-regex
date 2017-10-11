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
 * group(regex, input.sequence, group.id)
 * This method returns the input sub-sequence captured by the given group during the previous match operation.
 * regex - regular expression. eg: "\d\d(.*)WSO2"
 * inputSequence - input sequence to be matched with the regular expression eg: "21 products are produced by WSO2
 * currently group.id - the given group id of the regex expression eg: 0, 1, 2, etc.
 * Accept Type(s) for group(regex, input.sequence, group.id);
 * regex : STRING
 * input.sequence : STRING
 * group.id : INT
 * Return Type(s): STRING
 */

/**
 * Class representing the Regex Group implementation.
 */
@Extension(
        name = "group",
        namespace = "regex",
        description = "This method returns the input sub-sequence captured by the given group during the previous " +
                "match operation.",
        parameters = {
                @Parameter(name = "regex",
                        description = "regular expression. eg: \\d\\d(.*)WSO2.",
                        type = {DataType.STRING}),
                @Parameter(name = "input.sequence",
                        description = "input sequence to be matched with the regular expression "
                                + "eg: 21 products are produced by WSO2.",
                        type = {DataType.STRING}),
                @Parameter(name = "group.id",
                        description = "the given group id of the regex expression eg: 0, 1, 2, etc.",
                        type = {DataType.INT})
        },
        returnAttributes = @ReturnAttribute(
                description = "Returned type will be string.",
                type = {DataType.STRING}),
        examples = {
                @Example(
                        syntax = "define stream inputStream (inputSequence string, price long, regex string,"
                                + " group int);\n"
                                + "\n"
                                + "from inputStream select inputSequence, regex:group(\\d\\d(.*)(WSO2.*), "
                                + "21 products are produced within 10 years by WSO2 currently by WSO2 employees, 3) \n "
                                + "insert into outputStream;",
                        description = "Returns 'WSO2 employees', input sub-sequence captured by the given groupID, 3 "
                                + "during the previous match operation."
                )
        }
)
public class GroupFunctionExtension extends FunctionExecutor {
    private Attribute.Type returnType = Attribute.Type.STRING;

    //state-variables
    private boolean isRegexConstant = false;
    private String regexConstant;
    private Pattern patternConstant;

    @Override
    protected void init(ExpressionExecutor[] expressionExecutors, ConfigReader configReader,
                        SiddhiAppContext siddhiAppContext) {
        if (attributeExpressionExecutors.length != 3) {
            throw new SiddhiAppValidationException("Invalid no of arguments passed to regex:group() function, " +
                                                   "required 3, but found " + attributeExpressionExecutors.length);
        }
        if (attributeExpressionExecutors[0].getReturnType() != Attribute.Type.STRING) {
            throw new SiddhiAppValidationException("Invalid parameter type found for the first argument of " +
                                                   "str:group() function, " + "required " + Attribute.Type.STRING +
                                                   ", but found " +
                                                   attributeExpressionExecutors[0].getReturnType().toString());
        }
        if (attributeExpressionExecutors[1].getReturnType() != Attribute.Type.STRING) {
            throw new SiddhiAppValidationException("Invalid parameter type found for the second argument of " +
                                                   "str:group() function, " + "required " + Attribute.Type.STRING +
                                                   ", but found " +
                                                   attributeExpressionExecutors[1].getReturnType().toString());
        }
        if (attributeExpressionExecutors[2].getReturnType() != Attribute.Type.INT) {
            throw new SiddhiAppValidationException("Invalid parameter type found for the third argument of " +
                                                   "str:group() function, " + "required " + Attribute.Type.INT +
                                                   ", but found " +
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
            throw new SiddhiAppRuntimeException("Invalid input given to regex:group() function. First argument " +
                                                "cannot be null");
        }
        if (data[1] == null) {
            throw new SiddhiAppRuntimeException("Invalid input given to regex:group() function. Second " +
                                                "argument cannot be null");
        }
        if (data[2] == null) {
            throw new SiddhiAppRuntimeException("Invalid input given to regex:group() function. Third " +
                                                "argument cannot be null");
        }
        String source = (String) data[1];
        int groupId;
        try {
            groupId = (Integer) data[2];
        } catch (ClassCastException ex) {
            throw new SiddhiAppRuntimeException("Invalid input given to regex:group() function. " +
                                                "Third argument should be an integer");
        }

        if (!isRegexConstant) {
            regex = (String) data[0];
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(source);
        } else {
            matcher = patternConstant.matcher(source);
        }

        if (matcher.find() && groupId <= matcher.groupCount()) {
            return matcher.group(groupId);
        } else {
            //cannot terminate the event flow by throwing an exception just because a particular
            // event might not contain a matching group
            return null;
        }
    }

    @Override
    protected Object execute(Object data) {
        return null; //Since the group function takes in 3 parameters, this method does not
        // get called. Hence, not implemented.
    }

    @Override
    public Attribute.Type getReturnType() {
        return returnType;
    }

    @Override
    public Map<String, Object> currentState() {
        Map<String, Object> stateMap = new HashMap<>(3);
        stateMap.put("isRegexConstant", isRegexConstant);
        stateMap.put("regexConstant", regexConstant);
        stateMap.put("patternConstant", patternConstant);
        return stateMap;
    }

    @Override
    public void restoreState(Map<String, Object> state) {
        isRegexConstant = (Boolean) state.get("isRegexConstant");
        regexConstant = (String) state.get("regexConstant");
        patternConstant = (Pattern) state.get("patternConstant");
    }

}
