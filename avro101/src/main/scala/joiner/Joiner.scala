package joiner

import joiner.AgnosticJoiner.{HashMapJoiner, IterableJoiner, KMaterialized, KafkaStreamsJoiner}
import org.apache.kafka.streams.scala.ByteArrayKeyValueStore
import org.apache.kafka.streams.scala.kstream.KTable
import org.apache.kafka.streams.scala.kstream.Materialized

/** 1. MAIN ABSTRACTION */
trait AgnosticJoiner[AS[_, _], K1, V3] {

  def join[K2, V1, V2](
      keyExtractor: V1 => K2,
      combiner: (V1, V2) => V3
  )(
      as: AS[K1, V1],
      bs: AS[K2, V2]
  ): AS[K1, V3]

}

/** 2. DATA STRUCTURE SIMILAR TO KTABLE[K, V] */
case class KTableT[K, V](items: Iterable[(K, V)])

object AgnosticJoiner {

  /** 3. PLACE WHERE WE DECOUPLE IMPLEMENTATION */
  def join[AS[_, _], K1, V1, K2, V2, V3](
      keyExtractor: V1 => K2,
      combiner: (V1, V2) => V3
  )(
      as: AS[K1, V1],
      bs: AS[K2, V2]
  )(implicit impl: AgnosticJoiner[AS, K1, V3]): AS[K1, V3] =
    impl.join(keyExtractor, combiner)(as, bs)

  type KMaterialized[K1, V3] = Materialized[K1, V3, ByteArrayKeyValueStore]

  class KafkaStreamsJoiner[K1, V3](materialized: KMaterialized[K1, V3]) extends AgnosticJoiner[KTable, K1, V3] {
    override def join[K2, V1, V2](keyExtractor: V1 => K2, combiner: (V1, V2) => V3)(
        as: KTable[K1, V1],
        bs: KTable[K2, V2]
    ): KTable[K1, V3] = as.leftJoin(bs, keyExtractor, combiner(_, _), materialized)
  }

  class IterableJoiner[K1, V3] extends AgnosticJoiner[KTableT, K1, V3] {
    override def join[K2, V1, V2](keyExtractor: V1 => K2, combiner: (V1, V2) => V3)(
        as: KTableT[K1, V1],
        bs: KTableT[K2, V2]
    ): KTableT[K1, V3] = KTableT(
      as.items.flatMap { case (k1, v1) =>
        bs.items
          .filter { case (k2, _) => k2 == keyExtractor(v1) }
          .map { case (_, v2) => k1 -> combiner(v1, v2) }
      }
    )
  }

  class HashMapJoiner[K1, V3] extends AgnosticJoiner[Map, K1, V3] {
    override def join[K2, V1, V2](keyExtractor: V1 => K2, combiner: (V1, V2) => V3)(as: Map[K1, V1], bs: Map[K2, V2]): Map[K1, V3] = ???
  }

}

/** 4.1. INSTANCE TO DECLARE IN CODE */
object KafkaKTableInstances {
  implicit def ksJoiner[K1, V3](implicit km: KMaterialized[K1, V3]): AgnosticJoiner[KTable, K1, V3] = new KafkaStreamsJoiner(km)
}

/** 4.2. INSTANCE TO DECLARE IN TESTS */
object TestKTableTInstances {
  implicit def itJoiner[K1, V3]: AgnosticJoiner[KTableT, K1, V3] = new IterableJoiner[K1, V3]
}

/** 4.3. INSTANCE TO DECLARE IN TESTS */
object TestMapInstances {
  implicit def mJoiner[K1, V3]: AgnosticJoiner[Map, K1, V3] = new HashMapJoiner[K1, V3]
}

/** 5. BUSINESS LOGIC DECOUPLED */
object BusinessLogicDefinedAgnostically {

  /** V1 => K2 */
  val keyExtractor: ((String, Long)) => Long =
    (v1: (String, Long)) => v1._2

  /** (V1, V2) => V3 */
  val valueCombiner: ((String, Long), (String, Double)) => (String, String, Double) =
    (v1: (String, Long), v2: (String, Double)) => (v1._1, v2._1, v2._2)

}

/** 6.1 USE IN TESTS */
object UsingInTests extends App {
  import BusinessLogicDefinedAgnostically._
  import TestKTableTInstances._

  val t1: KTableT[Int, (String, Long)] = KTableT(
    Vector(
      (1, ("test 1st", 101L)),
      (2, ("test 2nd", 102L)),
      (3, ("test 3rd", 103L))
    )
  )

  val t2: KTableT[Long, (String, Double)] = KTableT(
    Vector(
      (101L, ("RED", 1001.1)),
      (102L, ("GREEN", 1002.2)),
      (103L, ("BLUE", 1003.3))
    )
  )

  val r: KTableT[Int, (String, String, Double)] =
    AgnosticJoiner.join(keyExtractor, valueCombiner)(t1, t2)

  r.items.foreach(println)
}

/** 6.2 USE WITH KAFKA STREAMS */
object UsingWithKafkaStreams {
  import BusinessLogicDefinedAgnostically._
  import KafkaKTableInstances._

  implicit val mat: KMaterialized[Int, (String, String, Double)] = ???
  val t1: KTable[Int, (String, Long)] = ???
  val t2: KTable[Long, (String, Double)] = ???

  val r: KTable[Int, (String, String, Double)] =
    AgnosticJoiner.join(keyExtractor, valueCombiner)(t1, t2)
}
