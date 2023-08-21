package com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions;

import com.saveunhappy.saitama.antlr.SaitamaLexer;
import com.saveunhappy.saitama.compiler.parsing.domain.Variable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
//变量声明
public class VariableDeclaration implements ClassScopeInstruction, Opcodes {

    Variable variable;

    public VariableDeclaration(Variable variable) {
        this.variable = variable;
    }

    @Override
    public void apply(MethodVisitor mv) {
        final int type = variable.getType();
        if (type == SaitamaLexer.NUMBER) {
            //获取到值
            int val = Integer.parseInt(variable.getValue());
            //推到栈顶
            mv.visitIntInsn(BIPUSH, val);
            //保存变量，并且记录位置
            mv.visitVarInsn(ISTORE, variable.getId());
        } else if (type == SaitamaLexer.STRING) {
            mv.visitLdcInsn(variable.getValue());
            mv.visitVarInsn(ASTORE, variable.getId());
        }
    }
}
