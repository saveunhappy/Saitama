package com.saveunhappy.saitama.compiler.bytecodegeneration;

import com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions.ClassScopeInstruction;
import com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions.VariableDeclaration;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;


//原来没有这个类的时候，这个只是根据传进来的文件名作为类名，生成一个Main函数进行打印和输出，
//现在只是暂时迁移到这边来，还是只生成一个main函数。

public class CompilationUnit {
    private ClassDeclaration classDeclaration;
    public CompilationUnit(ClassDeclaration classDeclaration) {
        this.classDeclaration = classDeclaration;
    }

    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    //获取类的名字
    public String getClassName() {
        return classDeclaration.getName();
    }
    public byte[] getByteCode() {
        //传入 0 代表不启用栈帧最大值和栈帧信息的计算，即不对字节码进行栈帧分析。
        //因为我们只关注生成简单的字节码，而不需要进行更深入的分析。
        ClassWriter cw = new ClassWriter(0);
        //52是字节码的版本号，代表Java8的版本，
        //ACC_PUBLIC + ACC_SUPER这是类的访问标志，表示类是 public 且使用了一个特殊标志。
        //其中，ACC_PUBLIC 表示类是 public，ACC_SUPER 表示使用新的 invokespecial 指令语义。
        //invockspecial 用于调用私有实例方法、构造器，以及使用 super 关键字调用父类的实例方法或构造器，和所实现接口的默认方法。
        //signature这是类的泛型签名，为null，代表没有泛型
        cw.visit(52,ACC_PUBLIC + ACC_SUPER,getClassName(),null, "java/lang/Object", null);
        //main方法，public static的，([Ljava/lang/String;)V中Ljava/lang/String表示是String类型
        //  [代表就是一维数组
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
                "([Ljava/lang/String;)V", null, null);

        {
            //获取变量的数量
            final long localVariablesCount = classDeclaration.getClassScopeInstructions().stream()
                    .filter(instruction -> instruction instanceof VariableDeclaration)
                    .count();
            //栈的深度
            final int maxStack = 100;
            //执行写入字节码的各种指令
            for (ClassScopeInstruction classScopeInstruction : classDeclaration.getClassScopeInstructions()) {
                classScopeInstruction.apply(mv);
            }
            //在方法体内生成一个字节码指令，表示方法的返回，而
            mv.visitInsn(RETURN);
            //设置方法的最大栈深度和局部变量数量，用于字节码生成器做一些优化。
            mv.visitMaxs(maxStack, (int) localVariablesCount);
            //在方法定义结束后标记方法的结束。
            mv.visitEnd();
        }
        cw.visitEnd();
        return cw.toByteArray();
    }



}
