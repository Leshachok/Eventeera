package app.eventeera.android.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.eventeera.android.R
import app.eventeera.android.data.model.Event

class EventAdapter(val items: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView
        val timeTextView: TextView

        init {
            titleTextView = view.findViewById(R.id.textTitle)
            timeTextView = view.findViewById(R.id.textTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_event, parent, false)

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.timeTextView.text = "${item.startTime} - ${item.endTime}"
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
