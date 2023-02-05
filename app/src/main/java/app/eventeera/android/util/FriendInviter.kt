package app.eventeera.android.util

interface FriendInviter {

    fun onFriendInvited(id: Long)

    fun onFriendRemoved(id: Long)
}