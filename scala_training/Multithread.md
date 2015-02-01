- Future(lass) & future(method)

```scala
import scala.concurrent._
import scala.util.{Success, Failure}
import ExecutionContext.Implicits.global

// A thread start after call the future method
val f: Future[Seq[Long]] = future {
  // do some db query
  Thread.sleep(2)
  Seq(49,59,29,60,20) // this is the thread return or can throws exception
}

f onComplete { // register the call back
  case Success(ids) => for(id <- ids) println (id)
  case Failure(t) => println("Error has occured:" + t.getMessage)
}
// Or
f onFailure { // register the call back
  case t => println("An error has occured: " + t.getMessage) // this is partial function
}
f onSuccess {
  case ids => for (id <- ids) println(id) // this is partial function
}
// can be invoked couple of times
f onSuccess {
  case ids => for (id <- ids) println(id) // this is partial function
}
```

- Functional Composition

```scala
import scala.concurrent._
import scala.util.{Success, Failure}
import ExecutionContext.Implicits.global

val f: Future[Seq[Long]] = future {
  // do some index query
  Thread.sleep(2000)
  Seq(49,59,29,60,20)
}

val f2 = f map { ids =>
  // do the cache query
  Thread.sleep(2000)
  for (id <- ids) yield {
    "Article" + id
  }
}

f2 onComplete {
  case Success(articles) => for(article <- articles) println (article)
  case Failure(t) => println("Error has occured:" + t.getMessage)
}
```

- For-Comprehensions

```scala
import scala.concurrent._
import scala.util.{Success, Failure}
import ExecutionContext.Implicits.global

val f1: Future[Seq[Long]] = future {
  // do some index query1
  Thread.sleep(2000)
  Seq(49,59,29,60,20)
}

val f2: Future[Seq[Long]] = future {
  // do some index query2
  Thread.sleep(2000)
  Seq(60,20,39,23)
}

val commonArticle = for(ids1 <- f1; ids2 <- f2) yield {
  // find the common set
  (ids1.toSet & ids2.toSet) map { i =>
    // do the cache query
    "Article" + i
  }
}

commonArticle onComplete {
  case Success(articles) => for(article <- articles) println (article)
  case Failure(t) => println("Error has occured:" + t.getMessage)
}
```

- [Akka](http://www.reactive.io/tips/2014/03/28/getting-started-with-actor-based-programming-using-scala-and-akka/)
Akka is part of the Scala standard library (the old actor is retired)


```scala
import akka.actor.{ActorLogging, ActorSystem, Props}

case class Ticket(quantity: Int)
case class FullPint(number: Int)
case class EmptyPint(number: Int)

class Person extends Actor with ActorLogging {
  def receive = {
    case FullPint(number) =>
      log.info(s"I'll make short work of pint $number")

      Thread.sleep(1000)

      log.info(s"Done, here is the empty glass for pint $number")

      sender ! EmptyPint(number)
  }
}

class BarTender extends Actor with ActorLogging {
  var total = 0

  def receive = {
    case Ticket(quantity) =>
      total = total + quantity

      log.info(s"I'll get $quantity pints for [" + sender.path + "]")

      for (number <- 1 to quantity) {
        log.info(s"Pint $number is coming right up for [" + sender.path + "]")

        Thread.sleep(1000)

        log.info(s"Pint $number is ready, here you go [" + sender.path + "]")

        sender ! FullPint(number)
      }

    case EmptyPint(number) =>
      total match {
        case 1 =>
          log.info("Ya'll drank those pints quick, time to close up shop")

          context.system.shutdown()

        case n =>
          total = total - 1

          log.info(s"You drank pint $number quick, but there are still $total pints left")
      }
  }
}

val system = ActorSystem("howdy-akka")

val zed = system.actorOf(Props(new BarTender), "zed")

val alice   = system.actorOf(Props(new Person), "alice")
val bob     = system.actorOf(Props(new Person), "bob")
val charlie = system.actorOf(Props(new Person), "charlie")

zed.tell(Ticket(2), alice)
zed.tell(Ticket(3), bob)
zed.tell(Ticket(1), charlie)

system.awaitTermination()
```


