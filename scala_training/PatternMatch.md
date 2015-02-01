- Traditional approach

```scala
def toYesOrNo(choice: Int): String = choice match {
  case 1 => "yes"
  case 0 => "no"
  case _ => "error"
}

def toYesOrNo(choice: Int): String = choice match {
  case 1 | 2 | 3 => "yes"
  case 0         => "no"
  case _         => "error"
}

def parseArgument(arg: String) = arg match {
  case "-h" | "--help"    => displayHelp
  case "-v" | "--version" => displayVerion
  case whatever           => unknownArgument(whatever)
}
```

- Type Matching

```scala
def f(x: Any): String = x match {
    case i:Int => "integer: " + i
    case _:Double => "a double"
    case s:String => "I want to say " + s
}
```

- Extractor

```scala
// Case Class
case class Address(street: String, county: String, state: String, country: String)

def extract(addr: Address) = addr match {
  case a @ Address(_, _, _, "US") => s"$a is in North America"
  case a @ Address(_, _, _, country) => s"$a is not a US address, but $country"
}

println(extract(Address("ZhongShan RD No.1", "HuangPu", "ShangHai", "China")))
println(extract(Address("Main ST 1", "Sunnyvale", "CA", "US")))

// unapply function
trait User {
  def name: String
}
class FreeUser(val name: String) extends User
class PremiumUser(val name: String, val level: Int) extends User

object FreeUser {
  def unapply(user: FreeUser): Option[String] = Some(user.name)
}
object PremiumUser {
  def unapply(user: PremiumUser): Option[(String, Int)] = Some((user.name, user.level))
}

val user1: User = new PremiumUser("Daniel", 3)
val user2: User = new FreeUser("Robert")

def sayHi(user: User) = user match {
  case FreeUser(name) => println(s"Hello $name")
  case PremiumUser(name, level) => println(s"Welcome back, Level $level user $name")
}

sayHi(user1)
sayHi(user2)
```

- Matching on Collection

```scala
def length[A](list : List[A]) : Int = list match {
  case _ :: tail => 1 + length(tail)
  case Nil => 0
}

val seq = Seq(1, 2, 3, 4)
seq match {
  case a :: b :: tail => println (s"first two [$a] [$b]")
  case _ => sys.error("not found")
}

seq match {
  case a :: _ :: _ :: last :: Nil => println (s"first and last [$a] [$last]")
  case _ => sys.error("not found")
}

val seq1 = Seq(1, 2, 3, 4)
val seq2 = Seq(2, 3, 4)

def matchFirst2(seq: Seq[Int]) = seq match {
  case 1 :: 2 :: tail => println ("first two element is 1 and 2")
  case _ => println ("first two element is not 1 and 2")
}

matchFirst2(seq1)
matchFirst2(seq2)
```

- Pattern Guard

```scala
case class User(name: String, age: Int)

val user1: User = new User("Daniel", 3)
val user2: User = new User("Robert", 24)

def permit(user: User) = user match {
  case User(name, age) if age >= 18 => println(s"Hello $name, welcome!")
  case User(name, _) => println(s"Sorry $name, you must be older than 18")
}

permit(user1)
permit(user2)
```

- A little more complicated Example

```scala
abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

def simplifyAll(expr: Expr): Expr = expr match {
  case UnOp("-", UnOp("-", e)) =>
    simplifyAll(e) // `-' is its own inverse
  case BinOp("/", e1, e2 @ Number(d)) if d == 0=> sys.error("cannot divide 0")
  case BinOp("+", Number(0), e)=>
    simplifyAll(e) // `0' is a neutral element for `+'
  case BinOp("*", Number(1), e) =>
    simplifyAll(e) // `1' is a neutral element for `*'
  case UnOp(op, e) =>
    UnOp(op, simplifyAll(e))
  case BinOp(op, l, r) =>
    BinOp(op, simplifyAll(l), simplifyAll(r))
  case _ => expr
}

// -(-(0+2))
val expr = UnOp("-",  UnOp("-", BinOp("+", Number(0), Number(2))))
simplifyAll(expr)
// -(-(2/0))
val expr = UnOp("-",  UnOp("-", BinOp("/", Number(2), Number(0))))
simplifyAll(expr)
```
