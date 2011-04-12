package test.android

class PenProperties(var color:Int, var width:Int, var density:Int) {
//	var color:Int = _
//	var width:Int = _
//	var density:Int = _
	
	def this() = this(0xff000000, 3, 128)
	def this(penProperties:PenProperties) = this(penProperties.color, penProperties.width, penProperties.density)
	
	def this(str:String) = {
		this()
		val strs = str.split("_")
		color = java.lang.Integer.parseInt(strs(0))
		width = java.lang.Integer.parseInt(strs(1))
		density = java.lang.Integer.parseInt(strs(2))
	}
	
	override def toString = {
		color + "_" + width + "_" + density
	}
//	def this(c:Int, w:Int, d:Int) = {
//		this()
//		this.color = c
//		this.width = w
//		this.density = d
//	}
}