package test.android
import android.app.Activity
import android.view.View

case class TypedResource[T](id: Int)
object TR {
  val pencolor_black = TypedResource[android.widget.RadioButton](R.id.pencolor_black)
  val pencolor_blue = TypedResource[android.widget.RadioButton](R.id.pencolor_blue)
  val pen_prop_cancel_btn = TypedResource[android.widget.Button](R.id.pen_prop_cancel_btn)
  val pencolor_green = TypedResource[android.widget.RadioButton](R.id.pencolor_green)
  val pen_prop_ok_btn = TypedResource[android.widget.Button](R.id.pen_prop_ok_btn)
  val undo_btn = TypedResource[android.widget.Button](R.id.undo_btn)
  val layer_2 = TypedResource[android.widget.RadioButton](R.id.layer_2)
  val pencolor_white = TypedResource[android.widget.RadioButton](R.id.pencolor_white)
  val layer_1 = TypedResource[android.widget.RadioButton](R.id.layer_1)
  val pen_width = TypedResource[android.widget.SeekBar](R.id.pen_width)
  val layer_0 = TypedResource[android.widget.RadioButton](R.id.layer_0)
  val canvas_view = TypedResource[test.android.CanvasView](R.id.canvas_view)
  val pencolor_red = TypedResource[android.widget.RadioButton](R.id.pencolor_red)
  val pen_density = TypedResource[android.widget.SeekBar](R.id.pen_density)
  val pen_prop_btn = TypedResource[android.widget.Button](R.id.pen_prop_btn)
}
trait TypedViewHolder {
  def view: View
  def findView[T](tr: TypedResource[T]) = view.findViewById(tr.id).asInstanceOf[T]  
}
trait TypedView extends View with TypedViewHolder { def view = this }
trait TypedActivityHolder {
  def activity: Activity
  def findView[T](tr: TypedResource[T]) = activity.findViewById(tr.id).asInstanceOf[T]
}
trait TypedActivity extends Activity with TypedActivityHolder { def activity = this }
object TypedResource {
  implicit def view2typed(v: View) = new TypedViewHolder { def view = v }
  implicit def activity2typed(act: Activity) = new TypedActivityHolder { def activity = act }
}
