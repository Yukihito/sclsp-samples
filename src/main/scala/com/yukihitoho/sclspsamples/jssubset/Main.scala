package com.yukihitoho.sclspsamples.jssubset

import com.yukihitoho.sclsp.implementations.DefaultDependencies
import com.yukihitoho.sclsp.interpreter.Interpreter
import com.yukihitoho.sclsp.parsing.Parser

class JsSubsetInterpreter extends Interpreter with DefaultDependencies {
  protected override val parser: Parser = new JsSubsetParser {}
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
