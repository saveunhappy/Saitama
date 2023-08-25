package com.saveunhappy.saitama.compiler.exception;

import com.saveunhappy.saitama.compiler.domain.expression.FunctionCall;
import com.saveunhappy.saitama.compiler.domain.scope.Scope;

public class CalledFunctionDoesNotExistException extends CompilationException {
    FunctionCall functionCall;

    public CalledFunctionDoesNotExistException(FunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    public CalledFunctionDoesNotExistException(FunctionCall functionCall, ReflectiveOperationException e) {
        this(functionCall);
    }

    public CalledFunctionDoesNotExistException(FunctionCall functionCall, Scope scope) {
        this(functionCall);
    }



    @Override
    public String getMessage() {
        return "Function call" + functionCall.toString() + "does not exists";
    }
}
