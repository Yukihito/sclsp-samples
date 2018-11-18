# Sclsp samples
 
 Some examples of use of [sclsp](https://github.com/Yukihito/sclsp).
 
## Getting started
### Prerequisites
 Install [sbt](https://www.scala-sbt.org/download.html).
 
### Run the samples
Clone this project. And run.
```
$ git clone git@github.com:Yukihito/sclsp-samples.git
$ cd sclsp-samples
$ sbt run  
```

You will see the options of samples like below. Choose one you want.

```
Multiple main classes detected, select one to run:

 [1] com.yukihitoho.sclspsamples.jssubset.JavaScriptSubsetSampleMain
 [2] com.yukihitoho.sclspsamples.repl.ReplMain
 [3] com.yukihitoho.sclspsamples.sharedenv.JavaScriptReplForSharedEnvironmentMain
 [4] com.yukihitoho.sclspsamples.sharedenv.LispReplForSharedEnvironmentMain
 [5] com.yukihitoho.sclspsamples.simpleusage.Main

Enter number: 
```

## Contents
1. Simple usage
   - A simple example of embedding sclsp in a scala application.
   - This program source codes are contained in [this directory](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/simpleusage)
   - Documents of this sample is [here(japanese)](https://github.com/Yukihito/sclsp-samples/tree/master/docs/SIMPLE_USAGE.ja.md).
2. REPL
   - The REPL of sclsp with default interpreter implementation.
   - This implementation contains the code examples about below.
     - The simple example of embedding sclsp in scala application.
     - Error handling. (Getting error position in code. Usages about stack trace object.)
     - Converting result object to external representation of S-expression.
   - This program source codes are contained in [this directory](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/repl)
   - Documents of this sample is [here(japanese)](https://github.com/Yukihito/sclsp-samples/tree/master/docs/REPL.ja.md).   
3. Subset of JavaScript
   - Implementing JavaScript subset by replace the default sclsp parser with the JavaScript parser.
   - If you want to look how it works at a glance, read [the Main class](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/jssubset/JavaScriptSubsetSampleMain.scala).
   - This implementation contains the code examples about below.
     - Implementing a parser of the subset of JavaScript by using [scala-parser-combinators](https://github.com/scala/scala-parser-combinators).
     - Injecting a own parser implementation to a interpreter.
     - Adding synonym of built-in operators.
   - This program source codes are contained in [this directory](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/jssubset)
   - Documents of this sample is [here(japanese)](https://github.com/Yukihito/sclsp-samples/tree/master/docs/JAVASCRIPT_SUBSET.ja.md).
4. Sharing variables (that includes lexical closure) with remote process.
   - An example of an implementation of sharing variables with remote process.
     For sharing data with remote process, there are many ways. Separating processes and data and using common data store is one of them. This sample is sort of that.
     In this case, processes access data by abstract interface. By doing so, we can choose any concrete implementation of data store. 
     Then, by choosing the key value store that can speak in tcp (We selected redis for it in this sample.) as concrete data store, we realize the function of sharing variables.
   - This implementation contains the code examples about below.
     - Injecting a own data store for variables perpetuation to a interpreter.
   - This program source codes are contained in [this directory](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/sharedenv)
   - Documents of this sample is [here(japanese)](https://github.com/Yukihito/sclsp-samples/tree/master/docs/SHARED_ENVIRONMENT.ja.md).