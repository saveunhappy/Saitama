package com.saveunhappy.saitama.compiler.bytecodegenerator;

import com.saveunhappy.saitama.compiler.domain.expression.Expression;
import com.saveunhappy.saitama.compiler.domain.expression.FunctionCall;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.statement.*;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;
import com.saveunhappy.saitama.compiler.domain.type.ClassType;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class StatementGenerator {
    private MethodVisitor methodVisitor;
    private ExpressionGenerator expressionGenerator;
    private Scope scope;

    public StatementGenerator(MethodVisitor methodVisitor, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.scope = scope;
        expressionGenerator = new ExpressionGenerator(methodVisitor, scope);
    }

    public void generate(PrintStatement printStatement) {
        Expression expression = printStatement.getExpression();
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        expression.accept(expressionGenerator);
        Type type = expression.getType();
        String descriptor = "(" + type.getDescriptor() + ")V";
        ClassType owner = new ClassType("java.io.PrintStream");
        String fieldDescriptor = owner.getInternalName();
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, fieldDescriptor, "println", descriptor, false);
    }

    public void generate(VariableDeclarationStatement variableDeclarationStatement) {
        Expression expression = variableDeclarationStatement.getExpression();
        String name = variableDeclarationStatement.getName();
        //因为之前已经把参数和本地变量都放到了一个存储本地变量的List中，所以这里要获取索引，
        int index = scope.getLocalVariableIndex(name);
        Type type = expression.getType();
        // 然后立马进行保存
        expression.accept(expressionGenerator);
        if (type == BuiltInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
        }
    }

    public void generate(FunctionCall functionCall) {
        functionCall.accept(expressionGenerator);
    }

    public void generate(ReturnStatement returnStatement) {
        Expression expression = returnStatement.getExpression();
        Type type = expression.getType();
        expression.accept(expressionGenerator);
        if (type == BuiltInType.VOID) {
            methodVisitor.visitInsn(Opcodes.RETURN);
        } else if (type == BuiltInType.INT) {
            methodVisitor.visitInsn(Opcodes.IRETURN);
        }
    }


    public void generate(Block block) {
        Scope newScope = block.getScope();
        List<Statement> statements = block.getStatements();
        StatementGenerator statementGenerator = new StatementGenerator(methodVisitor, newScope);
        statements.stream()
                .forEach(stmt -> stmt.accept(statementGenerator));
    }
}
