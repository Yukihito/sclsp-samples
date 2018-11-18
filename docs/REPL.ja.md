# REPL
このサンプルは、sclsp の REPL の実装例です

サンプルコードは [ここ](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/repl) にあります

このサンプルに含まれる内容は次のとおりです
- エラーハンドリング
- 結果として返されるオブジェクトを、S式の外部表現の文字列に変換する方法

# サンプルの実行
サンプルは次のようにして実行できます
```
$ sbt "runMain com.yukihitoho.sclspsamples.repl.ReplMain"
```

# 実装の詳細
## 動作の概要
REPLの動作の概要は次のとおりです
1. プロンプトを表示し、ユーザからの入力を待つ
2. ユーザから入力された文字を順番に文字列のバッファに追加する。これを改行の入力があるまで繰り返す
3. 改行が入力されたら、Interpreterでバッファの文字列の解釈を行う。ここで ParseError となった場合は、まだユーザから続きの入力があることが期待されるのでバッファをフラッシュせず 2 に戻る。そうでなければ 4 に進む
4. 入力を解釈した結果の値が得られた場合、それをS式の外部表現の文字列に変換して表示する。エラーとなった場合、エラーの種類、エラーが発生したコード上の位置、スタックトレースなどを表示する。
5. 1に戻る

1~5 の間に、ユーザからの Ctrl-C などの入力によって割り込みが入った場合、プログラムを終了する

サンプルにおける、このフローに対応する実装は [Repl.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/repl/Repl.scala) にあります

## エラー処理
REPLは、インタプリタからエラーが返却された場合に、そのエラーを人間が読める文字列として表示する必要があります。

返却される可能性のあるエラーは次のとおりです: [EvaluationError.scala](https://github.com/Yukihito/sclsp/blob/master/src/main/scala/com/yukihitoho/sclsp/evaluator/EvaluationError.scala) 

エラーオブジェクトには、エラー内容、エラーが起きたコード上の位置、スタックトレースなどの情報が含まれています。

サンプルでのエラーハンドリングの実装は [ErrorHandlingHelper.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/repl/ErrorHandlingHelper.scala) 及び [EvaluationErrorHandler.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/repl/EvaluationErrorHandler.scala) にあります

## 結果の値の外部表現への変換
インタプリタが入力の解釈に成功した場合、全体の解釈の結果としての値は [Value](https://github.com/Yukihito/sclsp/blob/master/src/main/scala/com/yukihitoho/sclsp/evaluator/Value.scala) として返却されます。これを人間が読める文字列に変換する必要があります。


サンプルにおいて、この変換を行っている実装は [ExternalRepresentationHelper.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/repl/ExternalRepresentationHelper.scala) にあります
