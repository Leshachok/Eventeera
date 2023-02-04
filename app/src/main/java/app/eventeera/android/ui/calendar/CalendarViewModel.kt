package app.eventeera.android.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.eventeera.android.data.model.Event
import app.eventeera.android.data.model.EventType
import app.eventeera.android.data.repository.CalendarRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CalendarRepository()

    private val eventDateFlow = MutableStateFlow<LocalDate>(LocalDate.now())
    private val eventTypeFlow = MutableStateFlow<String?>(null)

    val date = eventDateFlow.asLiveData()

    private val TAG = "CalendarViewModel"

    val eventsFlow = eventDateFlow.combine(eventTypeFlow) { date, type ->
        println(date)
        println(type)
        repository.getEvents(date, type)
    }

    fun setDate(date: LocalDate){
        viewModelScope.launch {
            eventDateFlow.emit(date)
        }
    }

    fun filterByType(eventType: String?){
        viewModelScope.launch {
            eventTypeFlow.emit(eventType)
        }
    }

    fun previousDate(){
        viewModelScope.launch {
            val date = eventDateFlow.value
            eventDateFlow.emit(date.minusDays(1))
        }
    }

    fun nextDate(){
        viewModelScope.launch {
            val date = eventDateFlow.value
            eventDateFlow.emit(date.plusDays(1))
        }
    }

    fun addEvent(event: Event){
        repository.addEvent(event)
    }

    fun editEvent(event: Event) {
        repository.editEvent(event)
    }

    fun removeEvent(id: Long) {
        repository.removeEvent(id)
    }

}