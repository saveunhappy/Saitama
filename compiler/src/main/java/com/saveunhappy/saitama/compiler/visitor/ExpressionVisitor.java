package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.CompareSign;
import com.saveunhappy.saitama.compiler.domain.expression.*;
import com.saveunhappy.saitama.compiler.domain.math.Addition;
import com.saveunhappy.saitama.compiler.domain.math.Division;
import com.saveunhappy.saitama.compiler.domain.math.Multiplication;
import com.saveunhappy.saitama.compiler.domain.math.Substraction;
import com.saveunhappy.saitama.compiler.domain.scope.FunctionSignature;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.utils.TypeResolver;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

//在解析表达式的时候，传的是ExpressionVisitor
public class ExpressionVisitor extends SaitamaBaseVisitor<Expression> {
    private Scope scope;

    public ExpressionVisitor(Scope scope) {
        this.scope = scope;
    }


    @Override
    public Expression visitVarReference(SaitamaParser.VarReferenceContext ctx) {
        String varName = ctx.getText();
        LocalVariable localVariable = scope.getLocalVariable(varName);
        return new VarReference(varName, localVariable.getType());
    }


    @Override
    public Expression visitValue(SaitamaParser.ValueContext ctx) {
        String value = ctx.getText();
        Type type = TypeResolver.getFromValue(value);
        return new Value(type, value);
    }

    @Override
    public Expression visitFunctionCall(SaitamaParser.FunctionCallContext ctx) {
        String funName = ctx.functionName().getText();
        FunctionSignature signature = scope.getSignature(funName);
        List<SaitamaParser.ArgumentContext> argumentCtx = ctx.argument();
        //增加了一个比较器，a1-a2，那么就是升序的，就会排好序。
        Comparator<SaitamaParser.ArgumentContext> argumentComparator = (arg1, arg2) -> {
            if (arg1.name() == null) return 0;
            String arg1Name = arg1.name().getText();
            String arg2Name = arg2.name().getText();
            return signature.getIndexOfParameters(arg1Name) - signature.getIndexOfParameters(arg2Name);
        };
        /**
         * 因为方法调用的话，你是需要传参数的啊，那么就获取到你的变量名，如果没有获取到
         * 就报错，很简单，你要传的，你没找到，那就是没有定义，所以报错
         * */
        List<Expression> arguments = argumentCtx.stream()
                .sorted(argumentComparator)
                .map(argument -> argument.expression().accept(this))
                .collect(toList());
        return new FunctionCall(signature, arguments, null);
    }


    @Override
    public Expression visitADD(SaitamaParser.ADDContext ctx) {
        SaitamaParser.ExpressionContext leftExpressionContext = ctx.expression(0);
        SaitamaParser.ExpressionContext rightExpressionContext = ctx.expression(1);
        Expression leftExpression = leftExpressionContext.accept(this);
        Expression rightExpression = rightExpressionContext.accept(this);

        return new Addition(leftExpression, rightExpression);
    }


    @Override
    public Expression visitMULTIPLY(SaitamaParser.MULTIPLYContext ctx) {
        SaitamaParser.ExpressionContext leftExpressionContext = ctx.expression(0);
        SaitamaParser.ExpressionContext rightExpressionContext = ctx.expression(1);
        Expression leftExpression = leftExpressionContext.accept(this);
        Expression rightExpression = rightExpressionContext.accept(this);

        return new Multiplication(leftExpression, rightExpression);
    }


    @Override
    public Expression visitSUBSTRACT(SaitamaParser.SUBSTRACTContext ctx) {
        SaitamaParser.ExpressionContext leftExpressionContext = ctx.expression(0);
        SaitamaParser.ExpressionContext rightExpressionContext = ctx.expression(1);
        Expression leftExpression = leftExpressionContext.accept(this);
        Expression rightExpression = rightExpressionContext.accept(this);

        return new Substraction(leftExpression, rightExpression);
    }

    @Override
    public Expression visitDIVIDE(SaitamaParser.DIVIDEContext ctx) {
        SaitamaParser.ExpressionContext leftExpressionContext = ctx.expression(0);
        SaitamaParser.ExpressionContext rightExpressionContext = ctx.expression(1);
        Expression leftExpression = leftExpressionContext.accept(this);
        Expression rightExpression = rightExpressionContext.accept(this);

        return new Division(leftExpression, rightExpression);
    }

    @Override
    public Expression visitConditionalExpression(SaitamaParser.ConditionalExpressionContext ctx) {
        SaitamaParser.ExpressionContext leftExpressionContext = ctx.expression(0);
        SaitamaParser.ExpressionContext rightExpressionContext = ctx.expression(1);

        Expression leftExpression = leftExpressionContext.accept(this);
        Expression rightExpression = rightExpressionContext != null ? rightExpressionContext.accept(this) : new Value(BuiltInType.INT, "0");

        CompareSign cmpSign = ctx.cmp != null ? CompareSign.fromString(ctx.cmp.getText()) : CompareSign.NOT_EQUAL;
        return new ConditionalExpression(leftExpression, rightExpression, cmpSign);
    }
}

