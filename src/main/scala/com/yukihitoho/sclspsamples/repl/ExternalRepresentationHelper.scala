package com.yukihitoho.sclspsamples.repl

import com.yukihitoho.sclsp.evaluator.BooleanValue
import com.yukihitoho.sclsp.evaluator.CompoundProcedureValue
import com.yukihitoho.sclsp.evaluator.NilValue
import com.yukihitoho.sclsp.evaluator.NumberValue
import com.yukihitoho.sclsp.evaluator.PairValue
import com.yukihitoho.sclsp.evaluator.PrimitiveProcedureValue
import com.yukihitoho.sclsp.evaluator.SpecialFormValue
import com.yukihitoho.sclsp.evaluator.StringValue
import com.yukihitoho.sclsp.evaluator.SymbolValue
import com.yukihitoho.sclsp.evaluator.Value

trait ExternalRepresentationHelper {
  // scalastyle:off
  protected def getExtRep(value: Value): String = value match {
    case StringValue(v) => "\"" + v + "\""
    case NumberValue(v) => v.toString
    case BooleanValue(v) => if (v) { "#t" } else { "#f" }
    case NilValue => "#nil"
    case SymbolValue(v, _) => v
    case _: CompoundProcedureValue => "#<closure>"
    case p: PrimitiveProcedureValue => s"#<closure ${p.builtinSymbol}>"
    case s: SpecialFormValue => s"#<syntax ${s.builtinSymbol}>"
    case pair: PairValue => pair.toList match {
      case Some(values) => "(" + values.map(getExtRep).mkString(" ") + ")"
      case None => s"(${getExtRep(pair.car)} . ${getExtRep(pair.cdr)})"
    }
    case _ => "#<unknown>"
  }
  // scalastyle:on
}
