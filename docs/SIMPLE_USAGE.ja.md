# Simple usage
このサンプルは、 [sclsp](https://github.com/Yukihito/sclsp) を scala のアプリケーションに組み込む簡単な実装例です。

サンプルコードは [ここ](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/simpleusage) にあります

# サンプルの実行
サンプルは次のようにして実行できます
```
$ sbt "runMain com.yukihitoho.sclspsamples.simpleusage.Main"
```
このサンプルは次のような結果を出力します
```
Right(NumberValue(120.0))
```

# 実装の詳細
コード全体は次のとおりです

[com.yukihitoho.sclspsamples.simpleusage.Main.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/simpleusage/Main.scala)
```$scala
package com.yukihitoho.sclspsamples.simpleusage

import com.yukihitoho.sclsp.implementations.DefaultInterpreter

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

```

DefaultInterpreter は、 sclsp のInterpreterのデフォルト実装です

Interpreter#interpret にソースコード(例では5の階乗を計算する例を指定しています)とファイル名を指定すると、Interpreterはソースコードを解釈した結果を返します。
結果の型はEitherで、解釈の成功はコード全体を評価した結果の値をRightに含めたものとして表現され、失敗はエラー情報をLeftに含めたものとして表現されます。

参考:
- 構文解析中に発生したエラーについての情報は次のクラスを通じて返却されます: [ParsingError](https://github.com/Yukihito/sclsp/blob/master/src/main/scala/com/yukihitoho/sclsp/parsing/ParsingError.scala)
- 式の評価中に発生したエラーについての情報は次のクラスを通じて返却されます: [EvaluationError](https://github.com/Yukihito/sclsp/blob/master/src/main/scala/com/yukihitoho/sclsp/evaluator/EvaluationError.scala)
