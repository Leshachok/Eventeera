package app.eventeera.android.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.eventeera.android.data.model.Event
import app.eventeera.android.databinding.FragmentCalendarBinding
import app.eventeera.android.ui.dialogs.CreateEventBottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class CalendarFragment : Fragment(), EventCreator {

    private val viewModel: CalendarViewModel by viewModels()

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: EventAdapter
    private var events = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        adapter = EventAdapter(events)

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
                events.add(it)
                adapter.notifyItemInserted(events.size - 1)
            }
        }

        viewModel.date.observe(viewLifecycleOwner) { date ->
            val uaDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.forLanguageTag("uk")) // 2
            val uaFormattedDate = date.format(uaDateFormatter)
            binding.dateText.text = uaFormattedDate

            binding.arrowLeft.isEnabled = date.dayOfYear != LocalDate.now().dayOfYear
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

        return binding.root
    }

    private fun onOpenCreateBottomSheet() {
        val dialog = CreateEventBottomSheetDialog(requireContext(), parentFragmentManager, this)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onEventCreated(event: Event) {
        val currentDate = viewModel.date.value
        currentDate?.let {
            val eventDate = LocalDate.parse(event.timeStamp, viewModel.pattern)
            if(eventDate.dayOfYear == currentDate.dayOfYear){
                events.add(event)
                events.sortBy { it.startTime }
                adapter.notifyItemInserted(events.indexOf(event))
            }
        }

        viewModel.addEvent(event)
    }
}

interface EventCreator {
    fun onEventCreated(event: Event)
}