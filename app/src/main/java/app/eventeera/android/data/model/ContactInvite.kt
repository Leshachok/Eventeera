package app.eventeera.android.data.model

data class ContactInvite(
    val contact: Contact,
    var isInvited: Boolean = false
)