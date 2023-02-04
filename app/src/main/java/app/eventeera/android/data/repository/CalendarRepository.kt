package app.eventeera.android.data.repository

import app.eventeera.android.data.model.Event
import app.eventeera.android.data.model.EventType
import app.eventeera.android.util.pattern
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarRepository() {

    private val events = mutableListOf<Event>()

    fun getEvents(date: LocalDate, type: String?): List<Event>{
        return events.filter { event ->
            val eventTime = LocalDate.parse(event.timeStamp, pattern)
            date.dayOfYear == eventTime.dayOfYear && (type.isNullOrEmpty() || type == event.type)
        }.sortedBy { it.startTime }
    }

    fun addEvent(event: Event) {
        events.add(event)
    }

    fun editEvent(event: Event) {
        val id = event.id
        val eventToEdit = events.firstOrNull {
            it.id == id
        }
        eventToEdit?.apply {
            title = event.title
            type = event.type
            startTime = event.startTime
            endTime = event.endTime
            timeStamp = event.timeStamp
        }
        println(eventToEdit)
    }

    fun removeEvent(id: Long) {
        events.removeIf { it.id == id }
    }

    init {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val tomorrow2 = tomorrow.plusDays(1)
        events.addAll(
            listOf(
                Event(1L, "Їмо борщ", EventType.REST, tomorrow2.format(DateTimeFormatter.ISO_DATE), "10:40", "11:00"),
                Event(2L, "Їдемо у Львів", EventType.REST, today.format(DateTimeFormatter.ISO_DATE), "20:40", "20:45"),
                Event(3L, "Слухаємо рок", EventType.ACTIVITY, tomorrow.format(DateTimeFormatter.ISO_DATE), "13:12", "13:22"),
                Event(4L, "П'ємо воду", EventType.WORK, today.format(DateTimeFormatter.ISO_DATE), "11:44", "15:45"),
                Event(5L, "Відпочиваємо", EventType.REST, tomorrow.format(DateTimeFormatter.ISO_DATE), "08:12", "09:00"),
            )

        )
    }
}