package com.euromix.esupervisor.app.screens

import com.euromix.esupervisor.app.utils.LiveEvent
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share

interface Scrollable {
    val scrollToTopEvent: LiveEvent<Unit>
    fun triggerScrollToTop()
}

class ScrollableDelegate : Scrollable {
    private val _scrollToTopEvent = MutableLiveEvent<Unit>()

    override val scrollToTopEvent: LiveEvent<Unit>
        get() = _scrollToTopEvent.share()

    override fun triggerScrollToTop() {
        _scrollToTopEvent.publishEvent(Unit)
    }
}