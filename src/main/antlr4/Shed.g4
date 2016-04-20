grammar Shed;

module: moduleDecl importStmt* moduleStatement* ;

moduleStatement: variableDecl | functionDecl | shapeDecl | labelDecl ;
functionStatement: variableDecl | expression ';' | 'return' expression ';' ;

moduleDecl: 'module' moduleIdentifier ';' ;

importStmt: 'import' ( moduleIdentifier | qualifiedTypeReference ) ';' ;

// comments

qualifiedTypeReference: ( moduleIdentifier '.' )? TypeIdentifier ;
TypeIdentifier: Upper IdentifierChar* ;

typeDef
  : '(' typeDef ')'
  | typeReference
  | structuralType
  | typeDef '&' typeDef
  | <assoc=right> typeDef '->' typeDef
  ;
typeReference: TypeIdentifier ;
structuralType: '{' ( LabelIdentifier ',' )* LabelIdentifier ','?  '}' ;

labelDecl: 'label' LabelIdentifier ':' typeDef ';' ;
LabelIdentifier: Backtick Lower IdentifierChar* ;

shapeDecl: 'shape' TypeIdentifier '=' typeDef ';' ;

variableDecl: 'val' Identifier ( ':' typeDef )? '=' expression ';' ;
Identifier: Lower IdentifierChar* ;
moduleIdentifier: Identifier ( '.' Identifier )* ;

functionDecl: 'fun' Identifier '(' parameters ')' '{' functionStatement* '}' ;
parameters: ( ( parameter ',' )* parameter )? ;
parameter: Identifier ':' typeDef ;

arguments: ( ( expression ',' )* expression )? ;

expression
  : intLiteral
  | stringLiteral
  | structureLiteral
  | expression '.' Identifier
  | expression '(' arguments ')'
  | expression '&' expression
  | expression ( '+' | '-' ) expression
  | Identifier
  ;
intLiteral : Integer ;

structureLiteral: '@{' ( ( field ',' )* field ','? )? '}' ;
field: LabelIdentifier '=' expression ;

stringLiteral: String ;
String: '"' StringCharacters? '"' ;
fragment StringCharacters: StringCharacter+ ;
fragment StringCharacter: ~["\\] | EscapeSequence ;
fragment EscapeSequence: '\\' [btnfr"'\\] ;

Integer: [0-9]+ ;
fragment Backtick: '`' ;
fragment Upper: [A-Z] ;
fragment Lower: [a-z] ;
fragment IdentifierChar: [a-zA-Z0-9_] ;

WS: [ \t\r\n\u000C]+ -> skip ;
