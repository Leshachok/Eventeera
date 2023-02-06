package app.eventeera.android.util

import android.content.res.Resources
import android.util.TypedValue

fun Int.formatTime(): String{
    return if (this < 10) "0$this" else "$this"
}

val Int.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics).toInt()