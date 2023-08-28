package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.clazz.Function;
import com.saveunhappy.saitama.compiler.domain.expression.FunctionParameter;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.statement.Statement;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.utils.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionVisitor extends SaitamaBaseVisitor<Function> {
    private Scope scope;
    /**
     * 怎么做到有不同的scope的？因为传进来的都是new的
     **/
    public FunctionVisitor(Scope scope) {
        this.scope = new Scope(scope);
    }

    @Override
    public Function visitFunction(@NotNull SaitamaParser.FunctionContext ctx) {
        //和获取方法签名一样，获取方法的名字。
        String name = getName(ctx);
        //获取方法的返回值类型
        Type returnType = getReturnType(ctx);
        //获取所有的参数的名字和类型，并且做了一件事，添加到那个方法的作用域中，作为变量使用
        //确实也就应该这么做，这个scope中就是有localVariable的集合，
        List<FunctionParameter> arguments = getArguments(ctx);
        Statement block = getBlock(ctx);

        return new Function( name, returnType, arguments, block);
    }
    private Statement getBlock(SaitamaParser.FunctionContext functionContext) {
        //这个时候的scope中已经有了变量，然后给了这个visitor.
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        SaitamaParser.BlockContext blockContext = functionContext.block();
        return blockContext.accept(statementVisitor);
    }
    private String getName(SaitamaParser.FunctionContext functionDeclarationContext) {
        return functionDeclarationContext.functionDeclaration().functionName().getText();
    }

    private Type getReturnType(SaitamaParser.FunctionContext functionDeclarationContext) {
        SaitamaParser.TypeContext typeCtx = functionDeclarationContext.functionDeclaration().type();
        return TypeResolver.getFromTypeName(typeCtx);
    }

    private List<FunctionParameter> getArguments(SaitamaParser.FunctionContext functionDeclarationContext) {
        List<SaitamaParser.FunctionArgumentContext> argsCtx = functionDeclarationContext.functionDeclaration().functionArgument();
        List<FunctionParameter> parameters = argsCtx.stream()
                .map(paramCtx -> new FunctionParameter(paramCtx.ID().getText(), TypeResolver.getFromTypeName(paramCtx.type())))
                .peek(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType())))
                .collect(Collectors.toList());
        return parameters;
    }

}
