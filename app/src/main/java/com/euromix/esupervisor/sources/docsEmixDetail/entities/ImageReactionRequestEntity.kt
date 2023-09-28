package com.euromix.esupervisor.sources.docsEmixDetail.entities

import com.euromix.esupervisor.App.Companion.dateToJsonString
import java.util.Calendar

data class ImageReactionRequestEntity(
    val url: String,
    val reaction: Int,
    val comment: String = "",
    val createDislikeTask: Boolean = false,
    val deadline: String = dateToJsonString(Calendar.getInstance().time)
)
