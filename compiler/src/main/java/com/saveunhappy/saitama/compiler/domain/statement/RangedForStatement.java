package com.saveunhappy.saitama.compiler.domain.statement;

import com.saveunhappy.saitama.compiler.bytecodegenerator.StatementGenerator;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;
import com.saveunhappy.saitama.compiler.domain.expression.UnsupportedRangedLoopTypes;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;
import com.saveunhappy.saitama.compiler.domain.type.Type;

public class RangedForStatement implements Statement {
    private Statement iteratorVariable;
    private Expression startExpression;
    private Expression endExpression;
    private Statement statement;
    private String iteratorVarName;
    private Scope scope;

    public RangedForStatement(Statement iteratorVariable, Expression startExpression, Expression endExpression, Statement statement, String iteratorVarName, Scope scope) {
        this.scope = scope;
        if (startExpression.getType() != BuiltInType.INT || endExpression.getType() != BuiltInType.INT) {
            throw new UnsupportedRangedLoopTypes(startExpression, endExpression);
        }
        this.iteratorVariable = iteratorVariable;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.statement = statement;
        this.iteratorVarName = iteratorVarName;
    }

    public Statement getIteratorVariableStatement() {
        return iteratorVariable;
    }

    public Expression getStartExpression() {
        return startExpression;
    }

    public Expression getEndExpression() {
        return endExpression;
    }

    public Statement getStatement() {
        return statement;
    }

    public String getIteratorVarName() {
        return iteratorVarName;
    }

    public Scope getScope() {
        return scope;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Type getType() {
        return startExpression.getType();
    }
}
