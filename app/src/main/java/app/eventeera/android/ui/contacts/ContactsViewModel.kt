package app.eventeera.android.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.eventeera.android.data.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ContactsViewModel : ViewModel() {

    private val repository = ContactRepository.Instance

    private val query = MutableStateFlow<String?>(null)

    private val TAG = "CalendarViewModel"

    val contactsFlow = query.flatMapLatest { query ->
        flowOf(repository.getContacts(query))
    }

    fun getContactsByQuery(query: String?){
        viewModelScope.launch {
            this@ContactsViewModel.query.emit(query)
        }
    }
}