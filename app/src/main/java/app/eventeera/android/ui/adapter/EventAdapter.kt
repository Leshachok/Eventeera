package app.eventeera.android.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.eventeera.android.R
import app.eventeera.android.data.model.Event
import app.eventeera.android.util.EventManager
import app.eventeera.android.util.getChipBgColorByEventType
import app.eventeera.android.util.getChipColorByEventType
import com.google.android.material.chip.Chip

class EventAdapter(private val items: List<Event>, private val context: Context, private val eventManager: EventManager) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView
        val timeTextView: TextView
        val typeChip: Chip

        init {
            titleTextView = view.findViewById(R.id.textTitle)
            timeTextView = view.findViewById(R.id.textTime)
            typeChip = view.findViewById(R.id.eventType)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_event, parent, false)

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = items[position]
        val color = getChipColorByEventType(item.type)
        val colorBg = getChipBgColorByEventType(item.type)

        val textColor = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        val bgColor = ColorStateList.valueOf(ContextCompat.getColor(context, colorBg))

        holder.apply {
            titleTextView.text = item.title
            timeTextView.text = "${item.startTime} - ${item.endTime}"
            typeChip.text = item.type
            typeChip.setTextColor(textColor)
            typeChip.chipBackgroundColor = bgColor
        }

        holder.itemView.setOnClickListener {
            eventManager.onViewEvent(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
