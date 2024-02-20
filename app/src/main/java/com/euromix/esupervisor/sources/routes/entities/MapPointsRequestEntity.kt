package com.euromix.esupervisor.sources.routes.entities

import com.euromix.esupervisor.app.model.routes.entities.RouteMapSelection
import com.euromix.esupervisor.app.utils.dateToJsonString
import com.squareup.moshi.Json

data class MapPointsRequestEntity(
    @field:Json(name = "start_date") val startDate: String,
    @field:Json(name = "end_date") val endDate: String,
    @field:Json(name = "trading_agent_id") val tradingAgentId: String?,
    @field:Json(name = "trading_team_id") val tradingTeamId: String?,

    @field:Json(name = "outlets_with_visits") val outletsWithVisits: Boolean,
    @field:Json(name = "outlets_without_visits") val outletsWithoutVisits: Boolean,

    @field:Json(name = "outlets_tt_not_ta") val outletsTTNotTA: Boolean,
    @field:Json(name = "outlets_not_route_tt_but_in_other_tt") val outletsNotRouteTTButInOtherTT: Boolean,
    @field:Json(name = "promising_outlets") val promisingOutlets: Boolean
) {
    companion object {

        fun mapPointsRequestEntity(selection: RouteMapSelection) =
            selection.day.dateToJsonString()
                .let {
                    MapPointsRequestEntity(
                        it,
                        it,
                        selection.tradingAgent?.id,
                        selection.tradingTeam?.id,

                        selection.outletsWithVisits,
                        selection.outletsWithoutVisits,

                        selection.outletsTTNotTA,
                        selection.outletsNotRouteTTButInOtherTT,
                        selection.promisingOutlets
                    )
                }
    }
}
