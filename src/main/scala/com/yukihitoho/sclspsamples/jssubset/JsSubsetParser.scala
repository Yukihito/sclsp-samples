package com.yukihitoho.sclspsamples.jssubset

import com.yukihitoho.sclsp.ast.Position
import com.yukihitoho.sclsp.parsing
import com.yukihitoho.sclsp.parsing.ParsingError.InvalidSyntax
import com.yukihitoho.sclsp.parsing._

// scalastyle:off
trait JsSubsetParser extends parsing.Parser {
  private def kw(pattern: String) = positioned(pattern ^^ Symbol)
  private def kw(pattern: String, internalSymbol: String) = positioned(pattern ^^ (_ => Symbol(internalSymbol)))

  private def parameters: Parser[Node] = positioned("(" ~> repsep(symbol, ",") <~ ")" ^^ NodeList)
  private def arguments: Parser[Node]  = positioned("(" ~> repsep(expr, ",") <~ ")" ^^ NodeList)
  private def condition: Parser[Node]  = positioned("(" ~> expr <~ ")")
  private def block: Parser[Node]      = positioned("{" ~> statements <~ "}")

  private def function: Parser[Node] = positioned(kw("function", "lambda") ~ parameters ~ block ^^ {
    case kw ~ parameters ~ block => NodeList(List(kw, parameters, block))
  })
  private def call: Parser[Node] = positioned((function | symbol) ~ arguments ^^ {
    case symbol ~ arguments => NodePair(symbol, arguments)
  })

  private def ifStatement: Parser[Node] = positioned(kw("if") ~ condition ~ block ~ opt(elseStatement) ^^ {
    case kw ~ condition ~ block ~ Some(elseStatement) => NodeList(List(kw, condition, block, elseStatement))
    case kw ~ condition ~ block ~ None => NodeList(List(kw, condition, block, NilLiteral))
  })
  private def elseStatement: Parser[Node] = positioned("else" ~ (ifStatement | block) ^^ {
    case _ ~ body => body
  })
  private def whileStatement: Parser[Node] = positioned(kw("while") ~ condition ~ block ^^ {
    case kw ~ condition ~ block => NodeList(List(kw, condition, block))
  })
  private def varStatement: Parser[Node] = positioned(kw("var", "define") ~ symbol ~ "=" ~ expr ^^ {
    case kw ~ symbol ~ _ ~ value => NodeList(List(kw, symbol, value))
  })
  private def assignmentStatement: Parser[Node] = positioned(symbol ~ kw("=", "set!") ~ expr ^^ {
    case symbol ~ kw ~ expr => NodeList(List(kw, symbol, expr))}
  )

  private def expr: Parser[Node] = expr4
  private def expr4: Parser[Node] = expr(List("||"), expr3)
  private def expr3: Parser[Node] = expr(List("&&"), expr2)
  private def expr2: Parser[Node] = expr(List(">", "<", ">=", "<=", "==", "!="), expr1)
  private def expr1: Parser[Node] = expr(List("+", "-"), expr0)
  private def expr0: Parser[Node] = expr(List("*", "/"), value)
  private def expr(patterns: List[String], arg: Parser[Node]): Parser[Node] = positioned(arg ~ operators(patterns, arg) ^^ {
    case head ~ tail => (head /: tail)((acc, f) => f(acc))
  })
  private def operators(patterns: List[String], arg: Parser[Node]): Parser[List[Node => Node]] = rep(patterns.map(pattern => operator(pattern, arg)) match {
    case head :: tail => tail.foldLeft(head)((acc, value) => acc | value)
    case _ => failure("An unknown error occurred when parsing operator.")
  })
  private def operator(opPattern: String, rhsPattern: Parser[Node]): Parser[Node => Node] = kw(opPattern) ~ rhsPattern ^^ {
    case kw ~ lhs => rhs => NodeList(List(kw, rhs, lhs))
  }
  //private def expr0: Parser[Node] = positioned(value ~ rep(op("*", value) | op("/", value)) ^^ {case head ~ tail => (head /: tail)((acc, f) => f(acc))})

  private def symbol: Parser[Node] = positioned(ident ^^ Symbol)

  private def value: Parser[Node] = positioned(
        stringLiteral ^^ (s => StringLiteral(s.slice(1, s.length - 1)))
      | floatingPointNumber ^^ (v => NumberLiteral(v.toDouble))
      | kw("!", "not") ~ value ^^ {case kw ~ value => NodePair(kw, value)}
      | "true" ^^ (_ => BooleanLiteral(true))
      | "false" ^^ (_ => BooleanLiteral(false))
      | "null" ^^ (_ => NilLiteral)
      | function
      | call
      | symbol
      | "(" ~> expr <~ ")"
  )

  private def statement: Parser[Node]  = positioned((ifStatement | whileStatement | varStatement | assignmentStatement | expr) <~ opt(";"))
  private def statements: Parser[Node] = positioned(rep(statement) ^^ {statements => NodeList(Symbol("begin") :: statements)})

  override def parseToNode(src: String, fileName: String): Either[InvalidSyntax, Node] =parseAll(statements, src) match {
    case Success(result, _) => Right(result)
    case noSuccess: NoSuccess =>
      val pos = noSuccess.next.pos
      Left(InvalidSyntax(noSuccess.msg, Position(pos.line, pos.column, fileName)))
  }
}
// scalastyle:on