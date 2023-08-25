package com.saveunhappy.saitama.compiler.domain.expression;

import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.bytecodegenerator.ExpressionGenerator;

public abstract class Expression {
    private Type type;

    public Expression(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public abstract void accept(ExpressionGenerator generator);
}
