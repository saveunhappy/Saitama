//规定这门语言的名字叫什么
grammar Saitama;
//找g4文件
@header {
package com.saveunhappy.saitama.antlr;
}

//RULES
compilationUnit : classDeclaration EOF ; //root rule - our code consist consist only of variables and prints (see definition below)
classDeclaration : className superClassName* '{' classBody '}' ;
className : ID ;
superClassName : ':' ID ;
classBody :  ( variable | print )* ;
variable : VARIABLE ID EQUALS value; //requires VAR token followed by ID token followed by EQUALS TOKEN ...
print : PRINT ID ; //print statement must consist of 'print' keyword and ID
value : op=NUMBER
      | op=STRING ; //must be NUMBER or STRING value (defined below)

//TOKENS
VARIABLE : 'var' ; //VARIABLE TOKEN must match exactly 'var'
PRINT : 'print' ;
EQUALS : '=' ; //must be '='
NUMBER : [0-9]+ ; //must consist only of digits
STRING : '"'.*'"' ; //must be anything in qutoes
ID : [a-zA-Z0-9]+ ; //must be any alphanumeric value
WS: [ \t\n\r]+ -> skip ; //special TOKEN for skipping whitespaces
