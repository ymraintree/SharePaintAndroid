package test.android

import java.util.Vector

class Stroke {
	var clientTime, serverTime:Long = _
	var userName:String = _
	var penProperties:PenProperties = _
	var layer:Int = _
	var xArray, yArray:Vector[Int] = _
	
	def this(str:String) {
		this()
		setupVariables
		xArray = new Vector
		yArray = new Vector
		val tagPoint:Array[String] = str.split(" points:")
		val keyVals:Array[String] = tagPoint(0).split("\\s")
		for (i <- 0 to keyVals.size) {
			val keyVal = keyVals(i).split(":")
			if (keyVal(0) == "clientTime") clientTime = keyVal(1).asInstanceOf[Long]
			else if (keyVal(0) == "serverTime") serverTime = keyVal(1).asInstanceOf[Long]
			else if (keyVal(0) == "penProperties") penProperties = new PenProperties(keyVal(1))
			else if (keyVal(0) == "userName") userName = keyVal(1)
		}
		val xys:Array[String] = tagPoint(1).split("\\s")
		for (i <- 0 to xys.size) {
			val xy = xys(i).split("-")
			if (xy.size == 2) {
				xArray.add(xy(0).asInstanceOf[Int])
				yArray.add(xy(1).asInstanceOf[Int])
			}
		}
		
	}
	
	private def setupVariables {
		clientTime = 0
		serverTime = 0
		layer = 0
		userName = null
		penProperties = null
	}
	
	override def toString = {
		var result = "clientTime:" + clientTime +
		" serverTime:" + serverTime + 
		" userName:" + userName + 
		" penProperties:" + penProperties + 
		" layer:" + layer +
		" points:"
		for (i <- 0 to xArray.size - 1) result += xArray.get(i) + "-" + yArray.get(i) + " "
		result
	}
}