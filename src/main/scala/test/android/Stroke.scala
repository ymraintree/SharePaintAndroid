package test.android

import scala.collection.mutable.ListBuffer

class Stroke {
	var strokeId:String =_
	var canvasId:String = _
	var clientTime:Long = _
	var serverTime:Long = _
	var userId:String = _
	var penProperties:PenProperties = _
	var layer:Int = _
	var xArray:ListBuffer[Int] = _
	var yArray:ListBuffer[Int] = _
	
	def this(str:String) {
		this()
		setupVariables
		xArray = new ListBuffer[Int]
		yArray = new ListBuffer[Int]
		val tagPoint:Array[String] = str.split(" points:")
		val keyVals:Array[String] = tagPoint(0).split("\\s")
		keyVals.foreach { (k) =>
			val keyVal = k.split(":")
			if (keyVal(0) == "clientTime") clientTime = java.lang.Long.parseLong(keyVal(1))
			else if (keyVal(0) == "serverTime") serverTime = java.lang.Long.parseLong(keyVal(1))
			else if (keyVal(0) == "userId") userId = keyVal(1)
			else if (keyVal(0) == "penProperties") penProperties = new PenProperties(keyVal(1))
			else if (keyVal(0) == "layer") layer = java.lang.Integer.parseInt(keyVal(1))
		}
		val xys:Array[String] = tagPoint(1).split("\\s")
		xys.foreach { (x) =>
			val xy = x.split("-")
			if (xy.size == 2) {
				xArray += java.lang.Integer.parseInt(xy(0))
				yArray += java.lang.Integer.parseInt(xy(1))
			}
		}
		
	}
	
	private def setupVariables {
		clientTime = 0
		serverTime = 0
		userId = null
		penProperties = null
		layer = 0
	}
	
	override def toString = {
		var result = 
			"strokeId:" + userId + "@" + clientTime +
			" clientTime:" + clientTime +
			" userId:" + userId + 
			" penProperties:" + penProperties + 
			" layer:" + layer +
			" points:"
		for (i <- 0 to xArray.size - 1) result += xArray(i) + "-" + yArray(i) + " "
		result
	}
}