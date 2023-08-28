package com.saveunhappy.saitama.compiler.domain.clazz;

import com.saveunhappy.saitama.compiler.domain.expression.FunctionParameter;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.statement.Statement;
import com.saveunhappy.saitama.compiler.domain.type.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Function {
    private String name;
    private List<FunctionParameter> arguments;
    private Statement rootStatement;
    private Type returnType;

    public Function(String name, Type returnType, List<FunctionParameter> arguments, Statement rootStatement) {
        this.name = name;
        this.arguments = arguments;
        this.rootStatement = rootStatement;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public Statement getRootStatement() {
        return rootStatement;
    }

    public Type getReturnType() {
        return returnType;
    }
}
