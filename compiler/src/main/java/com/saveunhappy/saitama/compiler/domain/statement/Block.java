package com.saveunhappy.saitama.compiler.domain.statement;

import com.saveunhappy.saitama.compiler.bytecodegenerator.StatementGenerator;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;

import java.util.List;

public class Block implements Statement {
    private List<Statement> statements;
    private Scope scope;

    public Block( Scope scope, List<Statement> statements) {
        this.statements = statements;
        this.scope = scope;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Scope getScope() {
        return scope;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
