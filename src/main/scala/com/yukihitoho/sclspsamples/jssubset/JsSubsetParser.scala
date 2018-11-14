package com.yukihitoho.sclspsamples.jssubset

import com.yukihitoho.sclsp.ast.Position
import com.yukihitoho.sclsp.parsing
import com.yukihitoho.sclsp.parsing.ParsingError.InvalidSyntax
import com.yukihitoho.sclsp.parsing._

trait JsSubsetParser extends parsing.Parser {
  private def function: Parser[Node] = positioned("function" ~> parameters ~ block ^^ {
    case parameters ~ block => NodeList(List(Symbol("lambda"), parameters, block))})
  private def symbol: Parser[Node] = positioned(ident ^^ Symbol)
  private def parameters: Parser[Node] = positioned("(" ~> repsep(symbol, ",") <~ ")" ^^ NodeList)
  private def call: Parser[Node] = positioned((function | symbol) ~ arguments ^^ {case symbol ~ arguments => NodePair(symbol, arguments)})
  private def arguments: Parser[Node] = positioned("(" ~> repsep(expr, ",") <~ ")" ^^ NodeList)
  private def condition: Parser[Node] = positioned("(" ~> expr <~ ")")
  private def ifStatement: Parser[Node] = positioned("if" ~> condition ~ block ~ opt(elseStatement) ^^ {
    case condition ~ block ~ Some(elseStatement) => NodeList(List(Symbol("if"), condition, block, elseStatement))
    case condition ~ block ~ None => NodeList(List(Symbol("if"), condition, block, NilLiteral))
  })
  private def elseStatement: Parser[Node] = positioned(ifStatement | block)
  private def whileStatement: Parser[Node] = positioned("while" ~> condition ~ block ^^ {
    case condition ~ block => NodeList(List(Symbol("while"), condition, block))
  })
  private def varStatement: Parser[Node] = positioned("var" ~> symbol ~ "=" ~ expr ^^ {
    case symbol ~ "=" ~ value => NodeList(List(Symbol("define"), symbol, value))
  })
  private def factor: Parser[Node] = positioned(
    stringLiteral ^^ (s => StringLiteral(s.slice(1, s.length - 1)))
      | number
      | "true" ^^ (_ => BooleanLiteral(true))
      | "false" ^^ (_ => BooleanLiteral(false))
      | "null" ^^ (_ => NilLiteral)
      | function
      | symbol
      | "(" ~> expr <~ ")"
  )
  private def expr: Parser[Node] = positioned(term ~ rep(plus | minus) ^^ {case head ~ tail => (head /: tail)((acc, f) => f(acc))})
  private def plus: Parser[Node => Node] = "+" ~ term ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("+"), rhs, lhs))}
  private def minus: Parser[Node => Node] = "-" ~ term ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("-"), rhs, lhs))}
  private def term: Parser[Node] = positioned(factor ~ rep(times | divide) ^^ {case head ~ tail => (head /: tail)((acc, f) => f(acc))})
  private def times: Parser[Node => Node] = "*" ~ factor ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("*"), rhs, lhs))}
  private def divide: Parser[Node => Node] = "/" ~ factor ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("/"), rhs, lhs))}
  private def number: Parser[Node] = positioned(floatingPointNumber ^^ (v => NumberLiteral(v.toDouble)))
  private def statement: Parser[Node] = positioned(ifStatement | whileStatement | varStatement | call | function | expr)
  private def statements: Parser[Node] = positioned(rep(statement) ^^ {statements => NodeList(Symbol("begin") :: statements)})
  private def block: Parser[Node] = positioned("{" ~> statements <~ "}")

  override def parseToNode(src: String, fileName: String): Either[InvalidSyntax, Node] =parseAll(statements, src) match {
    case Success(result, _) => Right(result)
    case noSuccess: NoSuccess =>
      val pos = noSuccess.next.pos
      Left(InvalidSyntax(noSuccess.msg, Position(pos.line, pos.column, fileName)))
  }
}
