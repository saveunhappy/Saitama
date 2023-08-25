package com.saveunhappy.saitama.compiler.domain.expression;

import com.saveunhappy.saitama.compiler.bytecodegenerator.ExpressionGenerator;
import com.saveunhappy.saitama.compiler.domain.type.Type;

public class Value extends Expression {
    private String value;

    public Value(Type type, String value) {
        super(type);
        this.value = value;
    }

    public String getValue() {
        return value;
    }


    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }
}
