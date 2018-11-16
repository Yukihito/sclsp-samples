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

 [1] com.yukihitoho.sclspsamples.jssubset.Main
 [2] com.yukihitoho.sclspsamples.repl.Main
 [3] com.yukihitoho.sclspsamples.simpleusage.Main

Enter number: 
```

## Contents
- Simple usage
  - A simple example of embedding sclsp in a scala application.
  - This program source codes are contained in [this directory](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/simpleusage)  

- REPL
  - The REPL of sclsp with default interpreter implementation.
  - This implementation contains the code examples about below.
    - The simple example of embedding sclsp in scala application.
    - Error handling. (Getting error position in code. Usages about stack trace object.)
    - Converting result object to external representation of S-expression.
  - This program source codes are contained in [this directory](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/repl)
 
- Subset of JavaScript
  - Implementing JavaScript subset by replace the default sclsp parser with the JavaScript parser.
  - If you want to look how it works at a glance, read [the Main class](https://github.com/Yukihito/sclsp-samples/blob/master/src/main/scala/com/yukihitoho/sclspsamples/jssubset/Main.scala).
  - This implementation contains the code examples about below.
    - Implementing a parser of the subset of JavaScript by using [scala-parser-combinators](https://github.com/scala/scala-parser-combinators).
    - Injecting a own parser implementation to a interpreter.
    - Adding synonym of built-in operators.
  - This program source codes are contained in [this directory](https://github.com/Yukihito/sclsp-samples/tree/master/src/main/scala/com/yukihitoho/sclspsamples/jssubset)    
    
  