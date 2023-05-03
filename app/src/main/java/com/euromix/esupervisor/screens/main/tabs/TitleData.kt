package com.euromix.esupervisor.screens.main.tabs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TitleData(
    val startText: String?,
    val endText: String?
):Parcelable
