package app.eventeera.android.data.repository

import app.eventeera.android.data.model.Event

class CalendarRepository {

    val events = mutableListOf(
        Event("Їмо борщ", "1", "2023-02-03 20:30:45", "10:40", "11:00"),
        Event("Їдемо у Львів", "1", "2022-02-02 08:30:45", "20:40", "20:45"),
        Event("Слухаємо рок", "1", "2022-02-02 20:30:45", "13:12", "13:22"),
        Event("П'ємо воду", "1", "2022-02-03 14:30:45", "11:44", "15:45"),
        Event("Відпочиваємо", "1", "2022-02-03 02:24:45", "08:12", "09:00"),
    )
}