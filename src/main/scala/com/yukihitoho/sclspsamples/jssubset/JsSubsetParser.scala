package com.yukihitoho.sclspsamples.jssubset

import com.yukihitoho.sclsp.ast.Position
import com.yukihitoho.sclsp.parsing
import com.yukihitoho.sclsp.parsing.ParsingError.InvalidSyntax
import com.yukihitoho.sclsp.parsing._
import scala.util.parsing.combinator._

trait JsSubsetParser extends parsing.Parser {
  class Parsers(fileName: String) extends JavaTokenParsers {
    private def function: Parser[Node] = positioned("function" ~> parameters ~ block ^^ {
      case parameters ~ block => NodeList(List(Symbol("lambda", fileName), parameters, block), fileName)})
    private def symbol: Parser[Node] = positioned(ident ^^ (v => Symbol(v, fileName)))
    private def parameters: Parser[Node] = positioned("(" ~> repsep(symbol, ",") <~ ")" ^^ (symbols => NodeList(symbols, fileName)))
    private def call: Parser[Node] = positioned((function | symbol) ~ arguments ^^ {case symbol ~ arguments => NodePair(symbol, arguments, fileName)})
    private def arguments: Parser[Node] = positioned("(" ~> repsep(expr, ",") <~ ")" ^^ (values => NodeList(values, fileName)))
    private def condition: Parser[Node] = positioned("(" ~> expr <~ ")")
    private def ifStatement: Parser[Node] = positioned("if" ~> condition ~ block ~ opt(elseStatement) ^^ {
      case condition ~ block ~ Some(elseStatement) => NodeList(List(Symbol("if", fileName), condition, block, elseStatement), fileName)
      case condition ~ block ~ None => NodeList(List(Symbol("if", fileName), condition, block, Symbol("#nil", fileName)), fileName)
    })
    private def elseStatement: Parser[Node] = positioned(ifStatement | block)
    private def whileStatement: Parser[Node] = positioned("while" ~> condition ~ block ^^ {
      case condition ~ block => NodeList(List(Symbol("while", fileName), condition, block), fileName)
    })
    private def varStatement: Parser[Node] = positioned("var" ~> symbol ~ "=" ~ expr ^^ {
      case symbol ~ "=" ~ value => NodeList(List(Symbol("define", fileName), symbol, value), fileName)
    })
    private def factor: Parser[Node] = positioned(
          stringLiteral ^^ (s => StringLiteral(s.slice(1, s.length - 1), fileName))
        | number
        | "true" ^^ (_ => Symbol("#t", fileName))
        | "false" ^^ (_ => Symbol("#f", fileName))
        | "null" ^^ (_ => Symbol("#nil", fileName))
        | function
        | ident ^^ (v => Symbol(v, fileName))
        | "(" ~> expr <~ ")"
    )
    private def expr: Parser[Node] = positioned(term ~ rep(plus | minus) ^^ {case head ~ tail => (head /: tail)((acc, f) => f(acc))})
    private def plus: Parser[Node => Node] = "+" ~ term ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("+", fileName), rhs, lhs), fileName)}
    private def minus: Parser[Node => Node] = "-" ~ term ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("-", fileName), rhs, lhs), fileName)}
    private def term: Parser[Node] = positioned(factor ~ rep(times | divide) ^^ {case head ~ tail => (head /: tail)((acc, f) => f(acc))})
    private def times: Parser[Node => Node] = "*" ~ factor ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("*", fileName), rhs, lhs), fileName)}
    private def divide: Parser[Node => Node] = "/" ~ factor ^^ {case _ ~ lhs => rhs => NodeList(List(Symbol("/", fileName), rhs, lhs), fileName)}
    private def number: Parser[Node] = positioned(floatingPointNumber ^^ (v => NumberLiteral(v.toDouble, fileName)))
    private def statement: Parser[Node] = positioned(ifStatement | whileStatement | varStatement | call | function | expr)
    private def statements: Parser[Node] = positioned(rep(statement) ^^ {statements => NodeList(Symbol("begin", fileName) :: statements, fileName)})
    private def block: Parser[Node] = positioned("{" ~> statements <~ "}")

    def parseToNode(src: String): Either[InvalidSyntax, Node] = parseAll(statements, src) match {
      case Success(result, _) => Right(result)
      case noSuccess: NoSuccess =>
        val pos = noSuccess.next.pos
        Left(InvalidSyntax(noSuccess.msg, Position(pos.line, pos.column, fileName)))
    }
  }

  override def parseToNode(src: String, fileName: String): Either[InvalidSyntax, Node] = new Parsers(fileName).parseToNode(src)
}
