package com.saveunhappy.saitama.compiler.domain.statement;

import com.saveunhappy.saitama.compiler.bytecodegenerator.StatementGenerator;

public interface Statement extends Node {
    void accept(StatementGenerator generator);
}
