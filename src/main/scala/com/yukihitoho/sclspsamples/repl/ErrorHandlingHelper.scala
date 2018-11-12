package com.yukihitoho.sclspsamples.repl

import com.yukihitoho.sclsp.ast.Position
import com.yukihitoho.sclsp.evaluator.{Call, PairValue, SymbolValue}

class ErrorHandlingHelper {
  protected def createUnderlinedText(text: String, position: Position, underlineLength: Int): String = {
    val lines = text.split('\n')
    if (lines.length >= position.line) {
      lines(position.line - 1) + '\n' + (" " * (position.column - 1)) + ("^" * underlineLength)
    } else {
      ""
    }
  }

  protected def createErrorMessage(name: String, detail: String): String = s"$name: $detail"

  protected def createStackTraceMessage(stackTrace: List[Call]): String =
    "Stack trace\n" +
    stackTrace.map("    " + callToString(_)).mkString("\n")

  protected def callToString(call: Call): String = call match {
    case Call(SymbolValue(value, position), _) =>
      s"$value ${positionToString(position)}"
    case Call(PairValue(_, _, position), _) =>
      s"anonymous function${position.map(positionToString).getOrElse("")}"
    case _ => ""
  }

  protected def positionToString(position: Position): String = s"(${position.fileName}:${position.line}:${position.column})"
}
