package com.saveunhappy.saitama.compiler.exception;

import com.saveunhappy.saitama.compiler.domain.scope.Scope;

public class LocalVariableNotFoundException extends RuntimeException {
    public LocalVariableNotFoundException(Scope scope, String variableName) {
        super("No local varaible found for name " + variableName + "found in scope" + scope);
    }
}
