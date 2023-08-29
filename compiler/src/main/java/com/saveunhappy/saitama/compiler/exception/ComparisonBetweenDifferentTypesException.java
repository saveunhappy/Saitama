package com.saveunhappy.saitama.compiler.exception;

import com.saveunhappy.saitama.compiler.domain.expression.Expression;

public class ComparisonBetweenDifferentTypesException extends RuntimeException {
    public ComparisonBetweenDifferentTypesException(Expression leftExpression, Expression rightExpression) {
        super("Comparison between types " + leftExpression.getType() + " and " + rightExpression.getType() + " not yet supported");
    }
}
