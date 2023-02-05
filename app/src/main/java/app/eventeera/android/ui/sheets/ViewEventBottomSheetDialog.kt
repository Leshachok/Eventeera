package app.eventeera.android.ui.sheets

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.eventeera.android.R
import app.eventeera.android.data.model.Event
import app.eventeera.android.databinding.BottomsheetEventViewBinding
import app.eventeera.android.ui.adapter.ContactAdapter
import app.eventeera.android.util.EventManager
import app.eventeera.android.util.getChipBgColorByEventType
import app.eventeera.android.util.getChipColorByEventType
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class ViewEventBottomSheetDialog(private val eventManager: EventManager, private val event: Event)
    : BottomSheetDialogFragment(R.layout.bottomsheet_event_view) {

    private var _binding: BottomsheetEventViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetEventViewBinding.inflate(layoutInflater)

        binding.eventTitleTextView.text = event.title

        fillDate()
        fillType()
        fillInvites()

        binding.buttonEditEvent.setOnClickListener {
            dismiss()
            eventManager.onEditEvent(event)
        }
        return binding.root
    }

    private fun fillInvites() {

        val friendAdapter = ContactAdapter(
            event.invitedContacts,
            requireContext()
        )

        binding.friendsRecyclerView.apply {
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fillDate() {
        val timeStamp = event.timeStamp
        val localDate = LocalDate.parse(timeStamp)

        val uaDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.forLanguageTag("uk")) // 2
        val uaFormattedDate = localDate.format(uaDateFormatter)

        binding.eventTimeTextView.text = "${event.startTime} - ${event.endTime}, $uaFormattedDate"
    }

    private fun fillType() {
        val color = getChipColorByEventType(event.type)
        val colorBg = getChipBgColorByEventType(event.type)

        val textColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))
        val bgColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorBg))

        binding.eventTypeChip.apply {
            text = event.type
            setTextColor(textColor)
            chipBackgroundColor = bgColor
        }
    }
}