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
    private List<Statement> statements;
    private Type returnType;
    private Scope scope;

    public Function(Scope scope, String name, Type returnType, List<FunctionParameter> arguments, List<Statement> statements) {
        this.scope = scope;
        this.name = name;
        this.arguments = arguments;
        this.statements = statements;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public Collection<Statement> getStatements() {
        return Collections.unmodifiableCollection(statements);
    }

    public Scope getScope() {
        return scope;
    }

    public Type getReturnType() {
        return returnType;
    }
}
