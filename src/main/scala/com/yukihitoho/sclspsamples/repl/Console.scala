package com.yukihitoho.sclspsamples.repl

import org.jline.reader.{EndOfFileException, LineReaderBuilder, UserInterruptException}

import scala.util.Try

class Console {
  import Console.ReadError._
  protected def prompt: String = "> "

  private val underlying = LineReaderBuilder.builder().build()

  def readLine(): Either[Console.ReadError, String] = {
    Try(underlying.readLine(prompt)).toEither.left.map {
      case _: EndOfFileException => EndOfFile
      case _: UserInterruptException => UserInterrupt
    }
  }
}

object Console {
  trait ReadError
  object ReadError {
    case object UserInterrupt extends ReadError
    case object EndOfFile extends ReadError
  }
}
