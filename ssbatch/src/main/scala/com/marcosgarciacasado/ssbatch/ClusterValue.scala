package com.marcosgarciacasado.ssbatch


/**
 * Cluster aggregation value.
 * 
 * @author Marcos García Casado
 * @param cua Cuarter of hour of the day.
 * @param valor Value of the cuarter of hour.
 */
class ClusterValue (cua : String, valor : Double) extends Equals{
  
    override def toString() = quarter+":"+value
  
    
	/**
	 * Cuarter of hour of the day.
	 */
	var quarter : String = cua
	
	/**
	 * Value of the cuarter of hour. 
	 */
	var value : Double = valor

  def canEqual(other: Any) = {
      other.isInstanceOf[com.marcosgarciacasado.ssbatch.ClusterValue]
    }

  override def equals(other: Any) = {
      other match {
        case that: com.marcosgarciacasado.ssbatch.ClusterValue => 
          that.canEqual(ClusterValue.this) && 
          that.quarter.equals(this.quarter) && 
          that.value == this.value 
        case _ => false
      }
    }

  override def hashCode() = {
      (""+quarter+","+value).hashCode()
    }

}