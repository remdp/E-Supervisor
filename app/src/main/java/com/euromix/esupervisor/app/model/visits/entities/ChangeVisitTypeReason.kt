package com.euromix.esupervisor.app.model.visits.entities

import com.euromix.esupervisor.app.model.common.entities.ServerPair

data class ChangeVisitTypeReason(
    val requiredDescription: Boolean,
    val reason: ServerPair
)
