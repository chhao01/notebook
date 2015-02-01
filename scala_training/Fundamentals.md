- Create object instance

```scala
   JAVA: final String[] a = new String[100];
         final double a = 2 + 1.2d;
         double c = a;
         c=1.2d;
         
   SCALA: val a: Array[String] = new Array[String](100) // val == final
          val a = new Array[String](100) // no ;
          val a = 2 + 1.2d // Type Inference .. more examples
          var c = a
          c = 1.2d
          lazy val a = b + c // will be calculated in the first time it used
          var d: String = _ // assign a default value
```

- Import

```scala
import java.util.Array                      // implicit import
import java.util._                          // wildcard
import java.util.{ArrayList, HashSet}       // multiple import
import java.util.{ArrayList => JArrayList}  // renaming
Object a {
    import java.util.ArrayList              // limit the import scope
    def calc: ArrayList = {…}
}

import java.util._
import java.util.{ArrayList => _}           // hide the specific import
import a._                                  // import the method of a specified object

```

- Scala Class Hierarchy
![Scala Class Hierarchy](http://www.scala-lang.org/old/sites/default/files/images/classhierarchy.png)


