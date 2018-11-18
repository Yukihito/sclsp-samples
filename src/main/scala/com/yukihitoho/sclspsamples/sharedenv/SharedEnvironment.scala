package com.yukihitoho.sclspsamples.sharedenv

import java.util.UUID

import com.yukihitoho.sclsp.ast.Position
import com.yukihitoho.sclsp.evaluator._
import com.yukihitoho.sclsp.implementations.DefaultVariablesRepository
import io.lettuce.core.api.StatefulRedisConnection
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

class RootSharedEnvironmentFactory(connection: StatefulRedisConnection[String, String]) extends EnvironmentFactory {
  override def create(base: Option[Environment]): Environment = {
    val builtinVariables = new DefaultVariablesRepository
    val rootEnvironment: Environment = new Environment {
      override protected val base: Option[Environment] = None
      protected val variables: VariablesRepository = builtinVariables
      protected override val environmentFactory: EnvironmentFactory =
        new SharedEnvironmentFactory(builtinVariables, connection, this)
    }
    rootEnvironment.extend(Seq())
  }
}

class SharedEnvironmentFactory(
  builtinVariables: VariablesRepository,
  connection: StatefulRedisConnection[String, String],
  val rootEnvironment: Environment
) extends EnvironmentFactory {
  override def create(base: Option[Environment]): Environment = {
    val newEnvironmentId = base match {
      case Some(_: SharedEnvironment) => UUID.randomUUID().toString
      case _ => "shared-root"
    }
    SharedEnvironment(builtinVariables, connection, newEnvironmentId, base, this)
  }

  def restore(environmentId: String, base: Environment): Environment = {
    SharedEnvironment(builtinVariables, connection, environmentId, Some(base), this)
  }

  def restore(environmentIds: List[String]): Environment =
    environmentIds.reverse.foldLeft[Environment](rootEnvironment){ (acc, environmentId) =>
      restore(environmentId, acc)
    }
}

case class SharedEnvironment(
  builtinVariables: VariablesRepository,
  connection: StatefulRedisConnection[String, String],
  environmentId: String,
  override val base: Option[Environment],
  override val environmentFactory: SharedEnvironmentFactory
) extends Environment {
  def environmentIds: List[String] = base match {
    case Some(base: SharedEnvironment) => environmentId :: base.environmentIds
    case _ => List(environmentId)
  }

  override val variables: VariablesRepository = new SharedVariablesRepository(connection, environmentId, builtinVariables, environmentFactory)

  override def store(modules: Seq[Module]): Environment = {
    environmentFactory.rootEnvironment.store(modules)
    this
  }
}

class SharedVariablesRepository(
  connection: StatefulRedisConnection[String, String],
  environmentId: String,
  builtinVariables: VariablesRepository,
  environmentFactory: SharedEnvironmentFactory
) extends VariablesRepository {
  override def find(name: String): Option[Variable] = {
    val sync = connection.sync()
    Option(sync.get(createKey(name)))
      .map(str => UserDefinedVariable(SymbolValue(name, Position(1, 1, "")), deserialize(str)))
  }

  override def store(variable: Variable): Unit = {
    val sync = connection.sync()
    sync.set(createKey(variable.name), serialize(variable.value))
  }

  private def createKey(name: String): String = s"$environmentId:$name"

  private def serialize(value: Value): String =
    toData(value).asJson.noSpaces

  private def deserialize(str: String): Value =
    parser.decode[Data](str).toOption.map(toValue).getOrElse(NilValue)

  // scalastyle:off
  private def toData(value: Value): Data = value match {
    case NilValue => NilData()
    case StringValue(v) => StringData(v)
    case NumberValue(v) => NumberData(v)
    case BooleanValue(v) => BooleanData(v)
    case SymbolValue(v, p) => SymbolData(v)
    case p: Builtin => BuiltinOperatorData(p.builtinSymbol)
    case CompoundProcedureValue(param, body, env, pos) => env match {
      case e: SharedEnvironment => CompoundProcedureData(param.map(v => SymbolData(v.value)), toData(body), e.environmentIds, pos)
      case _ => NilData()
    }
    case PairValue(car, cdr, _) => PairData(toData(car), toData(cdr))
    case _ => NilData()
  }
  // scalastyle:on

  private def toValue(data: Data): Value = data match {
    case NilData() => NilValue
    case StringData(v) => StringValue(v)
    case NumberData(v) => NumberValue(v)
    case BooleanData(v) => BooleanValue(v)
    case SymbolData(v) => SymbolValue(v, Position(1, 1, ""))
    case BuiltinOperatorData(builtinSymbol) => builtinVariables.find(builtinSymbol).map(_.value).getOrElse(NilValue)
    case CompoundProcedureData(param, body, environmentIds, pos) =>
      CompoundProcedureValue(param.map(v => SymbolValue(v.value, Position(1, 1, ""))), toValue(body), environmentFactory.restore(environmentIds), pos)
    case PairData(car, cdr) => PairValue(toValue(car), toValue(cdr), None)
  }
}

sealed trait Data

case class NilData() extends Data
case class StringData(value: String) extends Data
case class NumberData(value: Double) extends Data
case class BooleanData(value: Boolean) extends Data
case class SymbolData(value: String) extends Data
case class BuiltinOperatorData(builtinSymbol: String) extends Data
case class CompoundProcedureData(
  parameters: Seq[SymbolData],
  body: Data,
  environmentIds: List[String],
  position: Position
) extends Data
case class PairData(car: Data, cdr: Data) extends Data