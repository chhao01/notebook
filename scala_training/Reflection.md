- TypeTag
 Type erased in runtime, we can not run code like below:

```scala
class Foo
class Bar extends Foo

def meth[A](xs: List[A]) = xs match {
  case _: List[String] => "list of strings"
  case _: List[Foo] => "list of foos"
}
```

TypeTag works for the fixing, which bring the compile time information to runtime

```scala
import scala.reflect.runtime.universe._

class Foo
class Bar extends Foo


def meth[A : TypeTag](xs: List[A]) = typeOf[A] match {
  case t if t =:= typeOf[String] => "list of strings"
  case t if t <:< typeOf[Foo] => "list of foos"
}
```

- ClassTag
  A ClassTag[T] stores the erased class of a given type T, accessible via the runtimeClass field. This is particularly useful for instantiating Arrays whose element types are unknown at compile time.

```scala
import scala.reflect.ClassTag

def mkArray[T: ClassTag](elems: T*) = Array[T](elems: _*) // * means the variant length of argument
mkArray(42, 13)
mkArray("Japan","Brazil","Germany")
mkArray(12, "a")
```

- classOf & typeOf

```scala
classTag[Int] // res99: scala.reflect.ClassTag[Int] = ClassTag[int]
classTag[Int].runtimeClass // res100: Class[_] = int
classTag[Int].newArray(3) // res101: Array[Int] = Array(0, 0, 0)
classTag[List[Int]] // res104: scala.reflect.ClassTag[List[Int]] = ClassTag[class scala.collection.immutable.List]

typeTag[List[Int]] //  res105: reflect.runtime.universe.TypeTag[List[Int]] = TypeTag[scala.List[Int]]
typeTag[List[Int]].tpe // res107: reflect.runtime.universe.Type = scala.List[Int]
typeOf[List[Int]] // res108: reflect.runtime.universe.Type = scala.List[Int]
res107 =:= res108 // true
```

- Instantiating a type in runtime

```scala
import scala.reflect.runtime.{universe => ru}
case class Person(name: String)

val classPerson = ru.typeOf[Person].typeSymbol.asClass
val m = ru.runtimeMirror(getClass.getClassLoader)
val cm = m.reflectClass(classPerson)
val ctor = ru.typeOf[Person].declaration(ru.nme.CONSTRUCTOR).asMethod
val ctorm = cm.reflectConstructor(ctor)
val p = ctorm("Mike")
```
- Accessing a member of a type in runtime

```scala
import scala.reflect.runtime.{universe => ru}

case class Purchase(name: String, orderNumber: Int, var shipped: Boolean)
val p = Purchase("Jeff Lebowski", 23819, false)
val m = ru.runtimeMirror(p.getClass.getClassLoader)
val shippingTermSymb = ru.typeOf[Purchase].declaration(ru.newTermName("shipped")).asTerm
val im = m.reflect(p)
val shippingFieldMirror = im.reflectField(shippingTermSymb)
shippingFieldMirror.get // false
shippingFieldMirror.set(true) // set to true
shippingFieldMirror.get // false
```

- Concrete Example

```scala

abstract class DataType
case object StringType extends DataType
case object BooleanType extends DataType
case object LongType extends DataType
case object IntegerType extends DataType
case object ShortType extends DataType
case object ByteType extends DataType
case object BinaryType extends DataType
case object DoubleType extends DataType
case object FloatType extends DataType
case class ArrayType(elementType: DataType) extends DataType
case class StructField(name: String, dataType: DataType)
case class StructType(fields: Seq[StructField]) extends DataType

object ScalaReflection {
  val universe: scala.reflect.runtime.universe.type = scala.reflect.runtime.universe
  import universe._

  // The Predef.Map is scala.collection.immutable.Map.
  // Since the map values can be mutable, we explicitly import scala.collection.Map at here.
  import scala.collection.Map
  def schemaFor[T: TypeTag]: DataType = schemaFor(typeOf[T])

  /** Returns a catalyst DataType and its nullability for the given Scala Type using reflection. */
  def schemaFor(tpe: `Type`): DataType = {
    val className: String = tpe.erasure.typeSymbol.asClass.fullName
    tpe match {
      case t if t <:< typeOf[Option[_]] =>
        val TypeRef(_, _, Seq(optType)) = t
        schemaFor(optType)
      case t if t <:< typeOf[Array[Byte]] => BinaryType
      case t if t <:< typeOf[Array[_]] =>
        sys.error(s"Only Array[Byte] supported now, use Seq instead of $t")
      case t if t <:< typeOf[Seq[_]] =>
        val TypeRef(_, _, Seq(elementType)) = t
        ArrayType(schemaFor(elementType))
      case t if t <:< typeOf[Product] =>
        val formalTypeArgs = t.typeSymbol.asClass.typeParams
        val TypeRef(_, _, actualTypeArgs) = t
        val constructorSymbol = t.member(nme.CONSTRUCTOR)
        val params = if (constructorSymbol.isMethod) {
          constructorSymbol.asMethod.paramss
        } else {
          // Find the primary constructor, and use its parameter ordering.
          val primaryConstructorSymbol: Option[Symbol] = constructorSymbol.asTerm.alternatives.find(
            s => s.isMethod && s.asMethod.isPrimaryConstructor)
          if (primaryConstructorSymbol.isEmpty) {
            sys.error("Internal SQL error: Product object did not have a primary constructor.")
          } else {
            primaryConstructorSymbol.get.asMethod.paramss
          }
        }
        StructType(
          params.head.map { p =>
            val dataType = schemaFor(p.typeSignature.substituteTypes(formalTypeArgs, actualTypeArgs))
            StructField(p.name.toString, dataType)
          })
      case t if t <:< typeOf[String] => StringType
      case t if t <:< typeOf[java.lang.Integer] => IntegerType
      case t if t <:< typeOf[java.lang.Long] => LongType
      case t if t <:< typeOf[java.lang.Double] => DoubleType
      case t if t <:< typeOf[java.lang.Float] => FloatType
      case t if t <:< typeOf[java.lang.Short] => ShortType
      case t if t <:< typeOf[java.lang.Byte] => ByteType
      case t if t <:< typeOf[java.lang.Boolean] => BooleanType
      case t if t <:< definitions.IntTpe => IntegerType
      case t if t <:< definitions.LongTpe => LongType
      case t if t <:< definitions.DoubleTpe => DoubleType
      case t if t <:< definitions.FloatTpe => FloatType
      case t if t <:< definitions.ShortTpe => ShortType
      case t if t <:< definitions.ByteTpe => ByteType
      case t if t <:< definitions.BooleanTpe => BooleanType
    }
  }
}

case class Person(name: String, age: Int)
case class Room(id: Long, owners: Seq[Person])

ScalaReflection.schemaFor[Room]
```
