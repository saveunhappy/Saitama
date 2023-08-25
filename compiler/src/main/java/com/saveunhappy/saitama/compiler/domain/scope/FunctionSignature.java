package com.saveunhappy.saitama.compiler.domain.scope;

import com.saveunhappy.saitama.compiler.domain.expression.FunctionParameter;
import com.saveunhappy.saitama.compiler.domain.type.Type;

import java.util.List;

public class FunctionSignature {
    private String name;
    private List<FunctionParameter> arguments;
    private Type returnType;

    public FunctionSignature(String name, List<FunctionParameter> arguments, Type returnType) {
        this.name = name;
        this.arguments = arguments;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getArguments() {
        return arguments;
    }

    public Type getReturnType() {
        return returnType;
    }
}
