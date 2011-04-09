package test.android

class PenProperties(var color:Int, var width:Int, var density:Int) {
//	var color:Int = _
//	var width:Int = _
//	var density:Int = _
	
	def this() = this(0xff000000, 3, 128)
	
//	def this(c:Int, w:Int, d:Int) = {
//		this()
//		this.color = c
//		this.width = w
//		this.density = d
//	}
}