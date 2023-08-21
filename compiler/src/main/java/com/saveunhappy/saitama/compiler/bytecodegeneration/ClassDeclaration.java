package com.saveunhappy.saitama.compiler.bytecodegeneration;

import com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions.ClassScopeInstruction;

import java.util.ArrayDeque;
import java.util.Queue;

//类的声明,里面有方法和变量，属性声明
public class ClassDeclaration {
    private Queue<ClassScopeInstruction> classScopeInstructions = new ArrayDeque<>();
    private String name;


    public ClassDeclaration(Queue<ClassScopeInstruction> classScopeInstructions, String name) {
        this.classScopeInstructions = classScopeInstructions;
        this.name = name;
    }

    public void add(ClassScopeInstruction instruction) {
        this.classScopeInstructions.add(instruction);
    }


    public Queue<ClassScopeInstruction> getClassScopeInstructions() {
        return classScopeInstructions;
    }

    public String getName() {
        return name;
    }
}
