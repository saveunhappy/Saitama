package com.saveunhappy.saitama.compiler.domain.expression;

import com.saveunhappy.saitama.compiler.bytecodegenerator.ExpressionGenerator;
import com.saveunhappy.saitama.compiler.bytecodegenerator.StatementGenerator;
import com.saveunhappy.saitama.compiler.domain.scope.FunctionSignature;
import com.saveunhappy.saitama.compiler.domain.statement.Statement;
import com.saveunhappy.saitama.compiler.domain.type.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class FunctionCall extends Expression implements Statement {
    private Type owner;
    private FunctionSignature signature;
    private Collection<Expression> parameters;

    public FunctionCall(FunctionSignature signature, Collection<Expression> parameters, Type owner) {
        super(signature.getReturnType());
        this.signature = signature;
        this.parameters = parameters;
        this.owner = owner;
    }

    public String getFunctionName() {
        return signature.getName();
    }

    public FunctionSignature getSignature() {
        return signature;
    }

    public Optional<Type> getOwner() {
        return Optional.ofNullable(owner);
    }

    public Collection<Expression> getParameters() {
        return Collections.unmodifiableCollection(parameters);
    }


    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
