In Java

```java
public static void main(String[] args) {
  int x = 0;
  new Runnable() {
    public void run() {
        x = 1; // compilation error
    }
  }.run();
}
  
```

In scala

```scala
var constant=1
def addX(x: Int) = x + constant
def updateConstant(x: Int) { constant = x }
println(addX(100)) // prints 101
updateConstant(100)
println(constant) // prints 100
println(addX(100)) // prints 200
constant=2
println(addX(100)) // prints 200

// In a distributed system, probably the closure doesn't work as expected
```

