grammar Shed;

module: moduleDecl importStmt* moduleStatement* ;

moduleStatement: variableDecl | functionDecl | shapeDecl | labelDecl ;
functionStatement: variableDecl | expression ';' | 'return' expression ';' ;

moduleDecl: 'module' moduleIdentifier ';' ;

importStmt: 'import' ( moduleIdentifier | qualifiedReference ) ';' ;

qualifiedReference: moduleIdentifier identifier ;
identifier: TypeIdentifier | labelIdentifier | VariableIdentifier ;
TypeIdentifier: Upper IdentifierChar* ;

typeDef
  : '(' typeDef ')'
  | typeReference
  | structuralType
  | typeDef '&' typeDef
  | <assoc=right> typeDef '->' typeDef
  ;
typeReference: moduleIdentifier? TypeIdentifier ;
structuralType: '{' ( labelReference ',' )* labelReference ','?  '}' ;

labelReference : moduleIdentifier? labelIdentifier ;
labelDecl: 'label' labelIdentifier ':' typeDef ';' ;
labelIdentifier: '.' VariableIdentifier ;

shapeDecl: 'shape' TypeIdentifier '=' typeDef ';' ;

variableReference: moduleIdentifier? VariableIdentifier ;
variableDecl: 'val' VariableIdentifier ( ':' typeDef )? '=' expression ';' ;
VariableIdentifier: Lower IdentifierChar* ;
moduleIdentifier: '|' VariableIdentifier ( '.' VariableIdentifier )* '|' ;

functionDecl: 'fun' VariableIdentifier '(' parameters ')' ':' typeDef '{' functionStatement* '}' ;
parameters: ( ( parameter ',' )* parameter )? ;
parameter: VariableIdentifier ':' typeDef ;

arguments: ( ( expression ',' )* expression )? ;

expression
  : intLiteral
  | stringLiteral
  | structureLiteral
  | variableReference
  | expression labelReference
  | expression '(' arguments ')'
  | '!' expression
  | expression '&' expression
  | expression ( '*' | '/' ) expression
  | expression ( '+' | '-' ) expression
  | expression ( '==' | '!=' ) expression
  | '(' expression ')'
  ;
intLiteral : Integer ;

structureLiteral: '@{' ( ( field ',' )* field ','? )? '}' ;
field: labelReference '=' expression ;

// comments

// if x {
//    return 4;
// } else {
// }

stringLiteral: String ;
String: '"' StringCharacters? '"' ;
fragment StringCharacters: StringCharacter+ ;
fragment StringCharacter: ~["\\] | EscapeSequence ;
fragment EscapeSequence: '\\' [btnfr"'\\] ;

Integer: [0-9]+ ;
fragment Upper: [A-Z] ;
fragment Lower: [a-z] ;
fragment IdentifierChar: [a-zA-Z0-9_] ;

WS: [ \t\r\n\u000C]+ -> skip ;
