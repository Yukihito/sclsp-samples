package com.yukihitoho.sclspsamples.repl

import com.yukihitoho.sclsp.evaluator.Value
import com.yukihitoho.sclsp.{evaluator, interpreter}
import com.yukihitoho.sclsp.interpreter.InterpretationError.{EvaluationError, ParsingError}
import com.yukihitoho.sclsp.interpreter.Interpreter
import org.slf4j.Logger
import scala.annotation.tailrec

trait Repl extends EvaluationErrorHandler {
  override val logger: Logger
  protected val interpreter: Interpreter
  protected val console: Console
  import ReplError._

  private def readAndInterpret(prevLines: String): Either[ReplError, Value] = {
    (for {
      nextLine <- console.readLine().left.map(ReadError)
      lines <- Right(
        if (prevLines == "") {
          nextLine
        } else {
          prevLines + "\n" + nextLine
        }
      )
      value <- interpreter.interpret(lines, "<console>").left.map(e => InterpretationError(e, lines))
    } yield value) match {
      case Left(InterpretationError(_: ParsingError, src: String)) =>
        readAndInterpret(src)
      case v => v
    }
  }

  @tailrec
  final def repl(): Unit = {
    readAndInterpret("") match {
      case Right(value) =>
        // scalastyle:off
        println(getExtRep(value))
        // scalastyle:on
        repl()
      case Left(ReadError(Console.ReadError.UserInterrupt)) =>
        logger.info("User interrupt.")
        repl()
      case Left(ReadError(Console.ReadError.EndOfFile)) =>
        logger.error("Unexpected end of file.")
        repl()
      case Left(InterpretationError(EvaluationError(evaluator.EvaluationError.Exit(code, _)), _)) =>
        System.exit(code)
      case Left(InterpretationError(EvaluationError(e), src)) =>
        handleEvaluationError(src, e)
        repl()
      case e =>
        logger.error(s"Unexpected interpretation result.: $e")
        System.exit(1)
    }
  }
}

trait ReplError

object ReplError {
  case class ReadError(error: Console.ReadError) extends ReplError
  case class InterpretationError(error: interpreter.InterpretationError, src: String) extends ReplError
}
