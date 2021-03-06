package test.android

import java.lang.Math
import java.util.Vector
import java.util.Date

import android.view._
import android.graphics._
import android.content._
import android.util._
import scala.collection.immutable.TreeMap
import scala.collection.mutable.ListBuffer

class CanvasView(context:Context, attrs:AttributeSet) extends View(context, attrs) {
	val width = 480
	val height = 860
	var onStroke = false
	var penProperties = new PenProperties
	var imageBuffer:Option[Bitmap] = None
	var lastX, lastY:Int = _
	var layerIndex = 0
	val serverAgent = new ServerAgent(this)
	val userId = "testuser@" + System.currentTimeMillis
	clearCanvas

	var strokesHistory:Map[Long, Stroke] = new TreeMap[Long, Stroke]
	var xArray, yArray:ListBuffer[Int] = _
	
	def canvasId = "testCanvas"
		
	private def clearCanvas {
		imageBuffer match {
			case Some(n) => n.recycle
			case None => imageBuffer = Some(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
		}
		imageBuffer.get.eraseColor(0xffffffff)
		invalidate
	}
	
	override def onDraw(canvas:Canvas) {
		super.onDraw(canvas)
		if (imageBuffer != None) canvas.drawBitmap(imageBuffer.get, 0, 0, null);		
	}
	
	override def onTouchEvent(event:MotionEvent):Boolean = {
		if (imageBuffer == None) return false;
		val x = event.getX.asInstanceOf[Int]
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
		
		xArray = new ListBuffer[Int]
		yArray = new ListBuffer[Int]
		xArray += x
		yArray += y
	}
	
	private def touchDragged(x:Int, y:Int) {
		drawLine(lastX, lastY, x, y)
		val penWidthHalf:Int = penProperties.width / 2 + 1
		val minX = Math.min(x, lastX) - penWidthHalf
		val maxX = Math.max(x, lastX) + penWidthHalf
		val minY = Math.min(y, lastY) - penWidthHalf
		val maxY = Math.max(y, lastY) + penWidthHalf
		
		invalidate(new Rect(minX, minY, maxX, maxY))
		lastX = x
		lastY = y
		
		xArray += x
		yArray += y
	}

	private def touchReleased(x:Int, y:Int) { 
		var stroke = new Stroke
		stroke.userId = userId
		stroke.layer = layerIndex
		stroke.clientTime = (new Date).getTime
		stroke.strokeId = stroke.userId + "@" + stroke.clientTime
		stroke.penProperties = new PenProperties(penProperties)
		stroke.xArray = xArray
		stroke.yArray = yArray
		strokesHistory += (stroke.clientTime -> stroke)
		xArray = null
		yArray = null
		onStroke = false
		println(strokesHistory.size + " " + stroke);
		
		// サーバへ送信
		getResponse
		serverAgent.append(stroke)
	}

	private def drawLine(lastX2:Int, lastY2:Int, x:Int, y:Int) {
		val canvas:Canvas = new Canvas(imageBuffer.get)
		val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
		paint.setStrokeCap(Paint.Cap.ROUND)
		paint.setStrokeWidth(penProperties.width)
		paint.setColor(penProperties.color)
		canvas.drawLine(lastX2, lastY2, x, y, paint)
	}

	private def drawStroke(bitmap:Bitmap, stroke:Stroke) {
		println("stroke.xArray.size=" + stroke.xArray.size)
//		println("stroke.xArray.get(0)=" + stroke.xArray.get(0))
		if (stroke.xArray.size == 0) return
		val canvas:Canvas = new Canvas(bitmap)
		
		val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
		paint.setStyle(Paint.Style.STROKE)
		paint.setStrokeCap(Paint.Cap.ROUND)
		paint.setStrokeJoin(Paint.Join.ROUND)
		println("stroke width="+stroke.penProperties.width+" color="+stroke.penProperties.color)
		paint.setStrokeWidth(stroke.penProperties.width)
		paint.setColor(stroke.penProperties.color)

		val path:Path = new Path
//		if (stroke.xArray.get(0) == null || stroke.yArray.get(0) == null) return
		path.moveTo(stroke.xArray(0), stroke.yArray(0))
		for (i <- 1 to stroke.xArray.size - 1)
			path.lineTo(stroke.xArray(i), stroke.yArray(i))
		canvas.drawPath(path, paint)
	}
		
	def undoOneStroke {
		println("undoOneStroke called")
		if (strokesHistory.isEmpty) return
		imageBuffer.get.eraseColor(0xffffffff)
		
		strokesHistory -= strokesHistory.keySet.last
		strokesHistory.values.foreach { (s) => drawStroke(imageBuffer.get, s) }
		
		invalidate
	}
	
	def getResponse {
		
		var shouldBeRefresh = false
//		clearCanvas
		serverAgent.returnedStrokes.foreach((s) => drawStroke(imageBuffer.get, s))
		invalidate
	}

}