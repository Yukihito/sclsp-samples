# Sharing variables with remote process.
このサンプルは、リモートのプロセスと(レキシカルクロージャを含む)変数を共有できるような評価器の sclsp での実装例です。

リモートのプロセスとデータをやりとりする方法には様々なものがあります。
このサンプルでは、「プロセスとデータを切り離し、プロセスがデータにアクセスする際に抽象化されたデータストアのインターフェースを介すことによって、具体的なデータストアの実装を選択可能にし、このデータストアにネットワーク経由でアクセスできるものを選択する」という方法を使って、リモートのプロセスと変数の共有を行います。この方法を用いる場合、クロージャのように自身の定義時の環境の情報を含むような値も、通常の値と同じようにリモートのプロセスと共有できます。

サンプルコードは[ここ](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/sharedenv)にあります

このサンプルに含まれる内容は次のとおりです
- 自前で作成した変数のデータストアの実装の依存性をインタプリタに注入する

# サンプルの実行

サンプルでは、複数プロセスで共有する変数のデータストアに [Redis](https://redis.io/) を利用します。
そのため、サンプルの実行の前に redis-server を立ち上げる必要があります。
```
$ redis-server
```
以降の説明は、「ローカルに Redis を立て、ローカルで実行している複数のREPLのプロセスがその Redis を使って変数の共有を行う」という前提で進めます。

REPLを実行するには、プロジェクトのルートで次のコマンドを実行します。Lisp版とJavaScript(の小さいサブセット)版のREPLが選択可能です。(See also [Subset of JavaScript](https://github.com/Yukihito/sclsp-samples/tree/master/docs/JAVASCRIPT_SUBSET.ja.md))
```
# Lisp版のREPLを立ち上げる場合
$ sbt "runMain com.yukihitoho.sclspsamples.sharedenv.LispReplForSharedEnvironmentMain"

# JavaScript版のREPLを立ち上げる場合
$ sbt "runMain com.yukihitoho.sclspsamples.sharedenv.JavaScriptReplForSharedEnvironmentMain"
```

以降、AをLisp版で立ち上げたREPLを使うユーザ、BをJavaScript版で立ち上げたREPLを使うユーザとして、Bが定義したクロージャをAが利用する例を示します

(1) B が JavaScript版 の REPL を立ち上げ、以下の手順で小さいチャットアプリを定義します

```
$ sbt "runMain com.yukihitoho.sclspsamples.sharedenv.JavaScriptReplForSharedEnvironmentMain"

> var _messages = [];
_messages
> var chat = function(msg) {
>   _messages = cons(msg, _messages);
>   "Message sent!"
> }
chat
> var show = function() {
>   _messages;
> }
show
```
(2) B が (1) で定義した "chat" をつかってメッセージを投稿します
```
> chat("hello");
"Message sent!"
```
(3) A が Lisp版の REPL を立ち上げ、 (1) で B が定義した "show" をつかって投稿されたメッセージを確認します
```
$ sbt "runMain com.yukihitoho.sclspsamples.sharedenv.LispReplForSharedEnvironmentMain"

> (show)
("hello")
``` 

(4) A が (1) で B が定義した "chat" を使って返信します
```
> (chat "hi")
"Message sent!"
```

(5) B が (1) で定義した "show" を使って投稿されたメッセージを確認します
```
> show();
("hi" "hello")
```

# 実装の詳細
REPL 自体の実装の詳細は [REPL](https://github.com/Yukihito/sclsp-samples/tree/master/docs/REPL.ja.md) を参照してください

## データストアのインターフェースの実装
自前でデータストアの実装するには、次のクラスを継承した実装を定義する必要があります。
- [VariablesRepository](): 変数の名前と値のマッピングを永続化し、名前をキーとして保存された値を取得する機能を提供するインターフェースです。
- [Environment](): VariablesRepository を用いて変数の永続化を行います。また、ツリー上の親子関係を表現することができ、これによって静的スコープを実現します。
- [EnvironmentFactory](): Environment を作成するインターフェースです。Environment の具体的な実装として何が選択されるかをこれによって隠蔽します。

Redisのクライアントとして [Lettuce](https://lettuce.io/) を、値のシリアライズに [circe](https://circe.github.io/circe/) をつかって上記を実装した例が [SharedEnvironment.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/sharedenv/SharedEnvironment.scala) です。
(サンプル実装のため、データストア内のデータのガベージコレクションなど、メモリ管理について一切考慮されていません。また、パフォーマンスを考慮した実装にもなっていません)

## データストアの依存性の注入
次のような要領で、実装したデータストアの依存性をInterpreterに注入します

[LispReplForSharedEnvironmentMain.scala](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/sharedenv/LispReplForSharedEnvironmentMain.scala)

```scala
class SharedEnvironmentInterpreter(
  connection: StatefulRedisConnection[String, String]
) extends Interpreter with DefaultDependencies {
  protected override val environmentFactory: EnvironmentFactory = new RootSharedEnvironmentFactory(connection)
  ...
}
```