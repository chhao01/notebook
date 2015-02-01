- Mutable V.S Immutable

```scala
import scala.collection.mutable
import scala.collection
import scala.collection.immutable (default imported)
```

1. Collection Trait Graph
![collection](http://i.stack.imgur.com/bSVyA.png)

2. Immutable Graph
![immutable](http://i.stack.imgur.com/2fjoA.png)

3. Mutable Graph
![mutable](http://i.stack.imgur.com/Dsptl.png)

- Create Immutable Collection ([Seq](http://docs.scala-lang.org/overviews/collections/seqs.html),[Set](http://docs.scala-lang.org/overviews/collections/sets.html),[Map](http://docs.scala-lang.org/overviews/collections/maps.html),[Common Used Collection](http://docs.scala-lang.org/overviews/collections/concrete-immutable-collection-classes.html))

```scala
Traversable()             // An empty traversable object
List()                    // The empty list
List(1.0, 2.0)            // A list with elements 1.0, 2.0
Vector(1.0, 2.0)          // A vector with elements 1.0, 2.0
Iterator(1, 2, 3)         // An iterator returning three integers.
Set("dog", "cat", "bird") // A set of three animals
HashSet("dog", "cat", "bird")   // A hash set of the same animals
Map('a' -> 7, 'b' -> 0)     // A map from characters to integers

val seq = Seq("a", "b", "c")
val seq = "a" :: "b" :: "c" :: Nil // Nil a object of List[Nothing]() // Contra-variant & Co-variant
```

- Create mutable Collection ([Common Used Collection](http://docs.scala-lang.org/overviews/collections/concrete-mutable-collection-classes.html))

```scala
val buf = scala.collection.mutable.ListBuffer.empty[Int] // Invariant
buf += 1
buf += 10
```

- Array (correspond one-to-one to Java arrays, but with some collection methods) ([Array](http://docs.scala-lang.org/overviews/collections/arrays.html ))

```scala
val a1 = Array(1, 2, 3)
a1: Array[Int] = Array(1, 2, 3)
val a2 = a1 map (_ * 3)
```

- Loop & Yield

```scala
for (i <- 1 to 5) yield i // 1, 2, 3, 4, 5
for (i <- 1 until 5) yield i // 1, 2, 3, 4
for (i <- 1 to 5) yield i % 2
for (i <- 0 to 5; // nested loop
      j <- i to 5)
    yield Pair(i, j)

val a = Array(1, 2, 3, 4, 5)

// for loop yield over Collection
for (e <- a) yield e * 2

// for loop, yield, and guards (for loop 'if' conditions)
for (e <- a if e > 2) yield e

// foreach method
a.foreach(mbr => println(mbr)) // iterate all of the elements in the collection

// while
var i = 0
while(i < a.length) {
    println(a(i))
    i += 1
}

// do .. while
var i = 0
do {
    println(a(i))
    i += 1
} while (i < a.length)

// Create a new Collection
val transformed = a.map(_ + 20) // _ + 20 is a anonymous function

// Filter
val result = a.filter((i: Int) => i % 2 = 0) 
val result = a.filter(_ % 2 == 0)

// zip
List(1, 2, 3).zip(List("a", "b", "c")).map { case (a, b) => // partitial function
    b + a
}

val (odds, evens) = Array(1,2,3,4,5,6,7).partition(_ % 2 == 1)

// find
Array(1,2,3,4,5).find(_ > 3) // Some(4)

// exists
Array(1,2,3,4,5).exists(_ > 3) // true

// drop
Array(1,2,3,4,5,6,7,8,9).drop(5)

// dropWhile
Array(1,2,3,4,5,6,7,8,9).dropWhile(_ % 2 == 0)

// foldLeft
Array(1,2,3,4,5,6,7,8,9).foldLeft(0) { (sum: Int, e: Int) => println(s"sum:$sum e:$e"); sum + e }

// foldRight
Array(1,2,3,4,5,6,7,8,9).foldRight(0) { (e: Int, sum: Int) =>println(s"sum:$sum e:$e"); sum + e }

// flatten
List(List(1, 2), List(3, 4)).flatten

// flatMap
List(List(1, 2), List(3, 4)).flatMap(x => x.map(_ * 2))

// collect
val mixedList = List("a", 1, 2, "b", 19, 42.0) //this is a List[Any]
val results = mixedList collect {
  case s: String => "String:" + s
  case i: Int => "Int:" + i.toString 
  // 42.0 doesn't match any of the partial function, will be ignored
}

```

- Views

It's used for lazy computing (Performance V.S Modular)

```scala
val v = Vector(1,2,3,4)
v map(_ + 1)

val vv = v.view map(_ + 1)
vv.force
```

Another Example

```scala
// assume we have a predefined function and we can not change it
def exists(dates: Seq[java.util.Date], lucky: java.util.Date) = dates.exists(_ == lucky) 

// and we have a very long time sequence, but in Long type
val dates = Seq(1019299l, 923939l, 2929391l)

// How can we reuse the predefined function and with less performance penalty?
val vdates = dates.view.map(new java.util.Date(_))

exists(vdates, new java.util.Date(1019299))
```

- Iterators

Iterator Definition

```scala
abstract class Iterator[+A] {
  def hasNext: Boolean
  def next: A
}

// how we iterate the iterator
while (it.hasNext) 
  println(it.next())

```

More Examples

```scala
val it = Iterator("a", "b", "c")
val it1 = Iterator("a", "b", "c")
val it2 = Iterator(1, 2, 3)
val it = it1 ++ it2
it.foreach(println)

val it3 = Iterator(4,5,6)
val (it1, it2) = it3.duplicate // it3 is invalid
it1.foreach(println)
it2.foreach(println)
```

- Java & Scala Collection

```scala
import collection.JavaConversions._ // implicit function

scala.collection.Seq         => java.util.List
scala.collection.mutable.Seq => java.util.List
scala.collection.Set         => java.util.Set
scala.collection.Map         => java.util.Map
java.util.Properties         => scala.collection.mutable.Map[String, String]

Iterator               <=>     java.util.Iterator
Iterator               <=>     java.util.Enumeration
Iterable               <=>     java.lang.Iterable
Iterable               <=>     java.util.Collection
mutable.Buffer         <=>     java.util.List
mutable.Set            <=>     java.util.Set
mutable.Map            <=>     java.util.Map
mutable.ConcurrentMap  <=>     java.util.concurrent.ConcurrentMap


val a = Seq(1,2,3,4,5) // a: Seq[Int] = List(1, 2, 3, 4, 5)
val b: java.util.List[Int] = a // b: java.util.List[Int] = [1, 2, 3, 4, 5]


```

- Option

```scala
trait Option[T] {
  def isDefined: Boolean
  def get: T
  def getOrElse(t: T): T
}

// Option has 2 subclasses Some[T] & None 

val numbers = Map(1 -> "one", 2 -> "two")
numbers.get(2) // Some("two")
numbers.get(3) // None

val m = Map(2 -> "Two", 1 -> "One")
val result = m.getOrElse(1, "Unknow")
  
val m2 = new collection.mutable.HashMap[Int, String]()
m2.getOrElseUpdate(1, "One")
m2.getOrElseUpdate(2, "Two")

println(m2(1))


Array(Option(1), None, Option(2), Option(3), None).collect {
  case Some(i) => i
  case None => -1
}

```

- Tuple

Tuple is a trait with sub trait (Tuple1, Tuple2 ... Tuple22)

```scala
val stuff = (42, "fish") // stuff: (Int, String) = (42,fish)
println(stuff._1) // 42
println(stuff._2) // fish
val stuff3 = (54, 21.3, "fish") // stuff3: (Int, Double, String) = (54,21.3,fish)
def getStuff = stuff3 // def getStuff: (Int, Double, String)
val (v1, _, v3) = getStuff // v1: Int = 54 & v3: String = fish

```

- Product

Product is a trait with sub trait (Product1, Product2 ... Product22)

```scala
trait Product extends Any with Equals {
  def productElement(n: Int): Any

  /** The size of this product   */
  def productArity: Int

  /** An iterator over all the elements of this product.   */
  def productIterator: Iterator[Any] = new scala.collection.AbstractIterator[Any]
}

trait RecordType {
  self: Product =>
  def records: String = (
    self.productIterator.map({ arg =>
      arg.getClass.getSimpleName + ":" + arg
    }).toSeq.mkString("\n"))
}
case class X(i: Int, c: Char, s: String) extends RecordType

val x = X(5, 'd', "test")
println(x.records)
```


