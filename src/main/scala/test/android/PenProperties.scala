package test.android

class PenProperties(var color:Int, var width:Int, var density:Int) {
//	var color:Int = _
//	var width:Int = _
//	var density:Int = _
	
	def this() = this(0xff000000, 3, 128)
	def this(penProperties:PenProperties) = this(penProperties.color, penProperties.width, penProperties.density)
	
	def this(str:String) = {
		this()
		val strs = str.split("-")
		color = strs(0).asInstanceOf[Int]
		width = strs(1).asInstanceOf[Int]
		density = strs(2).asInstanceOf[Int]
	}
	
	override def toString = {
		color + "+" + width + "+" + density
	}
//	def this(c:Int, w:Int, d:Int) = {
//		this()
//		this.color = c
//		this.width = w
//		this.density = d
//	}
}