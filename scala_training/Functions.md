- Function Definition

```scala
// In java:
// public int sum(int a, int b) {
//   return a + b;
// }

def sum(a: Int, b: Int): Int = {
  return a + b
}
def sum(a: Int, b: Int) = {
  a + b
}
def sum(a: Int, b: Int = 2) = {        // val a = sum(1, 3)   or val a = sum(1)
  a + b
}
def fac(n: Int) = if (n == 0) 1 else n * fac(n – 1) // can not compile
def sum(a: Int, b: Int) = a + b
def sum(a: Int)(b: Int) = a + b
def sum(a: Int, b: Int) = ???
def process(a: Int): Unit = {}        // process(2)  
def process(a: Int) {}                // process(2) function without return value
def start = {2}                       // val a = start
def start() = {2}                     // val a = start()
println (process _)     // outputs <function1>|
println (start _)       // outputs <function0>
```

- Function as Parameter of another Function

```scala
 def oncePerSecond(callback: (Int) => Unit) {
   var i = 0
    while (i < 5) { callback(i); Thread sleep 1000; i += 1 }
  }
  def timeFlies(i: Int) {
    println("time flies like an arrow... " + i)
  }
  oncePerSecond(timeFlies)
```

- Anonymous Function

```scala
 def oncePerSecond(callback: (Int) => Unit) {
   var i = 0
    while (i < 5) { callback(i); Thread sleep 1000; i += 1 }
  }
  def timeFlies(i: Int) {
    println("time flies like an arrow... " + i)
  }
  oncePerSecond(timeFlies)

  oncePerSecond((i: Int) => { println ("I am an anonymous function." +i) })

  val d = (i: Int) => { println("test2.." + i) }
  oncePerSecond(d)
```

- Nest Function

```scala
def sumDoubles(n: Int): Int = {
  def dbl(a: Int) = 2 * a

  if(n > 0) {
    dbl(n) + sumDoubles(n - 1)
  } else {
    0 
  }              
}
```

Equivalent to

```scala
def dbl(a: Int) = 2 * a

def sumDoubles(n: Int): Int = {
  if(n > 0) {
    dbl(n) + sumDoubles(n - 1)
  } else {
    0 
  }              
}

```

- Implicit Function

```scala
    import java.math.{BigDecimal => JDecimal, MathContext}
    import scala.math.{BigDecimal => SDecimal}

    val a1 = new JDecimal(1231.123)  // Or produced by Java API
    val a2 = new JDecimal(456.456)    // Or produced by Java API
     
    def square(s: SDecimal) = s * s      // The Scala API requires ScalaDecimal
    def J2SDecimal(j: JDecimal) = new SDecimal(j,  MathContext.DECIMAL128)
    
    //////////////////////////////////////
    val b3 = square(J2SDecimal(a1))
    val b4 = square(J2SDecimal(a2))
    /////////////////////////////////////  ==>
    
    implicit def J2SDecimal(j: JDecimal) = new SDecimal(j,  MathContext.DECIMAL128)
    
    val b1 = square(a1)
    val b2 = square(a2)


```

- Currying Function

-- Example 1

```scala
def sum(a: Int)(b: Int) = a + b
val add5 = sum(5) _
println (add5(20))             // outputs 25

```

-- Example 2

```scala
def sum(a: Int, b: Int) = a + b
val add5: Int=>Int = sum(_, 5)
println (add5(20))             // outputs 25
```

-- Example 3

```scala
def sum(a: Int, b: Int) = a + b
def add5(x: Int) = sum(x, 5)
println (add5(20))             // outputs 25
```

-- Example 4

```scala
def query(conn: Connection)(c: Criteria) : Seq[Person] = {
   ctx.sql(sql).collect.map(…..)
}
val queryBackup= query(backupConnection) _
val queryOnline = query(onlineConnection) _
for (criteria <- criterias) {
  assert(queryBackup(critera) == queryOnline(critera))
}
```

- Partial Function

-- Example 1

```scala
val fraction = new PartialFunction[Int, Int] {
    def apply(d: Int) = 42 / d
    def isDefinedAt(d: Int) = d != 0
}

fraction.isDefinedAt(42)
fraction.isDefinedAt(0)
fraction(42)
fraction(0)
```

-- Example 2

```scala
val fraction: PartialFunction[Int, Int] =
    { case d: Int if d != 0 ⇒ 42 / d }

fraction(42)
fraction(0)

```

- High Order Function

-- Example 1

```scala
def safeStringOp(s: String, f: String => String) = {
  if (s != null) f(s) else s
}

def reverser(s: String) = s.reverse
safeStringOp(null, reverser)
safeStringOp("Ready", reverser)
```

-- Example 2

```scala
 def whileLoop(cond: => Boolean)(body: => Unit): Unit =
    if (cond) {
      body
      whileLoop(cond)(body)
    }
    
  var i = 10
  whileLoop (i > 0) {
    println(i)
    i -= 1
  }

  def loop(body: => Unit): LoopUnlessCond =
    new LoopUnlessCond(body)
    
  protected class LoopUnlessCond(body: => Unit) {
    def unless(cond: => Boolean) {
      body
      if (!cond) unless(cond)
    }
  }

  var i = 10
  loop {
    println("i = " + i)
    i -= 1
  } unless (i == 0)

```


