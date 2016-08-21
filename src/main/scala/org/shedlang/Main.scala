package org.shedlang

import org.shedlang.antlr._
import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import scala.collection.JavaConverters._
import scala.io.Source

object Main {

  def main(args: Array[String]): Unit = {
    println("Hello World")
  }

  def parseFib() = {
    val source = Source.fromFile("examples/fib.shed").getLines.mkString("\n")
    parse(source)
  }

  def parse(source: String) = {
    val stream = new ANTLRInputStream(source)
    val lexer = new ShedLexer(stream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new ShedParser(tokens)
    val tree = parser.module()
    val ruleNames = parser.getRuleNames()
    val visitor = new MyVisitor()

    tree.accept(visitor)
    //printTree(ruleNames, tree)
    // tree.toStringTree(parser)

    //(tree, visitor)
  }

  def parser(source: String) = {
    val stream = new ANTLRInputStream(source)
    val lexer = new ShedLexer(stream)
    val tokens = new CommonTokenStream(lexer)
    new ShedParser(tokens)
  }

  def printTree(ruleNames: Array[String], tree: Tree): Unit = {
    tree match {
      case context: RuleContext =>
        print("(")
        print(ruleNames(context.getRuleIndex))
        print(" ")
        for (
          child <- (0 to tree.getChildCount).map(tree.getChild)
        ) {
          printTree(ruleNames, child)
        }
        print(")")
      case terminal: TerminalNode =>
        Option(terminal.getSymbol).map(s => print(s.getText))
      case _ =>
    }
  }
}

sealed trait Node

case class ModuleIdentifier(name: String) extends Node

case class ImportStatement() extends Node

sealed trait ModuleStatement extends Node

case class Module(identifier: ModuleIdentifier, imports: Seq[ImportStatement], statements: Seq[ModuleStatement]) extends Node

case class LabelDeclaration(identifier: LabelIdentifier, typ: TypeDef) extends ModuleStatement

case class LabelIdentifier(name: String) extends Node

sealed trait Expression extends Node

case class IntegerLiteral(i: Int) extends Expression

trait BooleanLiteral extends Expression
object BooleanLiteral {
  case object True extends BooleanLiteral
  case object False extends BooleanLiteral
  def fromString(s: String): BooleanLiteral = s match {
    case "true" => True
    case "false"=> False
  }
}

sealed trait UnaryOperator

object UnaryOperator {
  case object Negate extends UnaryOperator
  case object Not extends UnaryOperator

  def fromString(s: String): UnaryOperator = s match {
    case "-" => Negate
    case "not" => Not
  }
}

case class UnaryOperation(operator: UnaryOperator, operand: Expression) extends Expression

sealed trait BinaryOperator

object BinaryOperator {

  case object Plus extends BinaryOperator
  case object Minus extends BinaryOperator
  case object Multiply extends BinaryOperator
  case object Divide extends BinaryOperator
  case object And extends BinaryOperator
  case object Or extends BinaryOperator
  case object Equals extends BinaryOperator

  def fromString(s: String): BinaryOperator = s match {
    case "+" => Plus
    case "-" => Minus
    case "*" => Multiply
    case "/" => Divide
    case "and" => And
    case "or" => Or
    case "==" => Equals
  }
}

trait TypeDef extends Node

case class QualifiedTypeReference(qualifier: String, name: String) extends TypeDef
case class TypeReference(name: String) extends TypeDef
case class JoinType(left: TypeDef, right: TypeDef) extends TypeDef
case class FunctionType(arg: TypeDef, returns: TypeDef) extends TypeDef

case class BinaryOperation(operator: BinaryOperator, left: Expression, right: Expression) extends Expression

case class StructuralType(fields: Seq[LabelReference]) extends TypeDef
case class LabelReference(name: String) extends Node

class MyVisitor extends ShedVisitor[Node] {

  def visit(x$1: ParseTree): Node = ???
  def visitChildren(x$1: RuleNode): Node = ???
  def visitErrorNode(x$1: ErrorNode): Node = ???
  def visitTerminal(x$1: TerminalNode): Node = ???

  def visitApplyExpression(context: ShedParser.ApplyExpressionContext): Node = ???
  def visitArguments(context: ShedParser.ArgumentsContext): Node = ???
  def visitBooleanAnd(op: ShedParser.BooleanAndContext): Node =
    visitBinaryOp(op)
  def visitBooleanLiteral(literal: ShedParser.BooleanLiteralContext): Node =
    BooleanLiteral.fromString(literal.getText())
  def visitBooleanNot(op: ShedParser.BooleanNotContext): Node =
    visitUnaryOp(op)
  def visitBooleanOr(op: ShedParser.BooleanOrContext): Node =
    visitBinaryOp(op)
  def visitEquality(op: ShedParser.EqualityContext): Node =
    visitBinaryOp(op)
  // TODO: def visitExpression(context: ShedParser.ExpressionContext): Expression =
  def visitField(context: ShedParser.FieldContext): Node = ???
  def visitFieldAccess(context: ShedParser.FieldAccessContext): Node = ???
  def visitFunctionDecl(context: ShedParser.FunctionDeclContext): Node = ???
  def visitFunctionStatement(context: ShedParser.FunctionStatementContext): Node = ???
  def visitFunctionType(context: ShedParser.FunctionTypeContext): Node =
    FunctionType(
      visitTypeDef(context.arg),
      visitTypeDef(context.returnType))
  def visitIdentifier(context: ShedParser.IdentifierContext): Node = ???
  def visitIfExpression(context: ShedParser.IfExpressionContext): Node = ???
  def visitImportStatement(context: ShedParser.ImportStatementContext): ImportStatement = ???
  def visitIntLiteral(literal: ShedParser.IntLiteralContext): Node =
    IntegerLiteral(literal.getText().toInt)
  def visitJoinExpression(context: ShedParser.JoinExpressionContext): Node = ???
  def visitJoinType(join: ShedParser.JoinTypeContext): Node =
    JoinType(
      visitTypeDef(join.left),
      visitTypeDef(join.right))
  def visitLabelDecl(context: ShedParser.LabelDeclContext): Node =
    LabelDeclaration(
      visitLabelIdentifier(context.id),
      visitTypeDef(context.typ))
  def visitLabelIdentifier(context: ShedParser.LabelIdentifierContext): LabelIdentifier =
    LabelIdentifier(context.getText())
  def visitLabelReference(context: ShedParser.LabelReferenceContext): LabelReference =
    LabelReference(context.getText())
  def visitModule(context: ShedParser.ModuleContext): Node =
    Module(
      visitModuleDecl(context.decl),
      context.children.asScala
        .collect { case c: ShedParser.ImportStatementContext => c }
        .map(visitImportStatement).toList,
      context.children.asScala
        .collect { case c: ShedParser.ModuleStatementContext => c }
        .map(visitModuleStatement).toList)
  def visitModuleDecl(context: ShedParser.ModuleDeclContext): ModuleIdentifier =
    visitModuleIdentifier(context.id)
  def visitModuleIdentifier(context: ShedParser.ModuleIdentifierContext): ModuleIdentifier =
    ModuleIdentifier(context.getText()) // TODO: perhaps just get the name without the bars?
  def visitModuleStatement(context: ShedParser.ModuleStatementContext): ModuleStatement =
    context.getChild(0).accept(this) match {
      case (statement: ModuleStatement) => statement
      case _ => throw new Exception("Should not occur.")
    }
  def visitNumericNegate(op: ShedParser.NumericNegateContext): Node =
    visitUnaryOp(op)
  def visitUnaryOp(op: ShedParser.ExpressionContext): Node = {
    val operator = UnaryOperator.fromString(op.getChild(0).getText())
    val operand = op.getChild(1).accept(this)
    operand match {
      case operand: Expression => UnaryOperation(operator, operand)
      case _ => throw new Exception("Should not occur.")
    }
  }
  def visitNumericOp1(op: ShedParser.NumericOp1Context): Node =
    visitBinaryOp(op)
  def visitNumericOp2(op: ShedParser.NumericOp2Context): Node =
    visitBinaryOp(op)
  def visitBinaryOp(op: ShedParser.ExpressionContext): Node = {
    val operator = BinaryOperator.fromString(op.getChild(1).getText())
    val left = op.getChild(0).accept(this)
    val right = op.getChild(2).accept(this)
    left -> right match {
      case (left: Expression, right: Expression) => BinaryOperation(operator, left, right)
      case _ => throw new Exception("Should not occur.")
    }
  }
  def visitParameter(context: ShedParser.ParameterContext): Node = ???
  def visitParameters(context: ShedParser.ParametersContext): Node = ???
  def visitParentheses(context: ShedParser.ParenthesesContext): Node = ???
  def visitParenthesizedType(context: ShedParser.ParenthesizedTypeContext): Node =
    context.inner.accept(this)
  def visitQualifiedReference(context: ShedParser.QualifiedReferenceContext): Node = ???
  def visitShapeDecl(context: ShedParser.ShapeDeclContext): Node = ???
  def visitStringLiteral(context: ShedParser.StringLiteralContext): Node = ???
  def visitStructuralType(context: ShedParser.StructuralTypeContext): Node = {
    val labels = context.children.asScala
      .collect { case c: ShedParser.LabelReferenceContext => c }
      .map(visitLabelReference).toList
    StructuralType(labels)
  }
  def visitStructureLiteral(context: ShedParser.StructureLiteralContext): Node = ???
  def visitTypeDef(context: ShedParser.TypeDefContext): TypeDef =
    context.accept(this) match {
      case typeDef: TypeDef => typeDef
      case _ => throw new Exception("Should not occur.")
    }
  def visitTypeReference(ref: ShedParser.TypeReferenceContext): Node = {
    Option(ref.qualifier) match {
      case Some(qualifier) => QualifiedTypeReference(qualifier.getText(), ref.name.getText())
      case None => TypeReference(ref.name.getText())
    }
  }
  def visitVariableDecl(context: ShedParser.VariableDeclContext): Node = ???
  def visitVariableReference(context: ShedParser.VariableReferenceContext): Node = ???
}
