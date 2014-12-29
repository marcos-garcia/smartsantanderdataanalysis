package com.marcosgarciacasado.ssbatch

import org.joda.time.DateTime

/**
 * Key class for the quarter of hour measure aggregation.
 * 
 * @author Marcos García Casado
 *
 */
class AggregatedMeasure (latitudec : String, longitudec : String, requestTimec : String, measurec : String) extends Serializable with Equals {

    override def toString() = measure+","+requestTime+","+latitude+","+longitude
  
    /**
     * Latitude coordinate of the sensor. 
     */
    var latitude : String = latitudec
	
    /**
	 * Longitude coordinate of the sensor.
	 */
	var longitude : String = longitudec
	
	/**
	 * Time of the measure value extraction.
	 */
	var requestTime : String = requestTimec
	
	/**
	 * Name of the magnitude.
	 */
	var measure : String = measurec

  def canEqual(other: Any) = {
      other.isInstanceOf[com.marcosgarciacasado.ssbatch.AggregatedMeasure]
    }

  override def equals(other: Any) = {
      other match {
        case that: com.marcosgarciacasado.ssbatch.AggregatedMeasure => 
          that.canEqual(AggregatedMeasure.this) && 
          that.latitude  == this.latitude  && 
          that.longitude == this.longitude && 
          that.requestTime  == this.requestTime  && 
          that.measure == this.measure 
        case _ => false
      }
    }

  override def hashCode() = {
      toString().hashCode()
    }
	
}