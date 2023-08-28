package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.expression.EmptyExpression;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.statement.*;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;

import java.util.List;
import java.util.stream.Collectors;

public class StatementVisitor extends SaitamaBaseVisitor<Statement> {
    private Scope scope;
    private ExpressionVisitor expressionVisitor;

    public StatementVisitor(Scope scope) {
        this.scope = scope;
        expressionVisitor = new ExpressionVisitor(scope);
    }


    //    @Override
//    public Statement visitPrintStatement(SaitamaParser.PrintStatementContext ctx) {
//        SaitamaParser.ExpressionContext expressionContext = ctx.expression();
//        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
//        Expression expression = expressionContext.accept(expressionVisitor);
//        return new PrintStatement(expression);
//    }
    /**
     * 这里直接做了一个简化，既然都需要expressionVisitor对象，那就创建对象的时候创建一个全局的
     * */
    @Override
    public Statement visitPrintStatement(SaitamaParser.PrintStatementContext ctx) {
        SaitamaParser.ExpressionContext expressionContext = ctx.expression();
        Expression expression = expressionContext.accept(expressionVisitor);
        return new PrintStatement(expression);
    }


    /**
     * SaitamaParser.ExpressionContext leftExpressionContext = ctx.expression(0);
     * SaitamaParser.ExpressionContext rightExpressionContext = ctx.expression(1);
     * Expression leftExpression = leftExpressionContext.accept(this);
     * Expression rightExpression = rightExpressionContext.accept(this);
     */
    @Override
    public Statement visitVariableDeclaration(SaitamaParser.VariableDeclarationContext ctx) {
        String varName = ctx.name().getText();
        SaitamaParser.ExpressionContext expressionCtx = ctx.expression();
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
//        return (Statement) ctx.accept(expressionVisitor);
    }
    @Override
    public Statement visitRETURNVOID(SaitamaParser.RETURNVOIDContext ctx) {
        return new ReturnStatement(new EmptyExpression(BuiltInType.VOID));
    }

    @Override
    public Statement visitRETURNWITHVALUE(SaitamaParser.RETURNWITHVALUEContext ctx) {
        Expression expression = ctx.expression().accept(expressionVisitor);
        return new ReturnStatement(expression);
    }

    @Override
    public Statement visitBlock(SaitamaParser.BlockContext ctx) {
        List<SaitamaParser.StatementContext> blockStatementCtx = ctx.statement();
        //这个方法就是把scope给拿到，因为是外面传过来的，所以只能重新赋值了，这个时候就有了这个scope的变量
//        Scope newScope = new Scope(scope);
//        StatementVisitor statementVisitor = new StatementVisitor(newScope);
        List<Statement> statements = blockStatementCtx.stream()
                .map(stmt -> stmt.accept(this))
                .collect(Collectors.toList());
        return new Block(scope, statements);
    }
}
