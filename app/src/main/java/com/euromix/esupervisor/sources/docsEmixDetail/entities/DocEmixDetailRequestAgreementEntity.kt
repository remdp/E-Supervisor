package com.euromix.esupervisor.sources.docsEmixDetail.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class DocEmixDetailRequestAgreementEntity(
    val approve: Boolean,
    @field:Json(name = "reason_reject") val reasonReject: String?
)
