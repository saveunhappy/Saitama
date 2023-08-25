package com.saveunhappy.saitama.compiler.bytecodegenerator;

import com.saveunhappy.saitama.compiler.domain.expression.*;
import com.saveunhappy.saitama.compiler.domain.math.*;
import com.saveunhappy.saitama.compiler.domain.scope.LocalVariable;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;
import com.saveunhappy.saitama.compiler.domain.type.BuiltInType;
import com.saveunhappy.saitama.compiler.domain.type.ClassType;
import com.saveunhappy.saitama.compiler.domain.type.Type;
import com.saveunhappy.saitama.compiler.exception.CalledFunctionDoesNotExistException;
import com.saveunhappy.saitama.compiler.utils.DecriptorFactory;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.Collection;
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
     *之前是保存到对应到位置，index就是你添加到本地变量到List中去的,这里就来加载
     * */
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
        Collection<Expression> parameters = functionCall.getParameters();
        //这里是核心，你调用的话，这里就先加载了；在main方法中是x,y
        parameters.forEach(param -> param.accept(this));
        Type owner = functionCall.getOwner().orElse(new ClassType(scope.getClassName()));
        String methodDescriptor = getFunctionDescriptor(functionCall);
        String ownerDescriptor = owner.getInternalName();
        String functionName = functionCall.getFunctionName();
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, ownerDescriptor, functionName, methodDescriptor, false);
    }

    public void generate(Addition expression) {
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.IADD);
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
            Collection<Expression> parameters = functionCall.getParameters();
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
