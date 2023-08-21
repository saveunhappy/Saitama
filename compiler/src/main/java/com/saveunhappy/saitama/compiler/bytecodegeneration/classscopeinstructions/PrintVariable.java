package com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions;

import com.saveunhappy.saitama.antlr.SaitamaLexer;
import com.saveunhappy.saitama.compiler.parsing.domain.Variable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

//打印变量
public class PrintVariable implements ClassScopeInstruction, Opcodes {

    private Variable variable;

    public PrintVariable(Variable variable) {
        this.variable = variable;
    }


    @Override
    public void apply(MethodVisitor mv) {
        final int type = variable.getType();
        final int id = variable.getId();
        //打印变量，我们打印变量都是System.out.println,而println是out的一个方法，这里
        //就是获取到out对象，而且是静态的变量
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if (type == SaitamaLexer.NUMBER) {
            //加载变量,基本类型就是I
            mv.visitVarInsn(ILOAD, id);
            //进行打印，invokevirtual就是去调用普通的方法
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        } else if (type == SaitamaLexer.STRING) {
            //引用类型就是A,
            mv.visitVarInsn(ALOAD, id);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }
}
