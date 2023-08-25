package com.saveunhappy.saitama.compiler.exception;

import com.saveunhappy.saitama.compiler.domain.scope.Scope;

public class MethodSignatureNotFoundException extends RuntimeException {
    public MethodSignatureNotFoundException(Scope scope, String methodName) {
        super("There is no method" + methodName);
    }
}
