package test.android

import _root_.android.os._
import java.net.HttpURLConnection
import java.net.URL
import java.io._
import scala.collection.mutable._

object ServerAgent {
	val serverUrl = "http://masaoyano-sharepaint.appspot.com/MainServlet"
	val timeout:Long = 10 * 1000

	val None = 1
	val Strokes = 1
	val Refresh = 2
}
class ServerAgent(paramCanvas:CanvasView) {
	var connection:HTTPConnectDelegate = null
	var requesting:Boolean = false
	var lastRequestTime:Long = 0

	var queuedCommands = new ListBuffer[String]
	var returnedStrokes = new ListBuffer[Stroke]
	var canvasShouldBeRefreshed:Boolean = false
	var canvasList = new ListBuffer[String]
	
	var handler:Handler = new Handler
	var canvas:CanvasView = paramCanvas

	def append(stroke:Stroke) {
		val cmd = canvas.canvasId + ": append: " + stroke.toString
		queue(cmd)
	}
	
	def queue(cmd:String) {
		queuedCommands.synchronized {
			queuedCommands += cmd
		}
		
		if (requesting) {
			if (lastRequestTime + ServerAgent.timeout < System.currentTimeMillis) {
				connection.httpConnect.disconnect
				clearConnection
			} else {
				return
			}
		}
		sendQueuedCommand
	}
	
	def dequeue { 
		queuedCommands.remove(0) 
	}
	
	def sendQueuedCommand {
		queuedCommands.synchronized {
			if (! queuedCommands.isEmpty)
				sendCommand(queuedCommands(0), false)
		}
	}
	
	def sendCommand(cmd:String, synchronous:Boolean) {
		if (synchronous) {
			val connect:HTTPConnectDelegate = new HTTPConnectDelegate(null, this)
			var strs:Array[String] = connect.sendCommandAndGetResponse(cmd)
			if (strs != null) connect.connectionDidiFinishLoading(strs)
		} else {
			connection = new HTTPConnectDelegate(cmd, this)
			requesting = true
			lastRequestTime = System.currentTimeMillis
			connection.start
		}
	}
	
	def clearConnection {
		if (connection != null) connection.httpConnect.disconnect
		connection = null
		requesting = false
		lastRequestTime = 0
	}
	
	def getResponse:Int = {
		if (canvasShouldBeRefreshed) return ServerAgent.Refresh
		else if (! returnedStrokes.isEmpty) return ServerAgent.Strokes
		return ServerAgent.None
	}

}

class HTTPConnectDelegate extends Thread {
	var httpConnect:HttpURLConnection = _
	var agent:ServerAgent = _
	var command:String = _
	
	def this(cmd:String, agent:ServerAgent) = {
		this()
		command = cmd
		this.agent = agent
	}
	
	override def run {
		println("run start")
		val response:Array[String] = sendCommandAndGetResponse(command)
		println("run 1 " + response == null)
		if (response == null) connectionDidFailWithError
		else connectionDidiFinishLoading(response)
	}
	
	def sendCommandAndGetResponse(cmd:String):Array[String] = {
		println("sendCommandAndGetResponse start " + cmd)
		val response = new ListBuffer[String]
		try {
			val url:URL = new URL(ServerAgent.serverUrl)
			httpConnect = url.openConnection.asInstanceOf[HttpURLConnection]
			httpConnect.setUseCaches(false)
			httpConnect.setRequestMethod("POST")
			httpConnect.setReadTimeout(ServerAgent.timeout.asInstanceOf[Int])
			httpConnect.setDoInput(true)
			httpConnect.setDoOutput(true)
			httpConnect.connect

			val writer:PrintStream = new PrintStream(httpConnect.getOutputStream)
			writer.print("params=" + cmd)
			writer.flush
			writer.close

			val reader:InputStreamReader = new InputStreamReader(httpConnect.getInputStream)
			val bufReader = new BufferedReader(reader)
			var line:String = null
			while ({ line = bufReader.readLine; line != null }) response += line
			bufReader.close
			reader.close
		} catch {
			case e:Exception => {
				httpConnect.disconnect
				return null
			}
		}
		response.toArray
	}
	
	def connectionDidiFinishLoading(strs:Array[String]) {
		println("connectionDidiFinishLoading start")
		agent.clearConnection
		
		agent.returnedStrokes.synchronized {
			strs.foreach { (line) =>
				if (line.contains(" append: ")) {
					val stroke:Stroke = new Stroke(line)
					agent.returnedStrokes += stroke
				} else if (line.contains(" delete: ")) {
					agent.canvasShouldBeRefreshed = true
				}
			}
		}
		agent.handler.post(new Runnable {
			def run { agent.canvas.getResponse }
		})
		agent.dequeue
		agent.sendQueuedCommand
	}
	
	def connectionDidFailWithError {
//		agent.dequeue
		agent.clearConnection
	}
	
}