- Generic Type

```scala
case class MyList[T] (head: T, tail: MyList[T]) {
  def ::(e: T): MyList[T] = MyList(e, this)
}

class Stack[T] {
  var elems: MyList[T] = MyList[T](null.asInstanceOf[T], null.asInstanceOf[MyList[T]])
  def push(x: T) { elems = x :: elems }
  def top: T = elems.head
  def pop() { elems = elems.tail }
}

val stack = new Stack[Int]
stack.push(1)
stack.push('a')
println(stack.top)
stack.pop()
println(stack.top)
```

- Polymorphic Method
```scala
trait Helper {
   def mkString[T](o: T): String = {
     if (o == null) {
        "null"
     } else {
        o.toString()
     }
   }
}

class A extends Helper
new A().mkString("D")
new A().mkString(1)
new A().mkString(null)
```

```scala
object Traversal {
  import scala.reflect.ClassTag
  def map[FROM, TO: ClassTag](input: Array[FROM], f: FROM => TO): Array[TO] = {
    val dest = new Array[TO](input.length)
    var i = 0
    while (i < input.length) {
      dest(i) = f(input(i))
      i += 1
    }
    
    dest
  }
}

val a = Array(1, 2, 3, 4, 5)
val b = Traversal.map(a, (x: Int) => x.toDouble)
```

- Covariance

1. covariant (C[+A]): C[A] is a subclass of C[B] if A is a sub class of B. 
   For example: List[String] is subclass of List[Any], as String is the sub class of Any
2. contravariant (C[-A]: C[A] is a subclass of C[B] if B is a sub class of A
   For example:
   
   ```scala
     trait Function1[-P, +R] {
       def apply(p: P): R
     }
     case class List[+A](head: A, tail: List[A]) {
       def prepend[U >: A](e: U): List[U] = List(e, this)
     }

   ```
   
3. invariant (C[A]): C[A] is not related with C[B] even if A is a subclass of B
  For example:
  java.util.List[String] V.S. java.util.List[Object] (no relation between them)

  ```java
    Object[] arr = new Integer[1];
    
    // RuntimeException
    arr[0] = "Hello, there!"; // And it’s not allowed in Scala during compile time, as Array is invariant in Scala.
   ```

- Invariant

```scala
val a = new Array[Int](2)
a(0) = 1
a(1) = 2
a(0) = “test” // compilation error 


case class ListNode1[T](h: T, t: ListNode1[T]) {
  def head: T = h
  def tail: ListNode1[T] = t
  def prepend(elem: T): ListNode1[T] =
    ListNode1(elem, this)
}
val a = ListNode1[String](null, null)
a.prepend("a").prepend("b") // ListNode1[String]
a.prepend("a").prepend(123) // error
```

- Upper Bound

```scala
import java.util.{List => JList, ArrayList => JArrayList}
object CollectionTest {
  def combine[T <:JList[String]](left: T, right: T): T = {
    left.addAll(right)
    left
  }
}

val a = new JArrayList[String]()
val b = new JArrayList[String]()
a.add("a")
a.add("b")
b.add("c")
b.add("d")
val c = CollectionTest.combine(a, b)
```

A more concrete Example:

```
abstract class TreeNode[BaseType <: TreeNode[BaseType]] {
  def left: BaseType
  def right: BaseType
}

trait Expression extends TreeNode[Expression] {
  def eval: Int
}

trait Leaf extends Expression {
  def left: Expression = ???
  def right: Expression = ???
}

case class Literal(value: Int) extends Leaf {
  def eval = value
}
case class Add(left: Expression, right: Expression) extends Expression {
  def eval = left.eval + right.eval
}
case class Minus(left: Expression, right: Expression) extends Expression {
  def eval = left.eval - right.eval
}
case class Multiply(left: Expression, right: Expression) extends Expression {
  def eval = left.eval * right.eval
}
case class Divide(left: Expression, right: Expression) extends Expression {
  def eval = left.eval / right.eval
}
```

- Lower Bound

```scala
case class ListNode[+T](h: T, t: ListNode[T]) {
  def head: T = h
  def tail: ListNode[T] = t
  
  // U must be the parent class(or the same class) of T
  def prepend[U >: T](elem: U): ListNode[U] =
    ListNode(elem, this)
}

val a = new ListNode(1, null) // T = Int
val b = a.prepend(1L)         // T = AnyVal
val c = b.prepend("abc")      // T = Any
```

- View Bound (Is deprecated)

Usually we have the function definition like:

```scala
def f[A <% B](a: A) = a.bMethod
or
def f[A](a: A)(implicit ev: A => B) = a.bMethod
```


```scala
implicit def strToInt(x: String) = x.toInt

object MyMath { 
  def addIt[A <% Int](x: A) = 123 + x 
  def max[A <% Int](a: A, b: A): Int = if (a > b) a else b
}

MyMath.max("123", "234") // 234
MyMath.addIt(1) // 124

```

- Context Bound

Usually we have the function definition like:

```scala
 // the function h requires input paramemter in the type of B[A]
def g[A : B](a: A) = h(a)
def g[A](a: A)(implicit ev: B[A]) = h(a)

def f[A](a: A, b: A)(implicit ord: Ordering[A]) = ord.compare(a, b)
def f[A : Numeric](a: A, b: A) = implicitly[Numeric[A]].plus(a, b)
```

- Abstract type members

In a trait, you can leave type members abstract.

```scala
// Type Memeber Version
trait Getter { 
  type A
  var x: A
  def getX: A = x 
}

trait Setter {
  type A
  var x: A
  def setX(x: A) {
    this.x = x
  }
}

class IntFoo extends Getter with Setter { 
  type A = Int
  var x = 123
}
   
class StringFoo extends Getter with Setter { 
  type A = String
  var x = "hey"
}


// Or the Generic Parameter Type Version
trait Getter[A] {
  var x: A
  def getX: A = x
}

trait Setter[A] {
  var x: A
  def setX(x: A) {
    this.x = x
  }
}

class IntFoo(var x: Int) extends Getter[Int] with Setter[Int]
class StringFoo(var x: String) extends Getter[String] with Setter[String]
```

More discussion: http://stackoverflow.com/questions/1154571/scala-abstract-types-vs-generics

- Contra-variance

```scala
case class ListNode2[+T](h: T, t: ListNode2[T]) {
  def head: T = h
  def tail: ListNode2[T] = t
  def prepend[U >: T](elem: U): ListNode2[U] =
    ListNode2(elem, this)
}

val a = ListNode2[String]("null", null)
a.prepend("a").prepend("b") // ListNode2[String]
a.prepend("a").prepend(123) // ListNode2[Any]
```


