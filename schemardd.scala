import org.apache.spark.sql.hive.HiveContext

case class KV(key: Int, value: String)

val kvRdd = sc.parallelize((1 to 100).map(i => KV(i, s"val_$i")))
val hc = new HiveContext(sc)
import hc._

kvRdd.where('key >= 1).saveAsParquetFile("/tmp/kv_parquet")
kvRdd.where('key >= 1).where('key <=5).registerTempTable("kv_rdd")

parquetFile("/tmp/kv_parquet").registerTempTable("kv_parquet")

val result = sql("SELECT a.key, b.value, c.key from kv_rdd a join kv_parquet b join src c limit 20")

result.collect().foreach(row => {
  val f0 = if(row.isNullAt(0)) "null" else row.getInt(0)
  val f1 = if(row.isNullAt(1)) "null" else row.getString(1)
  val f2 = if(row.isNullAt(2)) "null" else row.getInt(2)
  println(s"result:$f0, $f1, $f2")
})

