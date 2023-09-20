package com.saveunhappy.saitama.compiler.bytecodegenerator;

import com.saveunhappy.saitama.compiler.CompareSign;
import com.saveunhappy.saitama.compiler.domain.expression.ConditionalExpression;
import com.saveunhappy.saitama.compiler.domain.expression.Expression;
import com.saveunhappy.saitama.compiler.domain.expression.FunctionCall;
import com.saveunhappy.saitama.compiler.domain.expression.VarReference;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.statement.*;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;
import com.saveunhappy.saitama.compiler.domain.type.ClassType;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import org.objectweb.asm.Label;
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
        //String name = variableDeclarationStatement.getName();
        //Type type = expression.getType();
        expression.accept(expressionGenerator);//解析表达式，把1写入栈
        AssignmentStatement assignmentStatement = new AssignmentStatement(variableDeclarationStatement);
        generate(assignmentStatement);//保存变量，把1保存到一个变量中去
    }
//    public void generate(VariableDeclarationStatement variableDeclarationStatement) {
//        Expression expression = variableDeclarationStatement.getExpression();
//        String name = variableDeclarationStatement.getName();
//        //因为之前已经把参数和本地变量都放到了一个存储本地变量的List中，所以这里要获取索引，
//        int index = scope.getLocalVariableIndex(name);
//        Type type = expression.getType();
//        // 然后立马进行保存,VariableDeclarationStatement变量声明，当然要进行保存了，
//        expression.accept(expressionGenerator);
//        if (type == BuiltInType.INT) {
//            methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
//        } else {
//            methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
//        }
//    }

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

    public void generate(IfStatement ifStatement) {
        Expression condition = ifStatement.getCondition();
        condition.accept(expressionGenerator);
        Label trueLabel = new Label();
        Label endLabel = new Label();
        //现在栈顶是ICONST_1，那么是对的，IFNE，不等于0,跳到true。
        methodVisitor.visitJumpInsn(Opcodes.IFNE, trueLabel);
        ifStatement.getFalseStatement().accept(this);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        ifStatement.getTrueStatement().accept(this);
        methodVisitor.visitLabel(endLabel);
    }
//    public void generate(IfStatement ifStatement) {
//        Expression condition = ifStatement.getCondition();
//        condition.accept(expressionGenerator);
//        //注意，在condition.accept(expressionGenerator);代码之后，
//        //栈上面的字节码是ICONST_0或者ICONST_1,
//        Label trueLabel = new Label();
//        //TODO IFEQ是看是不是等于0，上面假设相等，那么现在栈顶就是ICONST_1，
//        //TODO 所以是false，那么就接着执行ifStatement.getTrueStatement().accept(this);
//        //TODO 因为字节码是GOTO那种类型的，所以如果你想按照正常的那种，if(true){}else{}
//        //TODO 你执行完IF之后是一定要跳转到一个位置的，你可以在这里反转，IFEQ改为IFNE
//        //TODO 但是我们是在操作符上做了改变，==对应的字节码就是不等于，这样相等就是放的就是
//        //TODO ICONST_1，然后IFEQ判断是不是0，如果是0跳转到trueLabel,这里是1，然后就不跳转
//        //TODO 执行了if下面的操作，重构那本书中也有，将条件表达式反转
//        methodVisitor.visitJumpInsn(Opcodes.IFEQ, trueLabel);
//        ifStatement.getTrueStatement().accept(this);
//        Label falseLabel = new Label();
//        methodVisitor.visitJumpInsn(Opcodes.GOTO, falseLabel);//如果是true,那么执行了ifStatement.getTrueStatement().accept(this);就该结束这个了，所以跳到false结束的地方
//        methodVisitor.visitLabel(trueLabel);
//        //表示栈帧没有发生改变，有可能你调用了一个方法之后消耗了栈帧，但是这里没有，告诉JVM，确实没有消耗栈帧
//        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//        ifStatement.getFalseStatement().accept(this);
//        methodVisitor.visitLabel(falseLabel);
//        //表示栈帧没有发生改变，有可能你调用了一个方法之后消耗了栈帧，但是这里没有，告诉JVM，确实没有消耗栈帧
//        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
//    }

    public void generate(Block block) {
        Scope newScope = block.getScope();
        List<Statement> statements = block.getStatements();
        StatementGenerator statementGenerator = new StatementGenerator(methodVisitor, newScope);
        statements
                .forEach(stmt -> stmt.accept(statementGenerator));
    }

    public void generate(RangedForStatement rangedForStatement) {
        Scope newScope = rangedForStatement.getScope();//获取自己scope的变量
        StatementGenerator scopeGeneratorWithNewScope = new StatementGenerator(methodVisitor, newScope);
        ExpressionGenerator exprGeneratorWithNewScope = new ExpressionGenerator(methodVisitor, newScope);
        Statement iterator = rangedForStatement.getIteratorVariableStatement();//获取变量的表达式，有变量名和初始值
        Label incrementationSection = new Label();
        Label decrementationSection = new Label();
        Label endLoopSection = new Label();
        String iteratorVarName = rangedForStatement.getIteratorVarName();//获取变量名
        Expression endExpression = rangedForStatement.getEndExpression();
        Expression iteratorVariable = new VarReference(iteratorVarName, rangedForStatement.getType());
        ConditionalExpression iteratorGreaterThanEndConditional = new ConditionalExpression(iteratorVariable, endExpression, CompareSign.GREATER);
        ConditionalExpression iteratorLessThanEndConditional = new ConditionalExpression(iteratorVariable, endExpression, CompareSign.LESS);
        /**上面为啥要创建两个，因为实现的功能是1 to 10就是递增，10 to 1就是递减，
             就比较谁大谁小决定递增还是递减*/
        iterator.accept(scopeGeneratorWithNewScope);//变量声明，就是x，循环变量
        // 大于的话，跳转到一个位置，传进去的这个对象是只有scope和classWriter，
        // 就是要进行写入字节码的，他本身就是一个Expression，所以可以把ExpressionVisitor传进去，
        // 进行生成字节码，然后iteratorLessThanEndConditional对象是有开始和结束的数字的，
        // 还有比大还是比小，又是一个新的Expression或者是Statement
        iteratorLessThanEndConditional.accept(exprGeneratorWithNewScope);
        methodVisitor.visitJumpInsn(Opcodes.IFNE,incrementationSection);
        // 大于的话，跳转到一个位置，传进去的这个对象是只有scope和classWriter，
        // 就是要进行写入字节码的，他本身就是一个Expression，所以可以把ExpressionVisitor传进去，
        // 进行生成字节码，然后iteratorLessThanEndConditional对象是有开始和结束的数字的，
        // 还有比大还是比小，又是一个新的Expression或者是Statement
        iteratorGreaterThanEndConditional.accept(exprGeneratorWithNewScope);
        methodVisitor.visitJumpInsn(Opcodes.IFNE,decrementationSection);
        /** 就当成在写汇编，incrementationSection就跳到这，decrementationSection就跳到后面，*/
        methodVisitor.visitLabel(incrementationSection);
        rangedForStatement.getStatement().accept(scopeGeneratorWithNewScope);
        /** 执行完之后，变量的值就加1*/
        methodVisitor.visitIincInsn(newScope.getLocalVariableIndex(iteratorVarName),1);
        //继续去进行比较
        iteratorGreaterThanEndConditional.accept(exprGeneratorWithNewScope);
        /** 这里如果不等于0，就又跳到上面去了*/
        methodVisitor.visitJumpInsn(Opcodes.IFEQ,incrementationSection);
        methodVisitor.visitJumpInsn(Opcodes.GOTO,endLoopSection);

        methodVisitor.visitLabel(decrementationSection);
        rangedForStatement.getStatement().accept(scopeGeneratorWithNewScope);
        /** 这里就减1,没有减法，就加上-1,就是减一的操作*/
        methodVisitor.visitIincInsn(newScope.getLocalVariableIndex(iteratorVarName),-1);
        //继续去进行比较
        iteratorLessThanEndConditional.accept(exprGeneratorWithNewScope);
        /** 这里如果不等于0，就又跳到上面去了*/
        methodVisitor.visitJumpInsn(Opcodes.IFEQ,decrementationSection);

        methodVisitor.visitLabel(endLoopSection);
    }


    public void generate(AssignmentStatement assignmentStatement) {
        String varName = assignmentStatement.getVarName();
        Type type = assignmentStatement.getExpression().getType();
        int index = scope.getLocalVariableIndex(varName);
        if (type == BuiltInType.INT || type == BuiltInType.BOOLEAN) {
            methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
        }
    }
}
