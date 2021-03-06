package com.yukihitoho.sclspsamples.repl

import com.yukihitoho.sclsp.implementations.DefaultInterpreter
import com.yukihitoho.sclsp.interpreter.Interpreter
import org.slf4j.{Logger, LoggerFactory}

object ReplMain extends App {
  new Repl {
    override val logger: Logger = LoggerFactory.getLogger(ReplMain.getClass)
    override protected val interpreter: Interpreter = DefaultInterpreter
    override protected val console: Console = new Console
  }.repl()
}
