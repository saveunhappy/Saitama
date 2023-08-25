package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.clazz.Function;
import com.saveunhappy.saitama.compiler.domain.global.ClassDeclaration;
import com.saveunhappy.saitama.compiler.domain.global.MetaData;
import com.saveunhappy.saitama.compiler.domain.scope.FunctionSignature;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;

import java.util.List;
import java.util.stream.Collectors;

//去访问这个类的源代码
public class ClassVisitor extends SaitamaBaseVisitor<ClassDeclaration> {
    private Scope scope;

    @Override
    public ClassDeclaration visitClassDeclaration(SaitamaParser.ClassDeclarationContext ctx) {
        String name = ctx.className().getText();
        FunctionSignatureVisitor functionSignatureVisitor = new FunctionSignatureVisitor();
        List<SaitamaParser.FunctionContext> methodCtx = ctx.classBody().function();
        //目前是只有自己的类名,之后加上构造器相关解析，可能就会加上父类相关的
        MetaData metaData = new MetaData(ctx.className().getText());
        scope = new Scope(metaData);
        //这里就是去获取所有的方法声明，并且添加到scope中去，返回值是没有用的，主要是
        //往scope中去添加方法签名
        List<FunctionSignature> signatures = methodCtx.stream()
                .map(method -> method.functionDeclaration().accept(functionSignatureVisitor))
                .peek(scope::addSignature)
                .collect(Collectors.toList());

        List<Function> methods = methodCtx.stream()
                .map(method -> method.accept(new FunctionVisitor(scope)))
                .collect(Collectors.toList());
        return new ClassDeclaration(name, methods);
    }
}
