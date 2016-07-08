grammar Shed;

module: moduleDecl importStmt* moduleStatement* ;

moduleStatement: variableDecl | functionDecl | shapeDecl | labelDecl ;
functionStatement
  : variableDecl
  | 'if' '(' expression ')' '{' functionStatement* '}' ( 'else' '{' functionStatement* '}' )?
  | expression ';'
  | 'return' expression ';' ;

moduleDecl: 'module' moduleIdentifier ';' ;

importStmt: 'import' ( moduleIdentifier | qualifiedReference ) ';' ;

qualifiedReference: moduleIdentifier identifier ;
identifier: TypeIdentifier | labelIdentifier | VariableIdentifier ;
TypeIdentifier: Upper IdentifierChar* ;

typeDef
  : '(' typeDef ')' // TODO: # <name>
  | typeReference
  | structuralType
  | typeDef '&' typeDef
  | <assoc=right> typeDef '->' typeDef
  ;
typeReference: moduleIdentifier? TypeIdentifier ;
structuralType: '{' ( labelReference ',' )* labelReference ','? '}' ;

labelReference : moduleIdentifier? labelIdentifier ;
labelDecl: 'label' labelIdentifier ':' typeDef ';' ;
labelIdentifier: '.' VariableIdentifier ;

shapeDecl: 'shape' TypeIdentifier '=' typeDef ';' ;

variableDecl: 'val' VariableIdentifier ( ':' typeDef )? '=' expression ';' ;
VariableIdentifier: Lower IdentifierChar* ;
moduleIdentifier: '|' VariableIdentifier ( '.' VariableIdentifier )* '|' ;

functionDecl: 'fun' VariableIdentifier '(' parameters ')' ':' typeDef '{' functionStatement* '}' ;
parameters: ( ( parameter ',' )* parameter )? ;
parameter: VariableIdentifier ':' typeDef ;

arguments: ( ( expression ',' )* expression )? ;

Comment: '//' ~[\n]* ( '\n' | EOF ) -> skip ;

expression
  : Integer # intLiteral
  | String # stringLiteral
  | ( 'true' | 'false' ) # booleanLiteral
  | '@{' ( ( field ',' )* field ','? )? '}' # structureLiteral
  | moduleIdentifier? VariableIdentifier # variableReference
  | expression labelReference # fieldAccess
  | expression '(' arguments ')' # applyExpression
  | 'not' expression # booleanNot
  | '-' expression # numericNegate
  | expression '&' expression # joinExpression
  | expression ( '*' | '/' ) expression # numericOp2
  | expression ( '+' | '-' ) expression # numericOp1
  | expression ( '==' | '!=' ) expression # equality
  | expression 'and' expression # booleanAnd
  | expression 'or' expression # booleanOr
  | 'if' '(' expression ')' expression ( 'else' expression )? # ifExpression
  | '(' expression ')' # parentheses
  ;

field: labelReference '=' expression ;

String: '"' StringCharacters? '"' ;
fragment StringCharacters: StringCharacter+ ;
fragment StringCharacter: ~["\\] | EscapeSequence ;
fragment EscapeSequence: '\\' [btnfr"'\\] ;

Integer: [0-9]+ ;
fragment Upper: [A-Z] ;
fragment Lower: [a-z] ;
fragment IdentifierChar: [a-zA-Z0-9_] ;

WS: [ \t\r\n\u000C]+ -> skip ;
