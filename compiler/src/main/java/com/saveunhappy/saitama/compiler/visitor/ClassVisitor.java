package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.bytecodegeneration.ClassDeclaration;
//去访问这个类的源代码
public class ClassVisitor extends SaitamaBaseVisitor<ClassDeclaration> {
    @Override
    public ClassDeclaration visitClassDeclaration(SaitamaParser.ClassDeclarationContext ctx) {
       //获取到类的名字
        String name = ctx.className().getText();
        //这个就是去访问类声明里面的东西了，就是方法体现在是只有变量声明和打印变量
        StatementVisitor statementVisitor = new StatementVisitor();
        //方法体去接收这个东西
        ctx.classBody().accept(statementVisitor);
        //返回新创建的对象，里面有指令和类的名字
        return new ClassDeclaration(statementVisitor.getClassScopeInstruction(), name);
    }
}
