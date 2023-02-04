package app.eventeera.android.util

fun Int.formatTime(): String{
    return if (this < 10) "0$this" else "$this"
}