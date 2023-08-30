package com.saveunhappy.saitama.compiler.domain.scope;

import com.saveunhappy.saitama.compiler.domain.expression.FunctionParameter;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.exception.ParameterForNameNotFoundException;

import java.util.List;

public class FunctionSignature {
    private String name;
    private List<FunctionParameter> parameters;
    private Type returnType;

    public FunctionSignature(String name, List<FunctionParameter> parameters, Type returnType) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    public FunctionParameter getParameterForName(String name) {
        return parameters.stream()
                .filter(param -> param.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ParameterForNameNotFoundException(name, parameters));
    }

    public int getIndexOfParameters(String parameterName) {
        FunctionParameter parameter = getParameterForName(parameterName);
        return parameters.indexOf(parameter);
    }
}
