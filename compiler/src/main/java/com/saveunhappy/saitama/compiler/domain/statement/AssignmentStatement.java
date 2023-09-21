package com.saveunhappy.saitama.compiler.domain.statement;

import com.saveunhappy.saitama.compiler.bytecodegenerator.StatementGenerator;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;

public class AssignmentStatement implements Statement {
    private String varName;
    private Expression expression;

    private boolean isDeclared;

    public AssignmentStatement(String varName, Expression expression,boolean isDeclared) {
        this.varName = varName;
        this.expression = expression;
        this.isDeclared = isDeclared;
    }

    public AssignmentStatement(VariableDeclarationStatement declarationStatement) {
        this.varName = declarationStatement.getName();
        this.expression = declarationStatement.getExpression();
        this.isDeclared = declarationStatement.isDeclared();
    }


    public String getVarName() {
        return varName;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean isDeclared() {
        return isDeclared;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
