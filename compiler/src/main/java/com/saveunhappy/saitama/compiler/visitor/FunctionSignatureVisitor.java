package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.expression.FunctionParameter;
import com.saveunhappy.saitama.compiler.domain.scope.FunctionSignature;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.utils.TypeResolver;

import java.util.ArrayList;
import java.util.List;

public class FunctionSignatureVisitor extends SaitamaBaseVisitor<FunctionSignature> {
    @Override
    public FunctionSignature visitFunctionDeclaration(SaitamaParser.FunctionDeclarationContext ctx) {
        String functionName = ctx.functionName().getText();
        List<SaitamaParser.FunctionArgumentContext> argsCtx = ctx.functionArgument();
        List<FunctionParameter> parameters = new ArrayList<>();
        for (int i = 0; i < argsCtx.size(); i++) {
            SaitamaParser.FunctionArgumentContext argCtx = argsCtx.get(i);
            String name = argCtx.ID().getText();
            //static void main(String[] args)  这里获取的类型就是STRING_ARR
            Type type = TypeResolver.getFromTypeName(argCtx.type());
            //方法参数，名字和类型
            FunctionParameter functionParameter = new FunctionParameter(name, type);
            parameters.add(functionParameter);
        }
        //这个返回方法的返回值类型,这样就有了方法的签名。
        Type returnType = TypeResolver.getFromTypeName(ctx.type());
        return new FunctionSignature(functionName, parameters, returnType);
    }
}
