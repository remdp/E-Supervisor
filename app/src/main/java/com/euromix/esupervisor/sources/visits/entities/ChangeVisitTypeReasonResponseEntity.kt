package com.euromix.esupervisor.sources.visits.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.visits.entities.ChangeVisitTypeReason
import com.squareup.moshi.Json

data class ChangeVisitTypeReasonResponseEntity(
    @field:Json(name = "required_description") val requiredDescription: Boolean,
    val reason: ServerPair
) {
    fun toChangeVisitTypeReason() =
        ChangeVisitTypeReason(requiredDescription = requiredDescription, reason = reason)
}
