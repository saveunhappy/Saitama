package com.saveunhappy.saitama.compiler.domain.expression;

import com.saveunhappy.saitama.compiler.CompareSign;
import com.saveunhappy.saitama.compiler.bytecodegenerator.ExpressionGenerator;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;

public class ConditionalExpression extends Expression {

    private CompareSign compareSign;
    private Expression leftExpression;
    private Expression rightExpression;

    public ConditionalExpression(Expression leftExpression, Expression rightExpression, CompareSign compareSign) {
        super(BuiltInType.BOOLEAN);
        this.compareSign = compareSign;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }


    public CompareSign getCompareSign() {
        return compareSign;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }
}
