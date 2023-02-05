package app.eventeera.android.ui.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import app.eventeera.android.R
import app.eventeera.android.data.model.Event
import app.eventeera.android.data.model.EventType
import app.eventeera.android.databinding.BottomsheetEventEditBinding
import app.eventeera.android.util.EventManager
import app.eventeera.android.util.formatTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class EditEventBottomSheetDialog(
    private val eventManager: EventManager,
    private var event: Event
) : BottomSheetDialogFragment(R.layout.bottomsheet_event_edit) {

    private var _binding: BottomsheetEventEditBinding? = null
    private val binding get() = _binding!!

    var date: String? = null
    var startTime: String? = null
    var endTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetEventEditBinding.inflate(layoutInflater)

        binding.spinner.adapter = ArrayAdapter(
            requireContext(),
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

        binding.buttonDeleteEvent.setOnClickListener {
            dismiss()
            eventManager.onEventRemoved(event.id)
        }

        fillFields()

        return binding.root
    }

    private fun fillFields() {
        binding.eventTitleEditText.setText(event.title)

        val selectedItem = when(event.type){
            EventType.ACTIVITY -> 0
            EventType.MEETING -> 1
            EventType.REST -> 2
            EventType.WORK -> 3
            else -> 4
        }
        binding.spinner.setSelection(selectedItem)


        val timeStamp = event.timeStamp
        val localDate = LocalDate.parse(timeStamp)

        val uaDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.forLanguageTag("uk")) // 2
        val uaFormattedDate = localDate.format(uaDateFormatter)

        binding.textDate.text = uaFormattedDate
        binding.textStartTime.text = event.startTime
        binding.textEndTime.text = event.endTime
    }

    private fun checkFields() {
        val title = binding.eventTitleEditText.text

        if(title.isNullOrEmpty()){
            Toast.makeText(context, "Заповніть назву.", Toast.LENGTH_SHORT).show()
            return
        }

        startTime?.let { start ->
            endTime?.let { end ->
                if(start > end) {
                    Toast.makeText(context, "Некоректний час.", Toast.LENGTH_SHORT).show()
                    return
                }
            } ?: kotlin.run {
                if(start > event.endTime) {
                    Toast.makeText(context, "Некоректний час.", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        endTime?.let { end ->
            if(startTime == null){
                if(end < event.startTime) {
                    Toast.makeText(context, "Некоректний час.", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        editEvent()
    }

    private fun editEvent() {
        val title = binding.eventTitleEditText.text.toString()

        if(title != event.title){
            event.title = title
        }

        startTime?.let {
            event.startTime = it
        }

        endTime?.let {
            event.endTime = it
        }

        date?.let {
            event.timeStamp = it
        }
        val type = binding.spinner.selectedItem.toString()
        if(type != event.type){
            event.type = type
        }

        dismiss()
        eventManager.onEventEdited(event)
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
        picker.show(parentFragmentManager, if(start) "timePickerStart" else "timePickerEnd")
    }

    private fun showDatePicker(){
        val datePicker = MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Оберіть дату!")
        val picker = datePicker.build()
        picker.addOnPositiveButtonClickListener {
            handleSelectedDate(it)
        }
        picker.show(parentFragmentManager, "datePicker")
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