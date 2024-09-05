package com.euromix.esupervisor.dialogs.selectPictureDialog

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.euromix.esupervisor.app.utils.Event
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.share

class SelectPictureViewModel : ViewModel() {

    private val _uriEvent = MutableLiveEvent<Uri>()
    val uriEvent = _uriEvent.share()

    fun selectPicture(uri: Uri) {
        _uriEvent.value = Event(uri)
    }

}