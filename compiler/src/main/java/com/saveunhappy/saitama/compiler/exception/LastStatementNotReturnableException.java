package com.saveunhappy.saitama.compiler.exception;

import com.saveunhappy.saitama.compiler.domain.statement.Statement;

public class LastStatementNotReturnableException extends RuntimeException {
    public LastStatementNotReturnableException(Statement lastStatement) {
        super("The statement " + lastStatement + " is a last statement in a functon, but it is not an expression!");
    }
}
