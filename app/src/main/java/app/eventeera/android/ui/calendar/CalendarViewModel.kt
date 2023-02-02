package app.eventeera.android.ui.calendar

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.eventeera.android.data.model.Event
import app.eventeera.android.data.repository.CalendarRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.log

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CalendarRepository()

    val pattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())

    val date = selectedDate.asLiveData()

    private val TAG = "CalendarViewModel"

    val eventsFlow = selectedDate.flatMapLatest { date ->
        repository.events.filter { event ->
            val eventTime = LocalDate.parse(event.timeStamp, pattern)
            date.dayOfYear == eventTime.dayOfYear
        }.sortedBy { it.startTime }.asFlow()
    }

    fun previousDate(){
        viewModelScope.launch {
            val date = selectedDate.value
            selectedDate.emit(date.minusDays(1))
        }
    }

    fun nextDate(){
        viewModelScope.launch {
            val date = selectedDate.value
            selectedDate.emit(date.plusDays(1))
        }
    }

    fun addEvent(event: Event){
        repository.addEvent(event)
        viewModelScope.launch {
            val date = selectedDate.value
            selectedDate.emit(date)
        }
    }

}