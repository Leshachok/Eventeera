package app.eventeera.android.ui.calendar

import android.icu.util.LocaleData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.eventeera.android.data.model.Event
import app.eventeera.android.databinding.FragmentCalendarBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class CalendarFragment : Fragment() {

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
            val dialog = BottomSheetDialog(requireContext())
            dialog.show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}