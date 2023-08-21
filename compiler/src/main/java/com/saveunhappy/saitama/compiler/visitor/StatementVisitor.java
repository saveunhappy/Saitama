package com.saveunhappy.saitama.compiler.visitor;

import com.saveunhappy.saitama.antlr.SaitamaBaseVisitor;
import com.saveunhappy.saitama.antlr.SaitamaParser;
import com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions.ClassScopeInstruction;
import com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions.PrintVariable;
import com.saveunhappy.saitama.compiler.bytecodegeneration.classscopeinstructions.VariableDeclaration;
import com.saveunhappy.saitama.compiler.parsing.domain.Variable;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class StatementVisitor extends SaitamaBaseVisitor<ClassScopeInstruction> {
    private Queue<ClassScopeInstruction> classScopeInstructions = new ArrayDeque<>();
    private Map<String, Variable> variables = new HashMap<>();

    @Override
    public VariableDeclaration visitVariable(SaitamaParser.VariableContext ctx) {
        TerminalNode varName = ctx.ID();
        SaitamaParser.ValueContext varValue = ctx.value();
        int varType = varValue.getStart().getType();
        int varIndex = variables.size();
        String varTextValue = varValue.getText();
        Variable var = new Variable(varIndex, varType, varTextValue);
        variables.put(varName.getText(), var);
        VariableDeclaration variableDeclaration = new VariableDeclaration(var);
        classScopeInstructions.add(new VariableDeclaration(var));
        logVariableDeclarationStatementFound(varName, varValue);
        return variableDeclaration;
    }

    @Override
    public PrintVariable visitPrint(SaitamaParser.PrintContext ctx) {
        TerminalNode varName = ctx.ID();
        boolean printedVarNotDeclared = !variables.containsKey(varName.getText());
        if (printedVarNotDeclared) {
            final String erroFormat = "ERROR: WTF? You are trying to print var '%s' which has not been declared!!!.";
            System.err.printf(erroFormat, varName.getText());
            return null;
        }
        final Variable variable = variables.get(varName.getText());
        PrintVariable printVariable = new PrintVariable(variable);
        classScopeInstructions.add(printVariable);
        logPrintStatementFound(varName, variable);
        return printVariable;
    }


    private void logVariableDeclarationStatementFound(TerminalNode varName, SaitamaParser.ValueContext varValue) {
        final int line = varName.getSymbol().getLine();
        final String format = "OK: You declared variable named '%s' with value of '%s' at line '%s'.\n";
        System.out.printf(format, varName, varValue.getText(), line);
    }

    private void logPrintStatementFound(TerminalNode varName, Variable variable) {
        final int line = varName.getSymbol().getLine();
        final String format = "OK: You instructed to print variable '%s' which has value of '%s' at line '%s'.'\n";
        System.out.printf(format, variable.getId(), variable.getValue(), line);
    }

    public Queue<ClassScopeInstruction> getClassScopeInstruction() {
        return classScopeInstructions;
    }
}
