package com.yukihitoho.sclspsamples.sharedenv

import com.yukihitoho.sclsp.evaluator.EvaluationError
import com.yukihitoho.sclspsamples.repl
import org.slf4j.Logger

trait EvaluationErrorHandler extends repl.EvaluationErrorHandler {
  import EvaluationError._
  protected val logger: Logger
  override def handleEvaluationError(src: String, error: EvaluationError): Unit = error match {
    case UnboundVariable(symbol, stackTrace) =>
      logger.error(createErrorMessage("Unbound variable.", s"${symbol.value} ${positionToString(symbol.position)}"))
      createStackTraceMessage(stackTrace).split("\n").foreach(logger.error)
    case InvalidNumberOfArguments(expected, actual, variadic, stackTrace) =>
      val descriptionOfExpectedNumber = if (variadic) {
        s"$expected or more"
      } else {
        expected
      }
      logger.error(createErrorMessage("Invalid number of arguments.", s"Expected $descriptionOfExpectedNumber, but was $actual."))
      createStackTraceMessage(stackTrace).split("\n").foreach(logger.error)
    case InvalidProcedureCall(pair, invalidOperator, stackTrace) =>
      logger.error(createErrorMessage("Invalid procedure call.", s"${getExtRep(invalidOperator)} is not a callable value."
        + pair.position.map(" " + positionToString(_)).getOrElse("")))
      createStackTraceMessage(stackTrace).split("\n").foreach(logger.error)
    case InvalidArgumentType(expectedType, actualValue, stackTrace) =>
      logger.error(createErrorMessage("Invalid argument type.", s"${getExtRep(actualValue)} is not a $expectedType value."))
      createStackTraceMessage(stackTrace).split("\n").foreach(logger.error)
    case DivisionByZero(stackTrace) =>
      logger.error(createErrorMessage("Division by zero.", ""))
      createStackTraceMessage(stackTrace).split("\n").foreach(logger.error)
    case InvalidSyntax(pair, stackTrace) =>
      pair.position match {
        case Some(position) =>
          logger.error(createErrorMessage("Invalid syntax.", s"Syntax error in or near below code. ${positionToString(position)}"))
          createStackTraceMessage(stackTrace).split("\n").foreach(logger.error)
        case None =>
          logger.error(createErrorMessage("Invalid syntax.", ""))
          createStackTraceMessage(stackTrace).split("\n").foreach(logger.error)
      }

  }
}
