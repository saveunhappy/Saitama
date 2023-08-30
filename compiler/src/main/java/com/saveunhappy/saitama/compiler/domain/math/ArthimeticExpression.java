package com.saveunhappy.saitama.compiler.domain.math;

import com.saveunhappy.saitama.compiler.domain.expression.Expression;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.exception.UnsupportedArthimeticOperationException;

public abstract class ArthimeticExpression extends Expression {
    private Expression leftExpression;
    private Expression rightExpression;

    public ArthimeticExpression(Type type, Expression leftExpression, Expression rightExpression) {
        super(type);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        if (type != BuiltInType.INT && type != BuiltInType.STRING) {
            throw new UnsupportedArthimeticOperationException(this);
        }
    }


    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }
}
