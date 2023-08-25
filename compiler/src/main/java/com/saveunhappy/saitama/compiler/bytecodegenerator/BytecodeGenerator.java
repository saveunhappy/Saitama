package com.saveunhappy.saitama.compiler.bytecodegenerator;


import com.saveunhappy.saitama.compiler.domain.global.ClassDeclaration;
import com.saveunhappy.saitama.compiler.domain.global.CompilationUnit;

public class BytecodeGenerator {
    public byte[] generate(CompilationUnit compilationUnit) {
        ClassDeclaration classDeclaration = compilationUnit.getClassDeclaration();
        //这里是生成类的字节码，肯定要借助ClassWriter,那么就在他的内部有一个ClassWriter
        //并且有栈帧计算也在初始化中完成
        ClassGenerator classGenerator = new ClassGenerator();
        return classGenerator.generate(classDeclaration).toByteArray();
    }
}
