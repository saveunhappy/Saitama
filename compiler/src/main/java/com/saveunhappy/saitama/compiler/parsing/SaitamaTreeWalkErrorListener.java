package com.saveunhappy.saitama.compiler.parsing;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
//对语法错误进行监听
public class SaitamaTreeWalkErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        final String errorFormat = "You fucked up at line %d,char %d :(. Details:%n%s";
        final String errorMsg = String.format(errorFormat, line, charPositionInLine, msg);
        System.err.println(errorMsg);
    }
}
