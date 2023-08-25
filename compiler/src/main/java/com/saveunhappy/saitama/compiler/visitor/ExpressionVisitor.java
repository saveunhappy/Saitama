package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.expression.*;
import com.saveunhappy.saitama.compiler.domain.math.Addition;
import com.saveunhappy.saitama.compiler.domain.math.Division;
import com.saveunhappy.saitama.compiler.domain.math.Multiplication;
import com.saveunhappy.saitama.compiler.domain.math.Substraction;
import com.saveunhappy.saitama.compiler.domain.scope.FunctionSignature;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.utils.TypeResolver;

import java.util.List;
import java.util.stream.Collectors;

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
        FunctionSignature signature = scope.getSignature(funName);//在最开始的时候就有了所有的方法签名，如果这个时候获取不到，那么就说明有错
        List<FunctionParameter> signatureParameters = signature.getArguments();
        List<SaitamaParser.ExpressionContext> calledParameters = ctx.expressionList().expression();
        /**
         * 因为方法调用的话，你是需要传参数的啊，那么就获取到你的变量名，如果没有获取到
         * 就报错，很简单，你要传的，你没找到，那就是没有定义，所以报错
         * */
        List<Expression> arguments = calledParameters.stream()
                .map((expressionContext -> expressionContext.accept(this)))
                .collect(Collectors.toList());
        Type returnType = signature.getReturnType();
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
}

