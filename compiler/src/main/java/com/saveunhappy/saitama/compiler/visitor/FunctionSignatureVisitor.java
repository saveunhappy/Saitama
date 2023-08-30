package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;
import com.saveunhappy.saitama.compiler.domain.expression.FunctionParameter;
import com.saveunhappy.saitama.compiler.domain.scope.FunctionSignature;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.utils.TypeResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FunctionSignatureVisitor extends SaitamaBaseVisitor<FunctionSignature> {

    private Scope scope;
    private ExpressionVisitor expressionVisitor;

    public FunctionSignatureVisitor(Scope scope) {
        this.scope = scope;
        this.expressionVisitor = new ExpressionVisitor(scope);
    }

    @Override
    public FunctionSignature visitFunctionDeclaration(SaitamaParser.FunctionDeclarationContext ctx) {
        String functionName = ctx.functionName().getText();
        List<SaitamaParser.FunctionParameterContext> argsCtx = ctx.functionParameter();
        List<FunctionParameter> parameters = new ArrayList<>();
        for (SaitamaParser.FunctionParameterContext argCtx : argsCtx) {
            String name = argCtx.ID().getText();
            //static void main(String[] args)  这里获取的类型就是STRING_ARR
            Type type = TypeResolver.getFromTypeName(argCtx.type());
            /** Parameter是形参，Arguments是实参，这里就是获取到形参里面的默认值*/
            Optional<Expression> defaultValue = getParameterDefaultValue(argCtx);
            //方法参数，名字和类型
            FunctionParameter functionParameter = new FunctionParameter(name, type, defaultValue);
            parameters.add(functionParameter);
        }
        //这个返回方法的返回值类型,这样就有了方法的签名。
        Type returnType = TypeResolver.getFromTypeName(ctx.type());
        return new FunctionSignature(functionName, parameters, returnType);
    }

    private Optional<Expression> getParameterDefaultValue(SaitamaParser.FunctionParameterContext parameterContext) {
        if (parameterContext.functionParamdefaultValue() != null) {
            SaitamaParser.ExpressionContext defaultValueCtx = parameterContext.functionParamdefaultValue().expression();
            return Optional.of(defaultValueCtx.accept(expressionVisitor));
        }
        return Optional.empty();
    }
}
