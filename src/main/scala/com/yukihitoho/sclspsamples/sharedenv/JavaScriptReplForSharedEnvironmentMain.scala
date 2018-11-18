package com.yukihitoho.sclspsamples.sharedenv

import com.yukihitoho.sclsp.evaluator.{EnvironmentFactory, Module}
import com.yukihitoho.sclsp.implementations.DefaultDependencies
import com.yukihitoho.sclspsamples.repl.{Console, Repl}
import com.yukihitoho.sclsp.interpreter.Interpreter
import com.yukihitoho.sclsp.modules.prelude.Prelude
import com.yukihitoho.sclsp.parsing.Parser
import com.yukihitoho.sclspsamples.jssubset.JsSubsetParser
import com.yukihitoho.sclspsamples.jssubset.modules.jssubset.JsSubset
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.RedisClient
import org.slf4j.{Logger, LoggerFactory}

class JavaScriptSharedEnvironmentInterpreter(
  connection: StatefulRedisConnection[String, String]
) extends Interpreter with DefaultDependencies {
  protected override val environmentFactory: EnvironmentFactory = new RootSharedEnvironmentFactory(connection)
  protected override val parser: Parser = new JsSubsetParser {}
  protected override val modules: Seq[Module] = Seq(Prelude, JsSubset)
}

object JavaScriptReplForSharedEnvironmentMain extends App {
  val url = if (args.isDefinedAt(0)) {
    args(0)
  } else {
    "redis://localhost:6379"
  }
  val client = RedisClient.create(url)
  val connection = client.connect()
  new Repl with EvaluationErrorHandler {
    override val logger: Logger = LoggerFactory.getLogger(JavaScriptReplForSharedEnvironmentMain.getClass)
    override protected val interpreter: Interpreter = new JavaScriptSharedEnvironmentInterpreter(connection)
    override protected val console: Console = new Console
  }.repl()
}
