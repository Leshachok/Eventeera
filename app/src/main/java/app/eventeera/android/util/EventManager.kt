package app.eventeera.android.util

import app.eventeera.android.data.model.Event


interface EventManager {

    fun onEventCreated(event: Event)

    fun onEventEdited(event: Event)

    fun onEventRemoved(id: Long)

    fun onViewEvent(event: Event)

    fun onEditEvent(event: Event)

}