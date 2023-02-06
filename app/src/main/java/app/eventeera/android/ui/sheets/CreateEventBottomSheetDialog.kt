package app.eventeera.android.ui.sheets

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.eventeera.android.R
import app.eventeera.android.data.model.ContactInvite
import app.eventeera.android.data.model.Event
import app.eventeera.android.data.model.EventType
import app.eventeera.android.data.repository.ContactRepository
import app.eventeera.android.databinding.BottomsheetEventCreateBinding
import app.eventeera.android.ui.adapter.FriendAdapter
import app.eventeera.android.util.EventManager
import app.eventeera.android.util.formatTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class CreateEventBottomSheetDialog(private val eventCreator: EventManager) : BottomSheetDialogFragment(R.layout.bottomsheet_event_create) {

    private var _binding: BottomsheetEventCreateBinding? = null
    private val binding get() = _binding!!

    private val invites = mutableListOf<ContactInvite>()

    var title: String? = null
    var date: String? = null
    var startTime: String? = null
    var endTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetEventCreateBinding.inflate(layoutInflater)

        val repository = ContactRepository.Instance
        val contacts = repository.getContacts()

        invites.addAll(contacts.map { contact -> ContactInvite(contact) })

        val friendAdapter = FriendAdapter(
            invites,
            requireContext()
        )

        binding.friendsRecyclerView.apply {
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

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

        return binding.root
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
        val id = (Math.random() * 1_000_000).toLong()

        val invitedContacts = invites.filter { it.isInvited }.map { it.contact }
        val event = Event(
            id,
            title = title!!,
            type = type,
            timeStamp =  date!!,
            startTime = startTime!!,
            endTime = endTime!!,
            invitedContacts = invitedContacts
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
        picker.show(parentFragmentManager, if(start) "timePickerStart" else "timePickerEnd")
    }

    private fun showDatePicker(){
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now()).build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Оберіть дату!")
        datePicker.setCalendarConstraints(constraintsBuilder)

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