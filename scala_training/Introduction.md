- # A Scalable language
- # Object-Oriented
- # Functional
- # Seamless Java Interop
- # Functions are Objects
- # Future-Proof
- # Fun
 <h3>--Martin Odersky</h3>

# First Example
- Compile & Run

```scala
1.scala:

object HelloWorld {
  def main(args: Array[String]) {
    println("Hello, world!")
  }
}

scalac 1.scala
scala HelloWorld
```

- Execute the code in Scala Shell

```scala
bin/scala
scala>println("Hello, world!")
Hello, world！
```

- Run the code as Script

```scala
file: 2.scala

println("Hello, world!")
    
$/tmp>scala 2.scala
Hello, world！

```

- Run Scala in Shell Scripts

```scala
file: script.sh
#!/bin/sh
exec scala "$0" "$@"
!#
object HelloWorld {
  def main(args: Array[String]) {
    println("Hello, world! " + args.toList)
  }
}
HelloWorld.main(args)
```
