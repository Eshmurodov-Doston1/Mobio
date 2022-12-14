package uz.idea.mobio.utils.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonElement
import uz.idea.mobio.R
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*


fun String?.isNotEmptyOrNull():Boolean{
    return this != null && this.isNotEmpty() && this != ""
}

fun View.enabled(){
    this.isEnabled = true
}

fun View.enabledFalse(){
    this.isEnabled = false
}

fun LogData(message:String){
    Log.e("ApplicationLog", message)
}

fun <T> JsonElement.parseClass(classData:Class<T>):T{
    val gson = Gson()
   return gson.fromJson(this,classData)
}

fun <T> Any.parseClass(classData:Class<T>):T{
    val gson = Gson()
    return gson.fromJson("$this",classData)
}

fun <A: Activity> Activity.startNewActivity(activity: Class<A>){
    Intent(this,activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(it)
    }
}

fun View.gone(){
    this.isVisible = false
}
fun View.visible(){
    this.isVisible = true
}


fun slideDown(view: View) {
    view.animate()
        .translationY(view.height.toFloat())
        .alpha(0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // superfluous restoration
                view.visibility = View.GONE
                view.alpha = 1f
                view.translationY = 0f
            }
        })
}

fun slideUp(view: View) {
    view.visibility = View.VISIBLE
    view.alpha = 0f
    if (view.height > 0) {
        slideUpNow(view)
    } else {
        // wait till height is measured
        view.post { slideUpNow(view) }
    }
}


private fun slideUpNow(view: View) {
    view.translationY = view.height.toFloat()
    view.animate()
        .translationY(0f)
        .alpha(1f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.VISIBLE
                view.alpha = 1f
            }
        })
}

fun queryCateGoryMap(page:Int,perPage:Int):HashMap<String,String>{
    val queryMap = HashMap<String,String>()
    queryMap["page"] = page.toString()
    queryMap["count"] = perPage.toString()
    return queryMap
}

fun queryNewMap(perPage:Int):HashMap<String,String>{
    val queryMap = HashMap<String,String>()
    queryMap["count"] = perPage.toString()
    return queryMap
}

fun Double.numberFormatter():String{
    val fmt = DecimalFormat()
    val fmts = DecimalFormatSymbols()
    fmts.groupingSeparator = ' '
    fmt.decimalFormatSymbols = fmts
    return fmt.format(this)
}

fun ImageView.imageData(url:String,context:Context){
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(context, R.color.circle_progress_color))
    Glide.with(context)
        .load(url)
        .placeholder(circularProgressDrawable)
        .into(this)
}