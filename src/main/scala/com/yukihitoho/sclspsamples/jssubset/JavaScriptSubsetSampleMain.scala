package com.yukihitoho.sclspsamples.jssubset

import com.yukihitoho.sclsp.evaluator.Module
import com.yukihitoho.sclsp.implementations.DefaultDependencies
import com.yukihitoho.sclsp.interpreter.Interpreter
import com.yukihitoho.sclsp.modules.prelude.Prelude
import com.yukihitoho.sclsp.parsing.Parser
import com.yukihitoho.sclspsamples.jssubset.modules.jssubset.JsSubset
import com.yukihitoho.sclspsamples.repl.ExternalRepresentationHelper

class JsSubsetInterpreter extends Interpreter with DefaultDependencies {
  protected override val parser: Parser = new JsSubsetParser {}
  protected override val modules: Seq[Module] = Seq(Prelude, JsSubset)
}

// scalastyle:off
object JavaScriptSubsetSampleMain extends App with ExternalRepresentationHelper {
  val interpreter = new JsSubsetInterpreter
  def trySample(src: String): Unit = {
    println("---- src ----")
    println(src)
    println("---- result ----")
    println(interpreter.interpret(src, "example.scm"))
    println()
  }

  trySample(
    """
      |var double = function(n) {
      |  n * 2;
      |}
      |var result = double(3);
      |result
    """.stripMargin) // Right(NumberValue(6.0))

  println("************")

  trySample(
    """
      |var two = 2;
      |1 + two * 3 - 4
    """.stripMargin) // Right(NumberValue(3.0))

  println("************")

  trySample(
    """
      |var result = "foo"
      |if (1 > 2) {
      |  result = "bar"
      |} else {
      |  result = "baz"
      |}
      |result
    """.stripMargin) // Right(StringValue(baz))

  println("************")

  trySample(
    """
      |var i = 0;
      |while (i < 5) {
      |  i = i + 1
      |}
      |i
    """.stripMargin) // Right(NumberValue(5.0))

    println("************")

  trySample(
    """
      |var factorial = function (n) {
      |  if (n > 1) {
      |    n * factorial (n - 1);
      |  } else {
      |    n;
      |  }
      |}
      |
      |factorial(5);
    """.stripMargin) // Right(NumberValue(120.0))

  println("************")

  trySample(
    """
      |var x = 1;
      |var l = [x, 2, 3];
      |l
    """.stripMargin) // Right(NumberValue(5.0))

  println("************")

  trySample(
    """
      |1 / 0;
    """.stripMargin) // Left(EvaluationError(DivisionByZero(...
}
// scalastyle:on
