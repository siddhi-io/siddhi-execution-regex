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
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing the Regex Find implementation.
 *
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
@Extension(
        name = "find",
        namespace = "regex",
        description = "Finds the subsequence that matches the given regex pattern.",
        parameters = {
                @Parameter(name = "regex",
                        description = "A regular expression that is matched to a sequence in order " +
                                "to find the subsequence of the same. For example, `\\d\\d(.*)WSO2`.",
                        type = {DataType.STRING},
                        dynamic = true),
                @Parameter(name = "input.sequence",
                        description = "The input sequence to be matched with the regular expression. "
                                + "For example, `21 products are produced by WSO2`.",
                        type = {DataType.STRING},
                        dynamic = true),
                @Parameter(name = "starting.index",
                        description = "The starting index of the input sequence from where the input sequence is" +
                                "matched with the given regex pattern."
                                + "For example, `10`.",
                        type = {DataType.INT},
                        optional = true,
                        dynamic = true,
                        defaultValue = "0")
        },
        parameterOverloads = {
                @ParameterOverload(parameterNames = {"regex", "input.sequence"}),
                @ParameterOverload(parameterNames = {"regex", "input.sequence", "starting.index"})
        },
        returnAttributes = @ReturnAttribute(
                description = "Returns `true` if a matching subsequence is available in the input.sequence, " +
                        "else return `false`.",
                type = {DataType.BOOL}),
        examples = {
                @Example(
                        syntax = "regex:find('\\d\\d(.*)WSO2', " +
                                "'21 products are produced by WSO2 currently')",
                        description = "This method attempts to find the subsequence of the input.sequence " +
                                "that matches the regex pattern, `\\d\\d(.*)WSO2`. It returns `true` " +
                                "as a subsequence exists."
                ),
                @Example(
                        syntax = "regex:find('\\d\\d(.*)WSO2', '21 products are produced by WSO2.', 4)",
                        description = "This method attempts to find the subsequence of the input.sequence "
                                + "that matches the regex pattern, `\\d\\d(.*)WSO2` starting from index `4`. "
                                + "It returns 'false' as subsequence does not exists."
                )
        }
)
public class FindFunctionExtension extends FunctionExecutor<FindFunctionExtension.ExtensionState> {
    private static final Logger log = Logger.getLogger(FindFunctionExtension.class);
    private Attribute.Type returnType = Attribute.Type.BOOL;

    @Override
    protected StateFactory<ExtensionState> init(ExpressionExecutor[] attributeExpressionExecutors,
                                                ConfigReader configReader,
                                                SiddhiQueryContext siddhiQueryContext) {
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
            String regexConstant = (String) ((ConstantExpressionExecutor) attributeExpressionExecutors[0]).getValue();
            Pattern patternConstant = Pattern.compile(regexConstant);
            return () -> new ExtensionState(true, regexConstant, patternConstant);
        }
        return () -> new ExtensionState(false, null, null);
    }

    @Override
    protected Object execute(Object[] data, ExtensionState extensionState) {
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

        if (!extensionState.isRegexConstant) {
            regex = (String) data[0];
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(source);

        } else {
            matcher = extensionState.patternConstant.matcher(source);

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
    protected Object execute(Object o, ExtensionState extensionState) {
        return null;
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
