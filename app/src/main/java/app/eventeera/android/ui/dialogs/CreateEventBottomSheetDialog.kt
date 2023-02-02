package app.eventeera.android.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import app.eventeera.android.R
import app.eventeera.android.data.model.Event
import app.eventeera.android.data.model.EventType
import app.eventeera.android.databinding.BottomsheetEventCreateBinding
import app.eventeera.android.ui.calendar.EventCreator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class CreateEventBottomSheetDialog(context: Context, private val fragmentManager: FragmentManager, val eventCreator: EventCreator) : BottomSheetDialog(context) {

    private var _binding: BottomsheetEventCreateBinding? = null
    private val binding get() = _binding!!

    var title: String? = null
    var date: String? = null
    var startTime: String? = null
    var endTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = BottomsheetEventCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.spinner.adapter = ArrayAdapter(
            context,
            R.layout.event_type_spinner_item,
            EventType.entries
        )

        binding.eventDate.setOnClickListener {
            showDatePicker()
        }

        binding.eventStartTime.setOnClickListener {
            showTimePicker(true)
        }

        binding.eventEndTime.setOnClickListener {
            showTimePicker(false)
        }

        binding.buttonCreateEvent.setOnClickListener {
            checkFields()
        }
    }

    private fun checkFields() {
        val title = binding.eventTitleEditText.text
        if(title.isNullOrEmpty()){
            Toast.makeText(context, "Заповніть назву.", Toast.LENGTH_SHORT).show()
            return
        }
        if(null in listOf(date, startTime, endTime)){
            Toast.makeText(context, "Заповніть інформацію про захід.", Toast.LENGTH_SHORT).show()
            return
        }
        if(startTime!! > endTime!!){
            Toast.makeText(context, "Некоректний час.", Toast.LENGTH_SHORT).show()
            return
        }
        this.title = title.toString()
        createEvent()
    }

    private fun createEvent() {
        val type = binding.spinner.selectedItem.toString()
        val event = Event(
            title = title!!,
            type = type,
            timeStamp =  date!!,
            startTime = startTime!!,
            endTime = endTime!!
        )
        eventCreator.onEventCreated(event)
        dismiss()
    }

    private fun showTimePicker(start: Boolean){
        val timePicker = MaterialTimePicker.Builder()
        timePicker.setTitleText("Оберіть час ${if(start) "початку" else "кінця"}!")

        val picker = timePicker.build()
        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val time = "${hour.formatTime()}:${minute.formatTime()}"
            if(start) {
                binding.textStartTime.text = time
                startTime = time
            }
            else {
                binding.textEndTime.text = time
                endTime = time
            }
        }
        picker.show(fragmentManager, if(start) "timePickerStart" else "timePickerEnd")
    }

    private fun showDatePicker(){
        val datePicker = MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Оберіть дату!")
        val picker = datePicker.build()
        picker.addOnPositiveButtonClickListener {
            handleSelectedDate(it)
        }
        picker.show(fragmentManager, "datePicker")
    }

    private fun handleSelectedDate(epoch: Long?) {
        epoch?.let {
            val date = Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            this.date = date.format(DateTimeFormatter.ISO_DATE)

            val uaDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.forLanguageTag("uk")) // 2
            val uaFormattedDate = date.format(uaDateFormatter)

            binding.textDate.text = uaFormattedDate
        }
    }
}

fun Int.formatTime(): String{
    return if (this < 10) "0$this" else "$this"
}