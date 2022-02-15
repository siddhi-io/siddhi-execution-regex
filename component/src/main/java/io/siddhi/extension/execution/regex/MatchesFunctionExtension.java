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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing the Regex Matches implementation.
 *
 * matches(regex, input.sequence)
 * This method attempts to match the entire 'inputSequence' against the 'regex' pattern.
 * regex - regular expression. eg: "\d\d(.*)WSO2"
 * input.sequence - input sequence to be matched with the regular expression eg: "21 products are produced by WSO2
 * currently Accept Type(s) for matches(regex, inputSequence);
 * regex : STRING
 * inputSequence : STRING
 * Return Type(s): BOOLEAN
 */
@Extension(
        name = "matches",
        namespace = "regex",
        description = "Matches the entire input.sequence against the regex pattern.",
        parameters = {
                @Parameter(name = "regex",
                        description = "A regular expression. For example, `\\d\\d(.*)WSO2`.",
                        type = {DataType.STRING},
                        dynamic = true),
                @Parameter(name = "input.sequence",
                        description = "The input sequence to be matched with the regular expression. "
                                + "For example, `21 products are produced by WSO2`.",
                        type = {DataType.STRING},
                        dynamic = true)
        },
        parameterOverloads = {
                @ParameterOverload(parameterNames = {"regex", "input.sequence"})
        },
        returnAttributes = @ReturnAttribute(
                description = "Returns `true` if the regex matches the entire input.sequence, else return `false`.",
                type = {DataType.BOOL}),
        examples = {
                @Example(
                        syntax = "regex:matches('WSO2(.*)middleware(.*)', "
                                + "'WSO2 is situated in trace and its a middleware company')",
                        description = "Function matches the entire input.sequence against " +
                                "`WSO2(.*)middleware(.*)` regex pattern, and as it matches it returns `true`."
                ),
                @Example(
                        syntax = "regex:matches('WSO2(.*)middleware', "
                                + "'WSO2 is situated in trace and its a middleware company')",
                        description = "Function matches the entire input.sequence against " +
                                "`WSO2(.*)middleware` regex pattern. As it does not match it returns `false`."
                )
        }
)
public class MatchesFunctionExtension extends FunctionExecutor<MatchesFunctionExtension.ExtensionState> {

    private Attribute.Type returnType = Attribute.Type.BOOL;
    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(MatchesFunctionExtension.class);

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

        if (!extensionState.isRegexConstant) {
            regex = (String) data[0];
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(source);
            return matcher.matches();

        } else {
            matcher = extensionState.patternConstant.matcher(source);
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
