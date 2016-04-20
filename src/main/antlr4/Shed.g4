grammar Shed;

module: moduleDecl importStmt* moduleStatement* ;

moduleStatement: variableDecl | functionDecl | shapeDecl | labelDecl ;
functionStatement: variableDecl | expression ';' | 'return' expression ';' ;

moduleDecl: 'module' moduleIdentifier ';' ;

importStmt: 'import' ( moduleIdentifier | qualifiedTypeReference ) ';' ;

// comments

qualifiedTypeReference: ( moduleIdentifier '.' )? TypeIdentifier ;
TypeIdentifier: Upper IdentifierChar* ;

typeDef: '(' typeDef ')'
       | typeReference
       | structuralType
       | typeDef '&' typeDef
       | <assoc=right> typeDef '->' typeDef
       ;
typeReference: TypeIdentifier ;
structuralType: '{' ( LabelIdentifier ',' )* LabelIdentifier ','?  '}' ;

//functionType: structuralType '->' functionType | structuralType ;

labelDecl: 'label' LabelIdentifier ':' typeDef ';' ;
LabelIdentifier: Backtick Lower IdentifierChar* ;

shapeDecl: 'shape' TypeIdentifier '=' typeDef ';' ;

variableDecl: 'val' Identifier ( ':' typeDef )? '=' expression ';' ;
Identifier: Lower IdentifierChar* ;
moduleIdentifier: Identifier ( '.' Identifier )* ;

functionDecl: 'fun' Identifier '(' parameters ')' '{' functionStatement* '}' ;
parameters: ( ( parameter ',' )* parameter )? ;
parameter: Identifier ':' typeDef ;

expression: intLiteral | stringLiteral | structureLiteral ;
intLiteral: Integer ;

// field access
// fun invoke
// structure composition
// variable references

// structure declaration
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
