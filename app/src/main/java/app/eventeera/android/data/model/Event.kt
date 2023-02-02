package app.eventeera.android.data.model

data class Event(
    val title: String,
    val type: String,
    val timeStamp: String,
    val startTime: String,
    val endTime: String
)

class EventType {
    companion object {
        val ACTIVITY = "Активність"
        val MEETING = "Зустріч"
        val REST = "Відпочинок"
        val WORK = "Робота"
        val SPORT = "Спорт"

        val entries = listOf(ACTIVITY, MEETING, REST, WORK, SPORT)
    }
}