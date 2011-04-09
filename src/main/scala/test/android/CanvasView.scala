package test.android

import android.view._
import android.graphics._
import android.content._
import android.util._

class CanvasView(context:Context, attrs:AttributeSet) extends View(context, attrs) {
	
	val width = 480
	val height = 860
	var onStroke = false
	var penProperties = new PenProperties
	var imageBuffer:Option[Bitmap] = None
//	var imageBuffer:Bitmap = null
	var lastX, lastY:Int = _
	clearCanvas

	private def clearCanvas {
		imageBuffer match {
			case Some(n) => n.recycle
			case None => imageBuffer = Some(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
		}
//		if (imageBuffer != None) imageBuffer.get.recycle
//		imageBuffer = Some(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
		imageBuffer.get.eraseColor(0xffffffff)
		invalidate()
	}
	
	override def onDraw(canvas:Canvas) {
		super.onDraw(canvas)
		if (imageBuffer != None) canvas.drawBitmap(imageBuffer.get, 0, 0, null);		
	}
	
	override def onTouchEvent(event:MotionEvent):Boolean = {
		if (imageBuffer == None) return false;
		val x:Int = event.getX.asInstanceOf[Int]
		val y:Int = event.getY.asInstanceOf[Int]
		
		event.getAction match {
			case MotionEvent.ACTION_DOWN => touchPressed(x, y)
			case MotionEvent.ACTION_MOVE => touchDragged(x, y)
			case MotionEvent.ACTION_UP => touchReleased(x, y)
		}
		true
	}
	
	private def touchPressed(x:Int, y:Int) {
		lastX = x
		lastY = y
		onStroke = true
	}
	
	private def touchDragged(x:Int, y:Int) {
		drawLine(lastX, lastY, x, y)
		val penWidthHalf:Int = penProperties.width / 2 + 1
		val minX = Math.min(x, lastX) - penWidthHalf
		val maxX = Math.max(x, lastX) + penWidthHalf
		val minY = Math.min(y, lastY) - penWidthHalf
		val maxY = Math.max(y, lastY) - penWidthHalf
		
		invalidate(new Rect(minX, minY, maxX, maxY))
		lastX = x
		lastY = y
	}

	private def touchReleased(x:Int, y:Int) { onStroke = false }

	private def drawLine(lastX2:Int, lastY2:Int, x:Int, y:Int) {
		val canvas:Canvas = new Canvas(imageBuffer.get)
		val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
		paint.setStrokeCap(Paint.Cap.ROUND)
		paint.setStrokeWidth(penProperties.width)
		paint.setColor(penProperties.color)
		canvas.drawLine(lastX2, lastY2, x, y, paint)
	}

}