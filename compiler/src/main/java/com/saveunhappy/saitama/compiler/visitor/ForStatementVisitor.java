package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.statement.RangedForStatement;
import com.saveunhappy.saitama.compiler.domain.statement.Statement;
import com.saveunhappy.saitama.compiler.domain.statement.VariableDeclarationStatement;

public class ForStatementVisitor extends SaitamaBaseVisitor<RangedForStatement> {
    private Scope scope;
    private ExpressionVisitor expressionVisitor;
    private StatementVisitor statementVisitor;

    public ForStatementVisitor(Scope scope) {
        this.scope = new Scope(scope);
        expressionVisitor = new ExpressionVisitor(this.scope);
        statementVisitor = new StatementVisitor(this.scope);
    }

    @Override
    public RangedForStatement visitForStatement(SaitamaParser.ForStatementContext ctx) {
        SaitamaParser.ForConditionsContext forConditionsContext = ctx.forConditions();
        Expression startExpression = forConditionsContext.startExpr.accept(expressionVisitor);
        Expression endExpression = forConditionsContext.endExpr.accept(expressionVisitor);
        SaitamaParser.VariableReferenceContext iterator = forConditionsContext.iterator;
        String varName = iterator.getText();
        if (scope.localVariableExists(varName)) {
            Statement iteratorVariable = new VariableDeclarationStatement(varName, startExpression);
            Statement statement = ctx.statement().accept(statementVisitor);
            return new RangedForStatement(iteratorVariable, startExpression, endExpression, statement, varName, scope);
        } else {
            scope.addLocalVariable(new LocalVariable(varName, startExpression.getType()));
            Statement iteratorVariable = new VariableDeclarationStatement(varName, startExpression);
            Statement statement = ctx.statement().accept(statementVisitor);
            return new RangedForStatement(iteratorVariable, startExpression, endExpression, statement, varName, scope);
        }
    }
}
