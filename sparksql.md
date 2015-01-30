```sql
create table src(key int, value string);
load data inpath '/home/spark/spark/examples/src/main/resources/kv1.txt' overwrite into table src;
show tables;
select * from src limit 2;
describe extended src;
analyze table src compute statistics noscan;

set spark.sql.hive.version;
cache table src;
cache lazy table src2 as select * from src limit 30;

uncache table src;
uncache table src2;

select 1,2,3;
set spark.sql.dialect=sql;
select 1,2,3;
set spark.sql.dialect;
set spark.sql.dialect=hiveql;

create table back as select key, value from src limit 20;

set spark.sql.autoBroadcastJoinThreshold=500;
explain extended select a.key, b.key, a.value, b.value from src a left join back b on a.key=b.key;
explain extended select a.key, b.key, a.value, b.value from src a join back b on a.key=b.key;
explain extended select a.key, b.key, a.value, b.value from src a join back b on a.key=b.key where a.key>3;
explain extended select a.key, b.key, a.value, b.value from src a join back b on a.key=b.key where b.key>3;
explain extended select a.key, b.key, a.value, b.value from src a left join back b on a.key=b.key and a.key>3;
explain extended select a.key, b.key, a.value, b.value from src a left join back b on a.key=b.key and b.key>3;
```
