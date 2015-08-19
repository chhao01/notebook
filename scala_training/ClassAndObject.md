- Class

```scala
import scala.beans.BeanProperty

abstract class Colorable(@BeanProperty  var color: Int)

class Point(protected var xc: Int, protected var yc: Int, color: Int) extends Colorable(color) {
  def this() = this(Int.MaxValue, Int.MaxValue, Int.MaxValue)
  
  def step(dx: Int, dy: Int) {
    this.xc =  this.xc + dx
    this.xc =  this.xc + dy
  }
  def move(x: Int, y: Int) = {
    this.xc = x
    this.yc = y
    this
  }
  override def toString(): String = s"($xc, $yc, $color)"
}

val pt = new Point (1,2,3)
pt.setColor(456)
println(pt.getColor())
```

- Case Class

-- pattern matching support
-- default implementations of equals and hashCode
-- default implementations of serialization
-- a prettier default implementation of toString, and
-- the small amount of functionality that they get from automatically inheriting from scala.Product

```scala
case class Point (x: Int, y: Int, color: Int)
val pt = Point(1, 2, 3)
pt.productIterator.foreach(println)

abstract class Colorable {
  def color: Int
}
case class Point(x: Int, y: Int, color: Int) extends Colorable
val pt = Point(1, 2, 3)
println(pt.isInstanceOf[Product])

pt.productIterator.foreach(println)
pt.productElement(0)
println(pt.toString)
```

- Inner Class

```scala
class Graph {
  class Node {
    var connectedNodes: List[Node] = Nil
  }
  var nodes: List[Node] = Nil
  
  def newNode: Node = {
    val res = new Node
    nodes = res :: nodes
    res
  }
  
  def addNode(node: Node): this.Node = {
    nodes = node :: nodes
    node
  }
}

val g1 = new Graph
val n = g1.newNode
g1.addNode(n)

val g2 = new Graph
val g2.addNode(n) // error found g1.Node, require g2.Node

```

- Trait

```scala
trait Similarity[T] {
  var threshold: Int = 100
  def isSimilar(x: T): Boolean
  def isNotSimilar(x: T): Boolean = !isSimilar(x)
}

trait Moveable[T] {
  def move(other: T): T
}

abstract class Colorable(val color: Int)
class Point(var x: Int, var y: Int, color: Int) extends Colorable(color) with Similarity[Point] with Moveable[Point] {
  def isSimilar(that: Point): Boolean = if (that == null) {
    false
  } else if (Math.abs(x - that.x) < threshold && Math.abs(y - that.y) < threshold) {
    true
  } else {
    false
  }
  def move(other: Point) = {
    this.x = other.x
    this.y = other.y
    this
  }
}
```

- Anonymous Class

```scala
trait SimpleTrait {
  def add1(x: Int) = { x + 1 }
  def add2(x: Int)
}

abstract class SimpleClass {
  def add3(x: Int) = { x + 3 }
  def add4(x: Int)
}

val a = new SimpleClass {
  def add4(x: Int) = x + 4
}

val a = new SimpleTrait {
  def add2(x: Int) = x + 2
}

val a = new SimpleClass with SimpleTrait {
  def add2(x: Int) = x + 2
  def add4(x: Int) = x + 4
}
```

- Implicit Class

Restrictions:
1. They must be defined inside of another trait/class/object
2. They may only take one non-implicit argument in their constructor.
3. There may not be any method, member or object in scope with the same name as the implicit class.


```scala
object Helpers {
  implicit class IntWithTimes(x: Int) {
    def times[A](f: => A): Unit = {
      def loop(current: Int): Unit =
        if(current > 0) {
          f
          loop(current - 1)
        }
      loop(x)
    }
  }
}

import Helpers._
5.times(println("HI"))

```

A more concrete example:

```scala
class RDD(data: String) {
  def collect() = data
  def compute() = println("processing..")
}

abstract class SQLContext {
  def env: String
  def sql(strText: String): RDD = new RDD(s"[$env]result of executing $strText")
}

object LocalMode {
  implicit object sqlContext extends SQLContext {
    def env = "LocalMode"
  }
}

object ClusterMode {
  implicit object sqlContext extends SQLContext {
    def env = "ClusterMode"
  }
}

object myApp {
  // will search SQLContext instance from the scope
  implicit class SqlExecutor(sql: String)(implicit context: SQLContext) {
    def run = {
      val rdd = context.sql(sql)
      rdd.compute()
      rdd.collect()
    }
  }
}

import myApp._
import ClusterMode._  // import LocalMode._
"select * from src".run

```

- Operators

```scala
case class N(val x: Int) {
  def +(that: N) = N(x + that.x)
  def add(that: N) = this + that

  def -(that: N) = N(x - that.x)
  def minus(that: N) = this - that
  
  def - = N(-x)
  def negate = this -; // ; is required, it may causes ambiguities
  def unary_- = this -; // this is a hack
}

-(N(2) + N(3)) // functional style
((N(2).+(N(3)))).- // OO style
```

- Object & Companion Object

Some most used function, which called implicitly
1. def apply
2. def unapply
3. def update

```scala
class Point (val x: Int, val y: Int, val color: Int)

object ConstantShape {
  val defaultColor = Int.MinValue    
}

// object is singleton
object Point { // companion object with the same name of the class, must defined in the file of the class
  val defaultColor = ConstantShape.defaultColor
    // built-in function
  def apply(x: Int, y: Int): Point = new Point(x, y, defaultColor)
  def apply(x: Int, y: Int, color: Int): Point = new Point(x, y, color)
}

Point(1, 2)  // Point.apply(1, 2), without the companion object we have to add the “new” e.g. val a = new Point(1,2,3)
Point(1, 2, 3) // Point.apply(1, 2, 3)

class Row(val values: Array[Any]) {
  def apply(ordinal: Int): Any = values(ordinal)
  def apply(ordinal: Int, defaultValue: Any): Any = {
    if (values(ordinal) == null) {
      defaultValue
    } else {
      values(ordinal)
    }
  }

  def update(ordinal: Int, value: Any) {
     values(ordinal) = value
  }
}

val row = new Row(Array(1, "abc"))
row(0) = null // update
println(row(0)) // call the apply version 1
println(row(0, "aaaa")) // call the apply version 2


object Extractor { // extractor
  def unapply(input: String): Option[(Int, Int, Int)] = if (input != null) {
    val parts = input.split(",")
    if (parts.length == 3) {
      // TODO more checking
      Some((parts(0).toInt, parts(1).toInt, parts(2).toInt))
    } else {
      None
    }
  } else {
    None
  }
}

"1,2,3" match {
  case Extractor(x, y, color) => println (s"$x  $y  $color")
  case _ => sys.error("something wrong!")
}

"1,2,3,4" match {
  case Extractor(x, y, color) => println (s"$x  $y  $color")
  case _ => sys.error("something wrong!")
}

```

- Explicit Self-Type Reference

Normally used in the Dependency Injection

```
abstract class ConnectionPool {
  def getConnection: java.sql.Connection
}

abstract class MySQLConnectionPool extends ConnectionPool {
  def getConnection: java.sql.Connection = {
    println("getting MySQL connection")
    null
  }
}

trait SQLServerConnectionPool extends ConnectionPool {
  def getConnection: java.sql.Connection = {
    println("getting SQL Server connection")
    null
  }
}

trait UserDao {
  self: ConnectionPool =>
  def authenticate(user: String, pass: String) = {
    val conn = getConnection
    println ("authenticating..")
    true
  }
}

trait BusinessDao {
  self: ConnectionPool =>
  def makeDeal(fromUser: String, toUser: String, deal: Int) {
    val conn = getConnection
    println(s"making deal $fromUser -> $toUser: deal: $deal")
  }
}

// Dependencies Injection
val dao = new SQLServerConnectionPool with UserDao
dao.authenticate("user1", "pass1")
val dao2 = new  MySQLConnectionPool with UserDao with BusinessDao
dao2.authenticate("user1", "pass1")


```


