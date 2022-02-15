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

import io.siddhi.annotation.Example;
import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.ParameterOverload;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing the Regex Group implementation.
 *
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
@Extension(
        name = "group",
        namespace = "regex",
        description = "Returns the subsequence captured by the given group during the regex " +
                "match operation.",
        parameters = {
                @Parameter(name = "regex",
                        description = "A regular expression. For example, `\\d\\d(.*)WSO2.`",
                        type = {DataType.STRING},
                        dynamic = true),
                @Parameter(name = "input.sequence",
                        description = "The input sequence to be matched with the regular expression. "
                                + "For example, 2`1 products are produced by WSO2`.",
                        type = {DataType.STRING},
                        dynamic = true),
                @Parameter(name = "group.id",
                        description = "The given group id of the regex expression. For example, `2`.",
                        type = {DataType.INT},
                        dynamic = true)
        },
        parameterOverloads = {
                @ParameterOverload(parameterNames = {"regex", "input.sequence", "group.id"})
        },
        returnAttributes = @ReturnAttribute(
                description = "The string matching the regex group.",
                type = {DataType.STRING}),
        examples = {
                @Example(
                        syntax = "regex:group('\\d\\d(.*)(WSO2.*)(WSO2.*)', "
                                    + "'21 products are produced within 10 years by WSO2 currently " +
                                "by WSO2 employees', 3)",
                        description = "Function returns 'WSO2 employees', the subsequence captured by the  groupID 3 " +
                                "according to the regex pattern, `\\d\\d(.*)(WSO2.*)(WSO2.*)`."

                )
        }
)
public class GroupFunctionExtension extends FunctionExecutor<GroupFunctionExtension.ExtensionState> {
    private Attribute.Type returnType = Attribute.Type.STRING;
    private static final long serialVersionUID = 1L;

    @Override
    protected StateFactory<ExtensionState> init(ExpressionExecutor[] attributeExpressionExecutors,
                                                ConfigReader configReader,
                                                SiddhiQueryContext siddhiQueryContext) {
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
            String regexConstant = (String) ((ConstantExpressionExecutor) attributeExpressionExecutors[0]).getValue();
            Pattern patternConstant = Pattern.compile(regexConstant);
            return () -> new GroupFunctionExtension.ExtensionState(true, regexConstant, patternConstant);
        }
        return () -> new ExtensionState(false, null, null);
    }

    @Override
    protected Object execute(Object[] data, ExtensionState extensionState) {
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

        if (!extensionState.isRegexConstant) {
            regex = (String) data[0];
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(source);
        } else {
            matcher = extensionState.patternConstant.matcher(source);
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
    protected Object execute(Object data, ExtensionState extensionState) {
        return null; //Since the group function takes in 3 parameters, this method does not
        // get called. Hence, not implemented.
    }

    @Override
    public Attribute.Type getReturnType() {
        return returnType;
    }

    static class ExtensionState extends State {

        private boolean isRegexConstant;
        private String regexConstant;
        private Pattern patternConstant;

        private ExtensionState(boolean isRegexConstant, String regexConstant, Pattern patternConstant) {
            this.isRegexConstant = isRegexConstant;
            this.regexConstant = regexConstant;
            this.patternConstant = patternConstant;
        }

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
