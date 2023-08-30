//规定这门语言的名字叫什么
grammar Saitama;
//找g4文件
@header {
package com.saveunhappy.saitama.antlr;
}

//RULES
compilationUnit : classDeclaration EOF ;
//类声明
classDeclaration : className '{' classBody '}' ;
//类名
className : ID ;
//类的内容，目前是只有方法0个或者是多个
classBody :  function* ;
//方法声明，方法体
function : functionDeclaration block;
//functionDeclaration : (type)? functionName '('(functionArgument)*')' ;
//返回值类型（可选） 方法名，参数列表，可以是多个
functionDeclaration : (type)? functionName '(' (functionParameter (',' functionParameter)*)?')' ;
//方法名
functionName : ID ;
//方法参数，String a;可以有默认值，比如String a = "a"
functionParameter : type ID functionParamdefaultValue? ;
//a = a
functionParamdefaultValue : '=' expression ;
//原生类型和引用类型
type : primitiveType
     | classType ;

primitiveType :  'boolean' ('[' ']')*
                |   'string' ('[' ']')*
                |   'char' ('[' ']')*
                |   'byte' ('[' ']')*
                |   'short' ('[' ']')*
                |   'int' ('[' ']')*
                |   'long' ('[' ']')*
                |   'float' ('[' ']')*
                |   'double' ('[' ']')*
                |   'void' ('[' ']')* ;
classType : QUALIFIED_NAME ('[' ']')* ;
//语句中可以有变量的声明，打印变量还有函数调用
block : '{' statement* '}' ;

statement :     block
               | variableDeclaration
               | printStatement
               | functionCall
               | returnStatement
               | ifStatement;

//var a = 1
variableDeclaration : VARIABLE name EQUALS expression;
//print a  print 1
printStatement : PRINT expression ;

returnStatement : 'return' #RETURNVOID
                | ('return')? expression #RETURNWITHVALUE ;
//函数调用
functionCall : functionName '('argument? (',' argument)* ')';

argument : expression
         | name '->' expression ;
ifStatement: 'if'  ('(')? expression (')')? trueStatement=statement ('else' falseStatement=statement)?;

name : ID ;
//参数列表
expressionList : expression? (',' expression)* ;

expression : variableReference #VarReference
           | value        #ValueExpr
           | functionCall #FUNCALL
           | '(' expression '/' expression ')' #DIVIDE
           | expression '/' expression #DIVIDE
           |  '('expression '*' expression')' #MULTIPLY
           | expression '*' expression  #MULTIPLY
           | '(' expression '+' expression ')' #ADD
           | expression '+' expression #ADD
           | '(' expression '-' expression ')' #SUBSTRACT
           | expression '-' expression #SUBSTRACT
           | expression cmp='>' expression #conditionalExpression
           | expression cmp='<' expression #conditionalExpression
           | expression cmp='==' expression #conditionalExpression
           | expression cmp='!=' expression #conditionalExpression
           | expression cmp='>=' expression #conditionalExpression
           | expression cmp='<=' expression #conditionalExpression
           ;

variableReference : ID ;
value : NUMBER
      | STRING ;
//TOKENS
VARIABLE : 'var' ;
PRINT : 'print' ;
EQUALS : '=' ;
NUMBER : '-'?[0-9]+ ;
STRING : '"'~('\r' | '\n' | '"')*'"' ;
ID : [a-zA-Z0-9]+ ;
QUALIFIED_NAME : ID ('.' ID)+;
WS: [ \t\n\r]+ -> skip ;
