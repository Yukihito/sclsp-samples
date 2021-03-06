package com.yukihitoho.sclspsamples.simpleusage

import com.yukihitoho.sclsp.implementations.DefaultInterpreter

// scalastyle:off
object Main extends App {
  val src: String =
    """
      |(begin
      |  (define factorial
      |    (lambda (n)
      |      (if (eq? n 1)
      |        1
      |        (* n (factorial (- n 1))))))
      |  (factorial 5))
    """.stripMargin
  println(DefaultInterpreter.interpret(src, "a-file-name-for-this-sample.scm")) // Right(NumberValue(120.0))
}
// scalastyle:on
