package com.saveunhappy.saitama.compiler;

import com.saveunhappy.saitama.antlr.SaitamaLexer;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.domain.global.CompilationUnit;
import com.saveunhappy.saitama.compiler.parsing.SaitamaTreeWalkErrorListener;
import com.saveunhappy.saitama.compiler.visitor.CompilationUnitVisitor;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;


public class Parser {
    public CompilationUnit getCompilationUnit(String fileAbsolutePath) throws IOException {
        //读取文件的内容
        CharStream charStream = new ANTLRFileStream(fileAbsolutePath);
        //词法分析
        SaitamaLexer lexer = new SaitamaLexer(charStream);
        //解析token
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        //进行解析，这个时候就已经有抽象语法树了。
        SaitamaParser parser = new SaitamaParser(tokenStream);

        ANTLRErrorListener errorListener = new SaitamaTreeWalkErrorListener();
        parser.addErrorListener(errorListener);
        //最原始的，编译的visitor
        CompilationUnitVisitor compilationUnitVisitor = new CompilationUnitVisitor();
        //parser.compilationUnit()就是antlr自己定义的规则，这里的compilation是个方法，返回的是上下文
        //accept需要ParseTreeVisitor的子类，所以需要你自己定义类，自己去定义你想做的事儿，
        return parser.compilationUnit().accept(compilationUnitVisitor);
    }
}
