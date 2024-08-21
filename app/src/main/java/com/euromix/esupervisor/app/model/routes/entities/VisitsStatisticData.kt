package com.euromix.esupervisor.app.model.routes.entities

import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.sources.routes.entities.PlanFact

data class VisitsStatisticData(
    val serverObject: ServerObject,
    val childrenCount: Int = 0,
    val planVisits: PlanFact = PlanFact(),
    val unscheduledVisits: PlanFact = PlanFact(),
    val regularVisits: PlanFact = PlanFact(),
    val remoteConstantVisits: PlanFact = PlanFact(),
    val remoteSituationalVisits: PlanFact = PlanFact(),
    val effectiveVisits: PlanFact = PlanFact(),
    var isExpanded: Boolean = false,
    var detailData: VisitsStatisticDetailData? = null,
    val outletsTime: String,
    val travelTime: String

)
