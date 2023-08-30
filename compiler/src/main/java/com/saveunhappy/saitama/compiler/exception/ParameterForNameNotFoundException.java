package com.saveunhappy.saitama.compiler.exception;

import com.saveunhappy.saitama.compiler.domain.expression.FunctionParameter;

import java.util.List;

public class ParameterForNameNotFoundException extends RuntimeException{
    public ParameterForNameNotFoundException(String name, List<FunctionParameter> parameters) {
    }
}
