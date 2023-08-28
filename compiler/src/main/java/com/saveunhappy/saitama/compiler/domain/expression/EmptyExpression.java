package com.saveunhappy.saitama.compiler.domain.expression;

import com.saveunhappy.saitama.compiler.bytecodegenerator.ExpressionGenerator;
import com.saveunhappy.saitama.compiler.domain.type.Type;

public class EmptyExpression extends Expression {
    public EmptyExpression(Type type) {
        super(type);
    }

    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }

}
