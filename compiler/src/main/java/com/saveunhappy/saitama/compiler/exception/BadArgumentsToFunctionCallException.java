package com.saveunhappy.saitama.compiler.exception;

import com.saveunhappy.saitama.compiler.domain.expression.FunctionCall;

public class BadArgumentsToFunctionCallException extends RuntimeException {
    public BadArgumentsToFunctionCallException(FunctionCall functionCall) {
    }
}
