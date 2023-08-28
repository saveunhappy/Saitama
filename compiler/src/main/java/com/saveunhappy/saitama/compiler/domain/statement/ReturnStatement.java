package com.saveunhappy.saitama.compiler.domain.statement;

import com.saveunhappy.saitama.compiler.bytecodegenerator.StatementGenerator;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;

public class ReturnStatement implements Statement {
    private Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Expression getExpression() {
        return expression;
    }
}
