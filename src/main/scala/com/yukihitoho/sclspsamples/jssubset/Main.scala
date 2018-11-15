package com.yukihitoho.sclspsamples.jssubset

import com.yukihitoho.sclsp.evaluator.EnvironmentFactory
import com.yukihitoho.sclsp.evaluator.Evaluator
import com.yukihitoho.sclsp.evaluator.Module
import com.yukihitoho.sclsp.evaluator.StackTrace
import com.yukihitoho.sclsp.implementations.DefaultDependencies
import com.yukihitoho.sclsp.implementations.DefaultEnvironmentFactory
import com.yukihitoho.sclsp.implementations.DefaultStackTrace
import com.yukihitoho.sclsp.interpreter.Interpreter
import com.yukihitoho.sclsp.modules.prelude.Prelude
import com.yukihitoho.sclsp.parsing.Parser
import com.yukihitoho.sclspsamples.jssubset.modules.jssubset.JsSubset

class JsSubsetInterpreter extends Interpreter {
  protected override val parser: Parser = new JsSubsetParser {}
  protected override val modules: Seq[Module] = Seq(Prelude, JsSubset)
  protected override val evaluator: Evaluator = new Evaluator {
    override val stackTrace: StackTrace = new DefaultStackTrace
  }
  protected override val environmentFactory: EnvironmentFactory = DefaultEnvironmentFactory
}

// scalastyle:off
object Main extends App {
  val interpreter = new JsSubsetInterpreter
  println(interpreter.interpret(
    """
      |var double = function(n) {
      |  n * 2;
      |}
      |
      |var two = 2;
      |
      |var result = double(1 + two * 3 - 4);
      |if (false) {
      |  print("hoge")
      |} else if (false) {
      |  print("fuga")
      |  3
      |} else {
      |  4
      |}
    """.stripMargin, "<console>"))
}
// scalastyle:on
