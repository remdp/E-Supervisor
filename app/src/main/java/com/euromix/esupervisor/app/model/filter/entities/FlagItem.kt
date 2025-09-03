package com.euromix.esupervisor.app.model.filter.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlagItem(val id: Int, val flag: Boolean = false) : Parcelable
