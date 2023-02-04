package app.eventeera.android.util

import app.eventeera.android.R
import app.eventeera.android.data.model.EventType

fun getChipColorByEventType(eventType: String): Int{
    return when(eventType){
        EventType.ACTIVITY -> {
            R.color.green01
        }
        EventType.MEETING -> {
            R.color.blue01
        }
        EventType.REST -> {
            R.color.blue02
        }
        EventType.WORK -> {
            R.color.orange01
        }
        else -> {
            R.color.red01
        }
    }
}

fun getChipBgColorByEventType(eventType: String): Int{
    return when(eventType){
        EventType.ACTIVITY -> {
            R.color.green01op
        }
        EventType.MEETING -> {
            R.color.blue01op
        }
        EventType.REST -> {
            R.color.blue02op
        }
        EventType.WORK -> {
            R.color.orange01op
        }
        else -> {
            R.color.red01op
        }
    }
}