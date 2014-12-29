package com.marcosgarciacasado.ssbatch

import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator

/**
 * Kyro Serialization for the created classes.
 * 
 * @author Marcos García
 *
 */
class SSBatchKyroRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[AggregatedMeasure])
    kryo.register(classOf[AggregatedTempValue])
  }
}