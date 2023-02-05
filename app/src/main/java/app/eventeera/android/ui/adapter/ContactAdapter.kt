package app.eventeera.android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import app.eventeera.android.R
import app.eventeera.android.data.model.Contact

class ContactAdapter(private val items: List<Contact>, private val context: Context) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactNameTextView: TextView
        val contactPhoneTextView: TextView
        val icon: AppCompatImageView

        init {
            contactNameTextView = view.findViewById(R.id.contactName)
            contactPhoneTextView = view.findViewById(R.id.contactPhone)
            icon = view.findViewById(R.id.contactIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_contact, parent, false)

        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            contactNameTextView.text = item.name
            contactPhoneTextView.text = item.phone
            item.icon?.let {
                icon.setImageDrawable(AppCompatResources.getDrawable(context, it))
            } ?: icon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.contact_zaluzhniy))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
