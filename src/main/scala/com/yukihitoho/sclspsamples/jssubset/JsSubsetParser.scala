package com.yukihitoho.sclspsamples.jssubset

import com.yukihitoho.sclsp.ast.Position
import com.yukihitoho.sclsp.parsing
import com.yukihitoho.sclsp.parsing.ParsingError.InvalidSyntax
import com.yukihitoho.sclsp.parsing._
import scala.util.parsing.combinator._

trait JsSubsetParser extends parsing.Parser {
  class Parsers(fileName: String) extends JavaTokenParsers {
    private def function: Parser[Node] = positioned("function" ~> parameters <~ "{" ~> fields <~ "}")
    private def symbol: Parser[Node] = positioned(ident ^^ (v => Symbol(v, fileName)))
    private def parameters: Parser[NodeList] = positioned("(" ~> repsep(symbol, ",") <~ ")" ^^ (symbols => NodeList(symbols, fileName)))
    private def call: Parser[Node] = ???
    private def arguments: Parser[Node] = ???
    private def ifStatement: Parser[Node] = ???
    private def elseStatement: Parser[Node] = ???
    private def whileStatement: Parser[Node] = ???
    private def varStatement: Parser[Node] = ???
    private def value: Parser[Node] = positioned(
      stringLiteral ^^ (s => StringLiteral(s.slice(1, s.length - 1), fileName))
        | number
        | ident ^^ (v => Symbol(v, fileName))
        | "true" ^^ (_ => Symbol("#t", fileName))
        | "false" ^^ (_ => Symbol("#f", fileName))
        | "null" ^^ (_ => Symbol("#nil", fileName))
        | expr
    )
    private def expr: Parser[Node] = positioned(term ~ rep(plus | minus) ^^ {case head ~ tail => (head /: tail)((acc, f) => f(acc))})
    private def plus: Parser[Node => Node] = "+" ~ term ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("+", fileName), rhs, lhs), fileName)}
    private def minus: Parser[Node => Node] = "-" ~ term ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("-", fileName), rhs, lhs), fileName)}
    private def term: Parser[Node] = positioned(factor ~ rep(times | divide) ^^ {case head ~ tail => (head /: tail)((acc, f) => f(acc))})
    private def times: Parser[Node => Node] = "*" ~ factor ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("*", fileName), rhs, lhs), fileName)}
    private def divide: Parser[Node => Node] = "/" ~ factor ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("/", fileName), rhs, lhs), fileName)}
    private def factor: Parser[Node] = number | symbol | "(" ~> expr <~ ")"
    private def number: Parser[Node] = positioned(floatingPointNumber ^^ (v => NumberLiteral(v.toDouble, fileName)))
    private def fields: Parser[Node] = ???

    def parseToNode(src: String): Either[InvalidSyntax, Node] = parseAll(fields, src) match {
      case Success(result, _) => Right(result)
      case noSuccess: NoSuccess =>
        val pos = noSuccess.next.pos
        Left(InvalidSyntax(noSuccess.msg, Position(pos.line, pos.column, fileName)))
    }
  }

  override def parseToNode(src: String, fileName: String): Either[InvalidSyntax, Node] = new Parsers(fileName).parseToNode(src)
}
