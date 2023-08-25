package com.saveunhappy.saitama.compiler.bytecodegenerator;

import com.saveunhappy.saitama.compiler.domain.clazz.Function;
import com.saveunhappy.saitama.compiler.domain.global.ClassDeclaration;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class ClassGenerator {
    private static final int CLASS_VERSION = 52;
    private ClassWriter classWriter;

    /**
     * COMPUTE_FRAMES：这个参数告诉 ClassWriter 计算栈映射帧。栈映射帧是字节码中的一部分，用于帮助 JVM 在运行时进行类型检查和优化。
     * COMPUTE_MAXS：这个参数告诉 ClassWriter 计算最大栈深度和局部变量的最大数量。这对于栈帧的计算非常重要。
     */
    public ClassGenerator() {
        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
    }

    public ClassWriter generate(ClassDeclaration classDeclaration) {
        //现在的类声明中只有方法，没有属性，所以只需要类的名字和这个类里面所包含的方法
        String name = classDeclaration.getName();
        classWriter.visit(
                CLASS_VERSION,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                name,
                null,
                "java/lang/Object",
                null
        );

        List<Function> methods = classDeclaration.getMethods();
        //每个方法都去生成写入字节，那么就需要使用classWriter,这个就是在这个类里面生成的，所以需要传过去
        //每个方法按照顺序去写入字节，等到最后，都写入字节类，就到classWriter了，
        //可以认为classWriter数一个字节数组
        methods.forEach(function -> new MethodGenerator(classWriter).generate(function));
        classWriter.visitEnd();
        return classWriter;
    }
}
