package com.saveunhappy.saitama.compiler;

import com.saveunhappy.saitama.antlr.SaitamaLexer;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.bytecodegeneration.CompilationUnit;
import com.saveunhappy.saitama.compiler.parsing.SaitamaTreeWalkErrorListener;
import com.saveunhappy.saitama.compiler.visitor.CompilationUnitVisitor;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;


public class Parser {
    public CompilationUnit getCompilationUnit(String fileAbsolutePath) throws IOException {
        CharStream charStream = new ANTLRFileStream(fileAbsolutePath);
        SaitamaLexer lexer = new SaitamaLexer(charStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SaitamaParser parser = new SaitamaParser(tokenStream);

        ANTLRErrorListener errorListener = new SaitamaTreeWalkErrorListener();
        parser.addErrorListener(errorListener);
        //最原始的，编译的visitor
        CompilationUnitVisitor compilationUnitVisitor = new CompilationUnitVisitor();
        return parser.compilationUnit().accept(compilationUnitVisitor);
    }
}
