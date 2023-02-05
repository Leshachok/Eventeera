package app.eventeera.android.data.repository

import app.eventeera.android.R
import app.eventeera.android.data.model.Contact

class ContactRepository private constructor(){

    companion object {
        val Instance = ContactRepository()
    }

    private val contacts = mutableListOf<Contact>()

    fun getContacts(query: String? = null): List<Contact>{
        return query?.let {
            contacts.filter { contact ->
                contact.name.contains(it)
            }
        } ?: contacts

    }

    fun addContact(contact: Contact) {
        contacts.add(contact)
    }

    init {
        contacts.addAll(
            listOf(
                Contact(1L, "Валерій Залужний", "+380994323454", R.drawable.contact_zaluzhniy),
                Contact(2L, "Тарас Чмут", "+380677656566", R.drawable.contact_chmut),
                Contact(3L, "Максим Марченко", "+380995542785", R.drawable.contact_marchenko),
            )
        )
    }
}