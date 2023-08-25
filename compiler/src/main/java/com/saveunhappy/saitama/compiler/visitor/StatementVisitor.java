package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.statement.PrintStatement;
import com.saveunhappy.saitama.compiler.domain.statement.Statement;
import com.saveunhappy.saitama.compiler.domain.statement.VariableDeclarationStatement;
public class StatementVisitor extends SaitamaBaseVisitor<Statement> {
    private Scope scope;

    public StatementVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Statement visitPrintStatement(SaitamaParser.PrintStatementContext ctx) {
        SaitamaParser.ExpressionContext expressionContext = ctx.expression();
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        Expression expression = expressionContext.accept(expressionVisitor);
        return new PrintStatement(expression);
    }

    /**
     * SaitamaParser.ExpressionContext leftExpressionContext = ctx.expression(0);
     * SaitamaParser.ExpressionContext rightExpressionContext = ctx.expression(1);
     * Expression leftExpression = leftExpressionContext.accept(this);
     * Expression rightExpression = rightExpressionContext.accept(this);
     * */
    @Override
    public Statement visitVariableDeclaration(SaitamaParser.VariableDeclarationContext ctx) {
        String varName = ctx.name().getText();
        SaitamaParser.ExpressionContext expressionCtx = ctx.expression();
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        //var result = 5 * 3 / 2 + 1 - 2这个怎么解析呢？就按照从做往右，谁的优先级高那么就在
        //首先解析到5，然后解析乘号，然后下面是个除法，
        //看5 - 3 / 2 + 1 - 2的话，那么就是中序遍历。
        //表达式里面可以嵌套表达式，所以是递归的进行的，那么在ExpressionVisitor中
        //所以accept就是this，递归的去调用,看上面的代码
        Expression expression = expressionCtx.accept(expressionVisitor);
        //表达式最终还是要变成一个值的，所以也变成一个变量添加到作用域中去
        scope.addLocalVariable(new LocalVariable(varName, expression.getType()));
        return new VariableDeclarationStatement(varName, expression);
    }

    @Override
    public Statement visitFunctionCall(SaitamaParser.FunctionCallContext ctx) {
        return (Statement) ctx.accept(new ExpressionVisitor(scope));
    }
}
