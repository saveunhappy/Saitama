package com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions;

import org.objectweb.asm.MethodVisitor;

//执行去写入字节码
public interface ClassScopeInstruction {
    void apply(MethodVisitor mv);
}
