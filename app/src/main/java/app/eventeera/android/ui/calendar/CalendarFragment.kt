package app.eventeera.android.ui.calendar

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.eventeera.android.R
import app.eventeera.android.data.model.Event
import app.eventeera.android.data.model.EventType
import app.eventeera.android.databinding.FragmentCalendarBinding
import app.eventeera.android.ui.dialogs.CreateEventBottomSheetDialog
import app.eventeera.android.ui.dialogs.EditEventBottomSheetDialog
import app.eventeera.android.ui.dialogs.ViewEventBottomSheetDialog
import app.eventeera.android.util.EventManager
import app.eventeera.android.util.pattern
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class CalendarFragment : Fragment(), EventManager {

    private val viewModel: CalendarViewModel by viewModels()

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: EventAdapter
    private var events = mutableListOf<Event>()

    private var currentType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        adapter = EventAdapter(events, requireContext(), this)

        registerForContextMenu(binding.bg2)

        binding.recyclerView.apply {
            adapter = this@CalendarFragment.adapter
            this.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.createEventButton.setOnClickListener {
            onOpenCreateBottomSheet()
        }

        binding.arrowRight.isEnabled = true

        lifecycleScope.launch {
            viewModel.eventsFlow.collectLatest {
                events.clear()

                events.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }

        binding.arrowLeft.setOnClickListener {
            events.clear()
            adapter.notifyDataSetChanged()

            viewModel.previousDate()
        }

        binding.arrowRight.setOnClickListener {
            events.clear()
            adapter.notifyDataSetChanged()

            viewModel.nextDate()
        }

        binding.bg1.setOnClickListener {
            showDatePicker()
        }

        binding.bg2.setOnClickListener {
            it.showContextMenu()
        }

        viewModel.date.observe(viewLifecycleOwner) { date ->
            val uaDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.forLanguageTag("uk")) // 2
            val uaFormattedDate = date.format(uaDateFormatter)
            binding.dateText.text = uaFormattedDate

            binding.arrowLeft.isEnabled = date.dayOfYear != LocalDate.now().dayOfYear
        }

        return binding.root
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = requireActivity().menuInflater
        inflater.inflate(R.menu.filter_menu, menu)
        println(currentType)
        currentType?.let {
            val optionId = when (it) {
                EventType.ACTIVITY -> R.id.option_1
                EventType.MEETING -> R.id.option_2
                EventType.REST -> R.id.option_3
                EventType.WORK -> R.id.option_4
                else -> R.id.option_5
            }
            println(optionId)
            val selectedItem = menu.findItem(optionId)
            println(selectedItem)
            selectedItem.setChecked(true)
        }
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        val type = when (menuItem.itemId) {
            R.id.option_1 -> {
                if (menuItem.isChecked) null else EventType.ACTIVITY
            }
            R.id.option_2 -> {
                if (menuItem.isChecked) null else EventType.MEETING
            }
            R.id.option_3 -> {
                if (menuItem.isChecked) null else EventType.REST
            }
            R.id.option_4 -> {
                if (menuItem.isChecked) null else EventType.WORK
            }
            R.id.option_5 -> {
                if (menuItem.isChecked) null else EventType.SPORT
            }
            else -> null
        }
        menuItem.isChecked = !menuItem.isChecked

        this.currentType = type
        sortEventsByType(type)
        return super.onContextItemSelected(menuItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sortEventsByType(type: String?) {
        events.clear()
        adapter.notifyDataSetChanged()

        viewModel.filterByType(type)
    }

    private fun onOpenCreateBottomSheet() {
        val dialog = CreateEventBottomSheetDialog(requireContext(), parentFragmentManager, this)
        dialog.show()
    }


    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
        datePicker.setTitleText("Оберіть дату!")
        val picker = datePicker.build()
        picker.addOnPositiveButtonClickListener {
            events.clear()
            adapter.notifyDataSetChanged()

            handleSelectedDate(it)
        }
        picker.show(parentFragmentManager, "datePicker")
    }

    private fun handleSelectedDate(epoch: Long?) {
        epoch?.let {
            val date = Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            viewModel.setDate(date)
        }
    }

    private fun updateRecyclerView(event: Event){
        val eventToEdit = events.firstOrNull { it.id == event.id }
        eventToEdit?.let {
            val index = events.indexOf(it)

            it.apply {
                title = event.title
                timeStamp = event.timeStamp
                type = event.type
                startTime = event.startTime
                endTime = event.endTime
            }
            var needToRearrange = false

            if(index != 0){
                val previousEvent = events[index - 1]
                if(previousEvent.startTime > it.startTime) needToRearrange = true
            }
            if(events.size != index+1){
                val nextEvent = events[index + 1]
                if(nextEvent.startTime < it.startTime) needToRearrange = true
            }
            if(needToRearrange) {
                events.sortBy { event -> event.startTime }
                adapter.notifyDataSetChanged()
            }
            else adapter.notifyItemChanged(index)
        }
    }

    override fun onEventCreated(event: Event) {
        val currentDate = viewModel.date.value
        currentDate?.let {
            val eventDate = LocalDate.parse(event.timeStamp, pattern)
            if (eventDate.dayOfYear == currentDate.dayOfYear) {
                events.add(event)
                events.sortBy { it.startTime }
                adapter.notifyItemInserted(events.indexOf(event))
            }
        }

        viewModel.addEvent(event)
    }

    override fun onEventEdited(event: Event) {
        viewModel.editEvent(event)

        val date = LocalDate.parse(event.timeStamp)
        if(date.dayOfYear == LocalDate.now().dayOfYear){
            updateRecyclerView(event)
        }
    }

    override fun onEventRemoved(id: Long) {
        viewModel.removeEvent(id)

        val eventToRemove = events.firstOrNull {
            it.id == id
        }
        eventToRemove?.let {
            val index = events.indexOf(it)

            events.removeAt(index)
            adapter.notifyItemRemoved(index)
        }
    }

    override fun onEditEvent(event: Event) {
        val bottomSheet = EditEventBottomSheetDialog(requireContext(), parentFragmentManager, this, event)
        bottomSheet.show()
    }

    override fun onViewEvent(event: Event) {
        val bottomSheet = ViewEventBottomSheetDialog(requireContext(), this, event)
        bottomSheet.show()
    }
}