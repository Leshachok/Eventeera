package app.eventeera.android.data.model

data class Event(
    val id: Long,
    var title: String,
    var type: String,
    var timeStamp: String,
    var startTime: String,
    var endTime: String
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