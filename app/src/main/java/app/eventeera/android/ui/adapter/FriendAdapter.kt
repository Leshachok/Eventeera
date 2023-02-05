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
import app.eventeera.android.data.model.ContactInvite
import app.eventeera.android.util.FriendInviter

class FriendAdapter(private val items: List<ContactInvite>, private val context: Context) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactNameTextView: TextView
        val contactPhoneTextView: TextView
        val icon: AppCompatImageView
        val inviteButton: AppCompatImageView
        val inviteIcon: AppCompatImageView

        init {
            contactNameTextView = view.findViewById(R.id.contactName)
            contactPhoneTextView = view.findViewById(R.id.contactPhone)
            icon = view.findViewById(R.id.contactIcon)
            inviteButton = view.findViewById(R.id.buttonInvite)
            inviteIcon = view.findViewById(R.id.inviteIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_contact_invite, parent, false)

        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            contactNameTextView.text = item.contact.name
            contactPhoneTextView.text = item.contact.phone

            item.contact.icon?.let {
                icon.setImageDrawable(AppCompatResources.getDrawable(context, it))
            } ?: icon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.contact_zaluzhniy))
        }

        val inviteButton = holder.inviteButton
        inviteButton.setOnClickListener {
            val drawable = if(item.isInvited) AppCompatResources.getDrawable(context, R.drawable.ic_person_add) else AppCompatResources.getDrawable(context, R.drawable.ic_person_remove)
            holder.inviteIcon.setImageDrawable(drawable)

            item.isInvited = !item.isInvited
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
