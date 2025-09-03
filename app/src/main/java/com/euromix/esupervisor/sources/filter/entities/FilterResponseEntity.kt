package com.euromix.esupervisor.sources.filter.entities

import android.os.Parcelable
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.filter.entities.DetailFilterItem
import com.euromix.esupervisor.app.model.filter.entities.FilterItem
import com.euromix.esupervisor.app.model.filter.entities.FilterSelection
import com.euromix.esupervisor.app.model.filter.entities.FlagItem
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterResponseEntity(
    @field:Json(name = "flags_number") val flagsNumber: Int = 0,
    @field:Json(name = "server_pairs") val serverPairs: List<List<ServerPair>>
) : Parcelable {
    fun toFilterSelection(): FilterSelection {

        var nextIdFlags = 0
        var nextIdTA = 0

        return FilterSelection(
            flags = List(flagsNumber) {
                FlagItem(id = nextIdFlags++)
            },
            items = serverPairs.map { listServerPairs ->
                FilterItem(
                    id = nextIdTA++,
                    detailFilterItems = listServerPairs.map { serverPair ->
                        DetailFilterItem(
                            serverPair = serverPair
                        )
                    })
            }
        )
    }
}
