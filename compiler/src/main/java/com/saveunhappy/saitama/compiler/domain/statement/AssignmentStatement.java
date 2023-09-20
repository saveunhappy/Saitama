package com.saveunhappy.saitama.compiler.domain.statement;

import com.saveunhappy.saitama.compiler.bytecodegenerator.StatementGenerator;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;

public class AssignmentStatement implements Statement {
    private String varName;
    private Expression expression;

    public AssignmentStatement(String varName, Expression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    public AssignmentStatement(VariableDeclarationStatement declarationStatement) {
        this.varName = declarationStatement.getName();
        this.expression = declarationStatement.getExpression();
    }


    public String getVarName() {
        return varName;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
