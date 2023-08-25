package com.saveunhappy.saitama.compiler.domain.math;

import com.saveunhappy.saitama.compiler.bytecodegenerator.ExpressionGenerator;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;

public class Addition extends ArthimeticExpression {
    public Addition(Expression leftExpression, Expression rightExpression) {
        super(leftExpression.getType(), leftExpression, rightExpression);
    }

    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }
}
