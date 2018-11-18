# Subset of JavaScript
このサンプルは、 sclsp のインタプリタに付属しているデフォルトのパーサの実装を、自前で作成した JavaScript のサブセットのパーサの実装に差し替えることによる、 JavaScript のサブセットのインタプリタの実装例です。

サンプルコードは [ここ](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/jssubset) にあります

このサンプルに含まれる内容は次のとおりです
- [scala-parser-combinators](https://github.com/scala/scala-parser-combinators) を使って、 sclsp で利用できる JavaScript のサブセットのパーサを作成する
- 自前で作成したパーサの実装の依存性をインタプリタに注入する
- ビルトインのオペレータのシノニムをインタプリタに追加する

# サンプルの実行
サンプルは次のようにして実行できます
```
$ sbt "runMain com.yukihitoho.sclspsamples.jssubset.JavaScriptSubsetSampleMain"
```
このサンプルは次のような結果を出力します
<details><summary>ここをクリックして展開</summary>

```
---- src ----

var double = function(n) {
  n * 2;
}
var result = double(3);
result
    
---- result ----
Right(NumberValue(6.0))

************
---- src ----

var two = 2;
1 + two * 3 - 4
    
---- result ----
Right(NumberValue(3.0))

************
---- src ----

var result = "foo"
if (1 > 2) {
  result = "bar"
} else {
  result = "baz"
}
result
    
---- result ----
Right(StringValue(baz))

************
---- src ----

var i = 0;
while (i < 5) {
  i = i + 1
}
i
    
---- result ----
Right(NumberValue(5.0))

************
---- src ----

var factorial = function (n) {
  if (n > 1) {
    n * factorial (n - 1);
  } else {
    n;
  }
}

factorial(5);
    
---- result ----
Right(NumberValue(120.0))

************
---- src ----

var x = 1;
var l = [x, 2, 3];
l
    
---- result ----
Right(PairValue(NumberValue(1.0),PairValue(NumberValue(2.0),PairValue(NumberValue(3.0),NilValue,None),None),None))

************
---- src ----

1 / 0;
    
---- result ----
Left(EvaluationError(DivisionByZero(List(Call(SymbolValue(/,Position(2,3,example.scm)),com.yukihitoho.sclsp.modules.prelude.Division$$anon$1@56ffc295), Call(SymbolValue(begin,Position(0,0,example.scm)),com.yukihitoho.sclsp.modules.prelude.Begin$$anon$1@2bd48569)))))

```

</details>

# 実装の詳細
## JavaScriptパーサの作成
まず、 com.yukihitoho.sclsp.parsing.Parser を継承して、 JavaScript の構文を sclsp の構文木に変換する Parser を作成します。 Parser の作成には [scala-parser-combinators](https://github.com/scala/scala-parser-combinators) を利用します。

サンプルに含まれる Parser の実装は次のものです: [JsSubsetParser.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/jssubset/JsSubsetParser.scala)

sclsp の構文木は [Node.scala](https://github.com/Yukihito/sclsp/blob/master/src/main/scala/com/yukihitoho/sclsp/parsing/Node.scala) のデータ構造で表されるS式です。パーサは、読みこんだ文字列を、このデータ構造をつかって Lisp が解釈できるようなS式に変換する必要があります。

## オペレーターの追加
次に、デフォルトの sclsp には存在しない JavaScript のオペレーターを追加します。sclsp では複数のオペレータをまとめたものを Module と呼び、 [Module.scala](https://github.com/Yukihito/sclsp/blob/master/src/main/scala/com/yukihitoho/sclsp/evaluator/Module.scala) を継承したクラスを作成することによって定義します。

このサンプルでは、 sclsp にデフォルトで定義されているオペレータを、対応する JavaScript のキーワードやオペレータ名に変換するようなシノニムが定義された Module を作成しています。 [JsSubset.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/jssubset/modules/jssubset/JsSubset.scala)

## インタプリタに作成した Parser, Moduleを追加する
  
次のような要領で、作成した Parser と Module の依存性が注入された Interpreter を定義して使います。

[JavaScriptSubsetSampleMain.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/jssubset/JavaScriptSubsetSampleMain.scala)

```scala
class JsSubsetInterpreter extends Interpreter with DefaultDependencies {
  protected override val parser: Parser = new JsSubsetParser {}
  protected override val modules: Seq[Module] = Seq(Prelude, JsSubset)
}
```
