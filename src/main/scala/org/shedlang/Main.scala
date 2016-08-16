package org.shedlang

import org.shedlang.antlr._
import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import scala.collection.JavaConverters._

object Main {

  def main(args: Array[String]): Unit = {
    println("Hello World")
  }

  def parse(source: String) = {
    val stream = new ANTLRInputStream(source)
    val lexer = new ShedLexer(stream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new ShedParser(tokens)
    val tree = parser.typeDef()
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

  def visitApplyExpression(x$1: ShedParser.ApplyExpressionContext): Node = ???
  def visitArguments(x$1: ShedParser.ArgumentsContext): Node = ???
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
  def visitField(x$1: ShedParser.FieldContext): Node = ???
  def visitFieldAccess(x$1: ShedParser.FieldAccessContext): Node = ???
  def visitFunctionDecl(x$1: ShedParser.FunctionDeclContext): Node = ???
  def visitFunctionStatement(x$1: ShedParser.FunctionStatementContext): Node = ???
  def visitFunctionType(context: ShedParser.FunctionTypeContext): Node =
    context.arg.accept(this) -> context.returnType.accept(this) match {
      case (arg: TypeDef, returns: TypeDef) => FunctionType(arg, returns)
      case _ => throw new Exception("Should not occur.")
    }
  def visitIdentifier(x$1: ShedParser.IdentifierContext): Node = ???
  def visitIfExpression(x$1: ShedParser.IfExpressionContext): Node = ???
  def visitImportStmt(x$1: ShedParser.ImportStmtContext): Node = ???
  def visitIntLiteral(literal: ShedParser.IntLiteralContext): Node =
    IntegerLiteral(literal.getText().toInt)
  def visitJoinExpression(x$1: ShedParser.JoinExpressionContext): Node = ???
  def visitJoinType(join: ShedParser.JoinTypeContext): Node =
    join.left.accept(this) -> join.right.accept(this) match {
      case (left: TypeDef, right: TypeDef) => JoinType(left, right)
      case _ => throw new Exception("Should not occur.")
    }
  def visitLabelDecl(x$1: ShedParser.LabelDeclContext): Node = ???
  def visitLabelIdentifier(x$1: ShedParser.LabelIdentifierContext): Node = ???
  def visitLabelReference(context: ShedParser.LabelReferenceContext): LabelReference =
    LabelReference(context.getText())
  def visitModule(x$1: ShedParser.ModuleContext): Node = ???
  def visitModuleDecl(x$1: ShedParser.ModuleDeclContext): Node = ???
  def visitModuleIdentifier(x$1: ShedParser.ModuleIdentifierContext): Node = ???
  def visitModuleStatement(x$1: ShedParser.ModuleStatementContext): Node = ???
  def visitNumericNegate(op: ShedParser.NumericNegateContext): Node =
    visitUnaryOp(op)
  def visitUnaryOp(op: ShedParser.ExpressionContext): Node = {
    val operator = UnaryOperator.fromString(op.getChild(0).getText())
    val operand = op.getChild(1).accept(this)
    operand match {
      case operand: Expression =>
        UnaryOperation(operator, operand)
      case _ =>
        throw new Exception("Should not occur.")
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
      case (left: Expression, right: Expression) =>
        BinaryOperation(operator, left, right)
      case _ =>
        throw new Exception("Should not occur.")
    }
  }
  def visitParameter(x$1: ShedParser.ParameterContext): Node = ???
  def visitParameters(x$1: ShedParser.ParametersContext): Node = ???
  def visitParentheses(x$1: ShedParser.ParenthesesContext): Node = ???
  def visitParenthesizedType(context: ShedParser.ParenthesizedTypeContext): Node =
    context.inner.accept(this)
  def visitQualifiedReference(x$1: ShedParser.QualifiedReferenceContext): Node = ???
  def visitShapeDecl(x$1: ShedParser.ShapeDeclContext): Node = ???
  def visitStringLiteral(x$1: ShedParser.StringLiteralContext): Node = ???
  def visitStructuralType(context: ShedParser.StructuralTypeContext): Node = {
    val labels = context.children.asScala
      .collect { case c: ShedParser.LabelReferenceContext => c }
      .map(visitLabelReference).toList
    StructuralType(labels)
  }
  def visitStructureLiteral(x$1: ShedParser.StructureLiteralContext): Node = ???
  // def visitTypeDef(x$1: ShedParser.TypeDefContext): Node = ???
  def visitTypeReference(ref: ShedParser.TypeReferenceContext): Node = {
    Option(ref.qualifier) match {
      case Some(qualifier) => QualifiedTypeReference(qualifier.getText(), ref.name.getText())
      case None => TypeReference(ref.name.getText())
    }
  }
  def visitVariableDecl(x$1: ShedParser.VariableDeclContext): Node = ???
  def visitVariableReference(x$1: ShedParser.VariableReferenceContext): Node = ???
}
