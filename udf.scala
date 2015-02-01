import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.SchemaRDD

case class KV(key: Int, value: String)

def printResult(result: SchemaRDD) {
  result.collect().foreach(row => {
     println (row.map { col => if (col == null) "null" else col.toString }.mkString(","))
  })
}

val hc = new HiveContext(sc)
import hc._

val kvRdd = sc.parallelize((1 to 100).map(i => KV(i, s"val_$i")))

val makeConcat = (a: Int, b: String) => s"$a => $b"
import org.apache.spark.sql.catalyst.dsl._
printResult(kvRdd.select('key, 'value, makeConcat.call('key, 'value)).limit(5))

registerFunction("MyConcat", makeConcat)
kvRdd.registerTempTable("test")
printResult(sql("SELECT key, value, MyConcat(key, value) from test limit 5"))


