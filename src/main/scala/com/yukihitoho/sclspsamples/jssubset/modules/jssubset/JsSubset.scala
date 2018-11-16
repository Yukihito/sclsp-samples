package com.yukihitoho.sclspsamples.jssubset.modules.jssubset

import com.yukihitoho.sclsp.evaluator.Module
import com.yukihitoho.sclsp.evaluator.SynonymVariable
import com.yukihitoho.sclsp.evaluator.VariableFactory
import com.yukihitoho.sclsp.modules.prelude.And
import com.yukihitoho.sclsp.modules.prelude.Define
import com.yukihitoho.sclsp.modules.prelude.Lambda
import com.yukihitoho.sclsp.modules.prelude.Not
import com.yukihitoho.sclsp.modules.prelude.Or
import com.yukihitoho.sclsp.modules.prelude.`Set!`
import com.yukihitoho.sclsp.modules.prelude.`Eq?`

object JsSubset extends Module {
  private def synonym(synonym: String, baseFactory: VariableFactory): VariableFactory =
    () => SynonymVariable(synonym, baseFactory.create)

  override def variableFactories: Seq[VariableFactory] = Seq(
    synonym("function", Lambda),
    synonym("var", Define),
    synonym("=", `Set!`),
    synonym("!", Not),
    synonym("||", Or),
    synonym("&&", And)
  )
}
