package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.clazz.Function;
import com.saveunhappy.saitama.compiler.domain.scope.FunctionSignature;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.statement.Statement;
import org.antlr.v4.runtime.misc.NotNull;


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
        FunctionSignature signature = scope.getSignature(ctx.functionDeclaration().functionName().getText());
        //获取所有的参数的名字和类型，并且做了一件事，添加到那个方法的作用域中，作为变量使用
        //确实也就应该这么做，这个scope中就是有localVariable的集合，
        addParametersAsLocalVariables(signature);
        //获取方法的返回值类型
        Statement block = getBlock(ctx);
        return new Function(signature, block);
    }

    private void addParametersAsLocalVariables(FunctionSignature signature) {
        signature.getParameters()
                .forEach(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType())));
    }
    private Statement getBlock(SaitamaParser.FunctionContext functionContext) {
        //这个时候的scope中已经有了变量，然后给了这个visitor.
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        SaitamaParser.BlockContext blockContext = functionContext.block();
        return blockContext.accept(statementVisitor);
    }
}
