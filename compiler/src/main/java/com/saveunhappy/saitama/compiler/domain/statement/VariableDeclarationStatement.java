package com.saveunhappy.saitama.compiler.domain.statement;

import com.saveunhappy.saitama.compiler.bytecodegenerator.StatementGenerator;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;

public class VariableDeclarationStatement implements Statement {
    private String name;
    private Expression expression;
    private boolean isDeclared;


    public VariableDeclarationStatement(String name, Expression expression, boolean isDeclared) {
        this.name = name;
        this.expression = expression;
        this.isDeclared = isDeclared;
    }

    public VariableDeclarationStatement(String name, Expression expression) {
//        this.name = name;
//        this.expression = expression;
        this(name,expression,false);
    }

    public String getName() {
        return name;
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
