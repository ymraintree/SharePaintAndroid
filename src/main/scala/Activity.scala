package test.android

import _root_.android.app.Activity
import _root_.android.app.Activity._
import _root_.android.os.Bundle
import _root_.android.widget._
import _root_.android.view._
import _root_.android.view.View._
import _root_.android.content._
import ConverterHelper._


object MainActivity {
	val ShowPenPropertiesId = 0
	var canvasView:CanvasView = _
	val PenWidthName = "PenWidth"
	val PenColorName = "PenColor"
}

class MainActivity extends Activity with TypedActivity {
	
    override def onCreate(savedInstanceState: Bundle) {
    	super.onCreate(savedInstanceState)
    	setContentView(R.layout.main)
    
    	MainActivity.canvasView = findView(TR.canvas_view)
    	findView(TR.pen_prop_btn).setOnClickListener( () => {
    			val intent = new Intent(MainActivity.this, classOf[PenSettingsActivity])
    			intent.putExtra(MainActivity.PenWidthName, MainActivity.canvasView.penProperties.width)
    			intent.putExtra(MainActivity.PenColorName, MainActivity.canvasView.penProperties.color)
    			startActivityForResult(intent, MainActivity.ShowPenPropertiesId)
    	})
    	findView(TR.undo_btn).setOnClickListener( () => MainActivity.canvasView.undoOneStroke )
    }
    
    override def onActivityResult(reqId:Int, result:Int, intent:Intent) {
    	if (reqId == MainActivity.ShowPenPropertiesId && result == RESULT_OK) {
    		val penWidth = intent.getIntExtra(MainActivity.PenWidthName, -1)
    		if (0 <= penWidth) MainActivity.canvasView.penProperties.width = penWidth
    		val penColor = intent.getIntExtra(MainActivity.PenColorName, 0)
    		if (penColor != 0) MainActivity.canvasView.penProperties.color = penColor
    	}
    }
}

class PenSettingsActivity extends Activity with TypedActivity {
	var penWidthSlider:SeekBar = _
	val penColorBtns:Array[RadioButton] = new Array(5)
	
	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.pen_properties)
		
		penWidthSlider = findView(TR.pen_width)
		penColorBtns(0) = findView(TR.pencolor_black)
		penColorBtns(1) = findView(TR.pencolor_white)
		penColorBtns(2) = findView(TR.pencolor_red)
		penColorBtns(3) = findView(TR.pencolor_green)
		penColorBtns(4) = findView(TR.pencolor_blue)
		
		val penWidth = getIntent.getIntExtra(MainActivity.PenWidthName, -1)
    	if (0 <= penWidth) penWidthSlider.setProgress(penWidth)
		val penColor = getIntent.getIntExtra(MainActivity.PenColorName, 0)
    	if (penColor != 0) setColorButton(penColor)
    	
    	findView(TR.pen_prop_ok_btn).setOnClickListener( () => okButtonPressed )
    	findView(TR.pen_prop_cancel_btn).setOnClickListener( () => {
    		setResult(RESULT_CANCELED)
    		finish
    	})
	}
	
	private def setColorButton(col:Int) {
		col match {
			case 0xff000000 => penColorBtns(0).setChecked(true)
			case 0xffffffff => penColorBtns(1).setChecked(true)
			case 0xffff0000 => penColorBtns(2).setChecked(true)
			case 0xff00ff00 => penColorBtns(3).setChecked(true)
			case 0xff0000ff => penColorBtns(4).setChecked(true)
		}
	}
	
	private def okButtonPressed {
		val intent:Intent = new Intent
		var penColor:Option[Int] = None
		if (penColorBtns(0).isChecked) penColor = Some(0xff000000)
		if (penColorBtns(1).isChecked) penColor = Some(0xffffffff)
		if (penColorBtns(2).isChecked) penColor = Some(0xffff0000)
		if (penColorBtns(3).isChecked) penColor = Some(0xff00ff00)
		if (penColorBtns(4).isChecked) penColor = Some(0xff0000ff)
		if (penColor.isDefined) intent.putExtra(MainActivity.PenColorName, penColor.get)
		
		intent.putExtra(MainActivity.PenWidthName, penWidthSlider.getProgress)

    	setResult(RESULT_OK, intent)
    	finish
	}

}

object ConverterHelper{
  import android.view.View.OnClickListener
  implicit def funcToClicker(f:View => Unit):OnClickListener = 
    new OnClickListener(){ def onClick(v:View)=f.apply(v)}
  implicit def funcToClicker0(f:() => Unit):OnClickListener = 
    new OnClickListener() { def onClick(v:View)=f.apply}
}