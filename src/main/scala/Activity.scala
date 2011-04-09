package test.android

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.widget._
import _root_.android.view._
import _root_.android.view.View._
import _root_.android.content._

object MainActivity {
	val ShowPenPropertiesId = 0
	var canvasView:CanvasView = _
}

class MainActivity extends Activity with TypedActivity {
	
  override def onCreate(savedInstanceState: Bundle) {
	  super.onCreate(savedInstanceState)
      setContentView(R.layout.main)
//    setContentView(new TextView(this) {
//      setText("hello, world, hello scala!")
//    })
    
      MainActivity.canvasView = findView(TR.canvas_view)
//    val penPropBtn:Button = findViewById(R.id.pen_prop_btn).asInstanceOf[Button]
//    penPropBtn.setOnClickListener(() => {
//    	val intent = new Intent(MainActivity.this, classOf[PenSettingsActivity])
//    	startActivityForResult(intent, MainActivity.ShowPenPropertiesId)
//    })
      findView(TR.pen_prop_btn).setOnClickListener(
    	  new OnClickListener { def onClick(v: View) {
    		  val intent = new Intent(MainActivity.this, classOf[PenSettingsActivity])
    		  startActivityForResult(intent, MainActivity.ShowPenPropertiesId)
    	  }}
      )
  }
}

class PenSettingsActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
      setContentView(R.layout.pen_properties);
  }
}