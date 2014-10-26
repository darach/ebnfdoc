// Copyright (c) 2014 Darach Ennis < darach at gmail dot com >.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:  
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.

grammar Ebnf;

ebnf: doc? statement*;

statement: doc? lhs Equals rhs Semi;

lhs: Id ;

rhs: doc? clause (op clause)*;

op: PipePipe | Pipe | Comma;

clause: 
    (LBrace rhs RBrace) |
    (LSquigly rhs RSquigly) |
    (LCurly rhs RCurly) |
    (Question Id Question) |
    literal
    ;

literal:
    Id |
    Terminal
    ;

doc: DOC_COMMENT;

Comma: ',';
LBrace: '[';
RBrace: ']';
LSquigly: '{';
RSquigly: '}';
LCurly: '(';
RCurly: ')';
Pipe: '|';
PipePipe: '||';
Equals: '=';
Question : '?';
Semi: ';';
Id: W;
Terminal: ('\'' .*? '\'') | ('"' .*? '"');

fragment W: A+;
fragment A: (D|U|L|'_');
fragment D: [0-9];
fragment U: [A-Z];
fragment L: [a-z];

WS  :  [ \t\r\n\u000C]+ -> skip
    ;

DOC_COMMENT
    :   '(**' .*? '*)'
    ;

COMMENT
    :   '(*' .*? '*)'
    ;
