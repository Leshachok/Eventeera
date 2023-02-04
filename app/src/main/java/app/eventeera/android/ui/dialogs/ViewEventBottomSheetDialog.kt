package app.eventeera.android.ui.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.core.content.ContextCompat
import app.eventeera.android.data.model.Event
import app.eventeera.android.databinding.BottomsheetEventViewBinding
import app.eventeera.android.util.EventManager
import app.eventeera.android.util.getChipBgColorByEventType
import app.eventeera.android.util.getChipColorByEventType
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class ViewEventBottomSheetDialog(context: Context, private val eventManager: EventManager, private val event: Event) : BottomSheetDialog(context) {

    private var _binding: BottomsheetEventViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = BottomsheetEventViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.eventTitleTextView.text = event.title

        fillDate()
        fillType()

        binding.buttonEditEvent.setOnClickListener {
            dismiss()
            eventManager.onEditEvent(event)
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

        val textColor = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        val bgColor = ColorStateList.valueOf(ContextCompat.getColor(context, colorBg))

        binding.eventTypeChip.apply {
            text = event.type
            setTextColor(textColor)
            chipBackgroundColor = bgColor
        }
    }
}