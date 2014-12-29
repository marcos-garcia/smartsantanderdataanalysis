package com.marcosgarciacasado.ssbatch


/**
 * Value aggregation class of sum and count operations.
 * 
 * @author Marcos García Casado
 *
 */
class AggregatedTempValue (sumc : Double, countc : Double) extends Equals{
  
    override def toString() = ""+(sum/count)
  
	/**
	 * Sum value. 
	 */
	var sum : Double = sumc
	
	/**
	 * Number of values.
	 */
	var count : Double = countc

  def canEqual(other: Any) = {
      other.isInstanceOf[com.marcosgarciacasado.ssbatch.AggregatedTempValue]
    }
    
    
 /**
  * Gets the mean of the value stored.
  * 
  * @return the mean value.
  */
def getMean() = {
      sum / count
    }

  override def equals(other: Any) = {
      other match {
        case that: com.marcosgarciacasado.ssbatch.AggregatedTempValue => 
          that.canEqual(AggregatedTempValue.this) && 
          that.sum == this.sum && 
          that.count == this.count 
        case _ => false
      }
    }

  override def hashCode() = {
      (""+sum+","+count).hashCode()
    }

}