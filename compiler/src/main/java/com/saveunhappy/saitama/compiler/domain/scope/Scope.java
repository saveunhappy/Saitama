package com.saveunhappy.saitama.compiler.domain.scope;

import com.google.common.collect.Lists;
import com.saveunhappy.saitama.compiler.domain.global.MetaData;
import com.saveunhappy.saitama.compiler.exception.LocalVariableNotFoundException;
import com.saveunhappy.saitama.compiler.exception.MethodSignatureNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class Scope {
    private List<LocalVariable> localVariables;
    private List<FunctionSignature> functionSignatures;
    private MetaData metaData;

    public Scope(MetaData metaData) {
        this.metaData = metaData;
        localVariables = new ArrayList<>();
        functionSignatures = new ArrayList<>();
    }

    public Scope(Scope scope) {
        metaData = scope.metaData;
        localVariables = Lists.newArrayList(scope.localVariables);
        functionSignatures = Lists.newArrayList(scope.functionSignatures);
    }

    public void addSignature(FunctionSignature signature) {
        functionSignatures.add(signature);
    }
    //方法调用的时候有用，你调用的方法不能不存在，所以在解析类的时候第一步就获取到了所有的方法签名，
    //如果没有的话，那么就抛出异常
    public FunctionSignature getSignature(String methodName) {
        return functionSignatures.stream()
                .filter(signature -> signature.getName().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new MethodSignatureNotFoundException(this, methodName));
    }

    public void addLocalVariable(LocalVariable localVariable) {
        localVariables.add(localVariable);
    }


    public LocalVariable getLocalVariable(String varName) {
        return localVariables.stream()
                .filter(variable -> variable.getName().equals(varName))
                .findFirst()
                .orElseThrow(() -> new LocalVariableNotFoundException(this, varName));
    }

    public int getLocalVariableIndex(String varName) {
        LocalVariable localVariable = getLocalVariable(varName);
        return localVariables.indexOf(localVariable);
    }

    public String getClassName() {
        return metaData.getClassName();
    }

}
