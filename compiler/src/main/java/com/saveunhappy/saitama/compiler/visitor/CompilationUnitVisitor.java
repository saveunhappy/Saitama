package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.bytecodegeneration.ClassDeclaration;
import com.saveunhappy.saitama.compiler.bytecodegeneration.CompilationUnit;

public class CompilationUnitVisitor extends SaitamaBaseVisitor<CompilationUnit> {

    @Override
    public CompilationUnit visitCompilationUnit(SaitamaParser.CompilationUnitContext ctx) {
        ClassVisitor classVisitor = new ClassVisitor();
        SaitamaParser.ClassDeclarationContext classDeclarationContext = ctx.classDeclaration();
        //返回新创建的对象，里面有指令和类的名字
        ClassDeclaration classDeclaration = classDeclarationContext.accept(classVisitor);
        return new CompilationUnit(classDeclaration);
    }
}
