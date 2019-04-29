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
        description = "This method attempts to match the 'inputSequence', from the beginning, against the "
                + "'regex' pattern.",
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
                description = "The value returned is of 'boolean' type.",
                type = {DataType.BOOL}),
        examples = {
                @Example(
                        syntax = "define stream InputStream (inputSequence string, price long, regex string,"
                                + " group int);\n"
                                + "\n"
                                + "from InputStream select inputSequence, regex:lookingAt(\\d\\d(.*)(WSO2.*), "
                                + "21 products are produced by WSO2 currently in Sri Lanka)",
                        description = "This method attempts to match the 'inputSequence' against the regex pattern," +
                                " \\d\\d(.*)(WSO2.*) from the beginning. Since it matches, the function returns 'true'."
                ),
                @Example(
                        syntax = "define stream InputStream (inputSequence string, price long, regex string,"
                                + " group int);\n"
                                + "\n"
                                + "from InputStream select inputSequence, regex:lookingAt(WSO2(.*)middleware(.*), "
                                + "sample test string and WSO2 is situated in trace and it's a middleware company)",
                        description = "This method attempts to match the 'inputSequence' against the regex pattern, " +
                                "WSO2(.*)middleware(.*) from the beginning. Since it does not match, the function " +
                                "returns false."
                )
        }
)
public class LookingAtFunctionExtension extends FunctionExecutor<LookingAtFunctionExtension.ExtensionState> {
    private Attribute.Type returnType = Attribute.Type.BOOL;
    private static final Logger log = Logger.getLogger(LookingAtFunctionExtension.class);

    @Override
    protected StateFactory<ExtensionState> init(ExpressionExecutor[] attributeExpressionExecutors,
                                                ConfigReader configReader,
                                                SiddhiQueryContext siddhiQueryContext) {
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

        if (!extensionState.isRegexConstant) {
            regex = (String) data[0];
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(source);
            return matcher.lookingAt();

        } else {
            matcher = extensionState.patternConstant.matcher(source);
            return matcher.lookingAt();
        }
    }

    @Override
    protected Object execute(Object o, ExtensionState extensionState) {
        return null;  //Since the lookingAt function takes in 2 parameters, this method does not
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
