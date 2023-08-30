package com.saveunhappy.saitama.compiler.bytecodegenerator;

import com.saveunhappy.saitama.compiler.CompareSign;
import com.saveunhappy.saitama.compiler.domain.expression.*;
import com.saveunhappy.saitama.compiler.domain.math.*;
import com.saveunhappy.saitama.compiler.domain.scope.FunctionSignature;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;
import com.saveunhappy.saitama.compiler.domain.type.ClassType;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.exception.BadArgumentsToFunctionCallException;
import com.saveunhappy.saitama.compiler.exception.CalledFunctionDoesNotExistException;
import com.saveunhappy.saitama.compiler.exception.ComparisonBetweenDifferentTypesException;
import com.saveunhappy.saitama.compiler.utils.DecriptorFactory;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ExpressionGenerator {
    private MethodVisitor methodVisitor;
    //原来是需要每个表达式之类的把scope都获取到传过来，
    //现在是statement包含expression生成的东西和method生成相关的东西，相当于就是模板模式
    //原来的compositeVisitor没有了，原来是StatementVisitor和ExpressionVisitor
    //现在Expression继承自Statement，所以就去掉那个东西了。
    /***
     *
     *         StatementVisitor statementVisitor = new StatementVisitor(scope);
     *         ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
     *         CompositeVisitor<Statement> compositeVisitor = new CompositeVisitor<>(statementVisitor, expressionVisitor);
     *         return ctx.blockStatement().stream()
     *                 .map(compositeVisitor::accept)
     *                 .collect(Collectors.toList());
     *
     */
    private Scope scope;

    public ExpressionGenerator(MethodVisitor methodVisitor, Scope scope) {
        this.scope = scope;
        this.methodVisitor = methodVisitor;
    }

    /**
     * 之前是保存到对应到位置，index就是你添加到本地变量到List中去的,这里就来加载
     */
    public void generate(VarReference varReference) {
        String varName = varReference.getVarName();
        int index = scope.getLocalVariableIndex(varName);
        LocalVariable localVariable = scope.getLocalVariable(varName);
        Type type = localVariable.getType();
        if (type == BuiltInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, index);
        }
    }

    public void generate(FunctionParameter parameter) {
        Type type = parameter.getType();
        int index = scope.getLocalVariableIndex(parameter.getName());
        if (type == BuiltInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, index);
        }
    }


    public void generate(Value value) {
        Type type = value.getType();
        String stringValue = value.getValue();
        if (type == BuiltInType.INT) {
            int intValue = Integer.parseInt(stringValue);
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, intValue);
        } else if (type == BuiltInType.STRING) {
            stringValue = StringUtils.removeStart(stringValue, "\"");
            stringValue = StringUtils.removeEnd(stringValue, "\"");
            methodVisitor.visitLdcInsn(stringValue);
        }
    }

    //  怎么把参数传递过去的呢？调用的话，你首先要获取到你之前声明的参数吧，
    //  然后你把他们都加载，iload,aload之类的，然后，都在栈上了，进行调用,invokeVirtual，
    //  这样子。你是怎么知道参数的个数呢？第一行代码就是获取到你当时创建方法调用的参数，这里就循环就好了
    public void generate(FunctionCall functionCall) {
        String functionName = functionCall.getFunctionName();
        //方法签名，有参数有默认值的话，那么在方法签名中是有值的。
        FunctionSignature signature = functionCall.getSignature();
        //arguments是实参，比如方法调用sum(a,b)
        List<Expression> arguments = functionCall.getArguments();
        List<FunctionParameter> parameters = signature.getParameters();
        //如果实参的个数大于方法声明的形参的个数，那么就报错，
        /**
         * sum(a,b,c)
         * int sum(a,b)
         * 这样就会报错
         * */
        if (arguments.size() > parameters.size()) {
            throw new BadArgumentsToFunctionCallException(functionCall);
        }
        /**如果形参没有全部填上，说明有默认值，那么就从你填完了的那个索引开始往后的都取默认值，
         * 就是和可变参数一样
         * 比如 int sum(int x,int y = 1){x + y}
         *  调用的话可以是sum(a,b)
         *  也可以是sum(a)
         *  那么arguments.forEach(argument -> argument.accept(this));就是先把a写入
         *  字节码中去。
         *          for (int i = arguments.size(); i < parameters.size(); i++) {
         *             Expression defaultParameter = parameters.get(i).getDefaultValue()
         *                     .orElseThrow(() -> new BadArgumentsToFunctionCallException(functionCall));
         *             defaultParameter.accept(this);
         *         }
         *         就是去取出来b的值去放到字节码中去。
         * */
        arguments.forEach(argument -> argument.accept(this));
        for (int i = arguments.size(); i < parameters.size(); i++) {
            Expression defaultParameter = parameters.get(i).getDefaultValue()
                    .orElseThrow(() -> new BadArgumentsToFunctionCallException(functionCall));
            defaultParameter.accept(this);
        }
        Type owner = functionCall.getOwner().orElse(new ClassType(scope.getClassName()));
        String methodDescriptor = getFunctionDescriptor(functionCall);
        String ownerDescriptor = owner.getInternalName();
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, ownerDescriptor, functionName, methodDescriptor, false);
    }

    //  怎么把参数传递过去的呢？调用的话，你首先要获取到你之前声明的参数吧，
    //  然后你把他们都加载，iload,aload之类的，然后，都在栈上了，进行调用,invokeVirtual，
    //  这样子。你是怎么知道参数的个数呢？第一行代码就是获取到你当时创建方法调用的参数，这里就循环就好了
//    public void generate(FunctionCall functionCall) {
//        Collection<Expression> parameters = functionCall.getParameters();
//        //这里是核心，你调用的话，这里就先加载了；在main方法中是x,y
//        parameters.forEach(param -> param.accept(this));
//        Type owner = functionCall.getOwner().orElse(new ClassType(scope.getClassName()));
//        String methodDescriptor = getFunctionDescriptor(functionCall);
//        String ownerDescriptor = owner.getInternalName();
//        String functionName = functionCall.getFunctionName();
//        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, ownerDescriptor, functionName, methodDescriptor, false);
//    }

    public void generate(Addition expression) {
        if (expression.getType().equals(BuiltInType.STRING)) {
            generateStringAppend(expression);//这个type默认是使用左边的
            return;
        }
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.IADD);
    }
    public void generateStringAppend(Addition expression) {
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);

        Expression leftExpression = expression.getLeftExpression();
        leftExpression.accept(this);
        String leftExprDescriptor = leftExpression.getType().getDescriptor();
        String descriptor = "("+leftExprDescriptor+ ")Ljava/lang/StringBuilder;";
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        
        Expression rightExpression = expression.getRightExpression();
        rightExpression.accept(this);
        String rightExprDescriptor = rightExpression.getType().getDescriptor();
        descriptor = "("+rightExprDescriptor+ ")Ljava/lang/StringBuilder;";
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    }

    public void generate(Substraction expression) {
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.ISUB);
    }

    public void generate(Multiplication expression) {
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.IMUL);
    }

    public void generate(Division expression) {
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.IDIV);
    }

    public void generate(EmptyExpression expression) {
        System.out.println("do nothing,asm deal");
        // do nothing
    }

    public void generate(ConditionalExpression conditionalExpression) {
        Expression leftExpression = conditionalExpression.getLeftExpression();
        Expression rightExpression = conditionalExpression.getRightExpression();
        Type type = leftExpression.getType();
        if (type != rightExpression.getType()) {
            throw new ComparisonBetweenDifferentTypesException(leftExpression, rightExpression);
        }

        leftExpression.accept(this);//这里就是去加载变量名了，比如expected
        rightExpression.accept(this);//这里也是去加载变量,actual
        CompareSign compareSign = conditionalExpression.getCompareSign();
        Label endLabel = new Label();
        /*这里的比较运算符是相反的，比如==，但是opcode是IF_ICMPNE, 如果expected NOT_EQ actual,就跳转到false的Label */
        // == IF_ICMPEQ 8 == 5+3 所以是true，那么句推送常量1
        Label trueLabel = new Label();
        methodVisitor.visitJumpInsn(compareSign.getOpcode(), trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_0);//推送常量0
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_1);//推送常量1
        methodVisitor.visitLabel(endLabel);
    }

    private void evaluateArthimeticComponents(ArthimeticExpression expression) {
        Expression leftExpression = expression.getLeftExpression();
        Expression rightExpression = expression.getRightExpression();
        leftExpression.accept(this);
        rightExpression.accept(this);
    }

    public String getFunctionDescriptor(FunctionCall functionCall) {
        return Optional.of(getDescriptorForFunctionInScope(functionCall, scope))
                .orElse(getDescriptorForFunctionOnClasspath(functionCall, scope))
                .orElseThrow(() -> new CalledFunctionDoesNotExistException(functionCall, scope));
    }

    private Optional<String> getDescriptorForFunctionInScope(FunctionCall functionCall, Scope scope) {
        return Optional.of(DecriptorFactory.getMethodDescriptor(functionCall.getSignature()));//TODO check errors here (not found function tec)
    }

    private Optional<String> getDescriptorForFunctionOnClasspath(FunctionCall functionCall, Scope scope) {
        try {
            String functionName = functionCall.getFunctionName();
            Optional<Type> owner = functionCall.getOwner();
            String className = owner.isPresent() ? owner.get().getName() : scope.getClassName();
            Class<?> aClass = Class.forName(className);
            Method method = aClass.getMethod(functionName);
            String methodDescriptor = org.objectweb.asm.Type.getMethodDescriptor(method);
            return Optional.of(methodDescriptor);
        } catch (ReflectiveOperationException e) {
            return Optional.empty();
        }
    }

}
