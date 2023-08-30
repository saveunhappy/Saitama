package com.saveunhappy.saitama.compiler.domain.expression;

import com.saveunhappy.saitama.compiler.bytecodegenerator.ExpressionGenerator;
import com.saveunhappy.saitama.compiler.domain.type.Type;

import java.util.Optional;

public class FunctionParameter extends Expression {
    private final String name;
    private Optional<Expression> defaultValue;

    public FunctionParameter(String name, Type type, Optional<Expression> defaultValue) {
        super(type);
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Optional<Expression> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }
}

