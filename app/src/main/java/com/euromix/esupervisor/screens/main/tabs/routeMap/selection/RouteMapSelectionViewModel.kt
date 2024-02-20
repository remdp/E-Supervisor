package com.euromix.esupervisor.screens.main.tabs.routeMap.selection

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.routes.entities.RouteMapSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.sources.routes.entities.TradingAgentsAndTeams
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteMapSelectionViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {

    private val _foundTradingAgents = MutableLiveData<Result<List<TradingAgentsAndTeams>>>()
    val foundTradingAgents = _foundTradingAgents.share()

    private val _foundTradingTeams = MutableLiveData<Result<List<ServerPair>>>()
    val foundTradingTeams = _foundTradingTeams.share()

    private val _selection = MutableLiveData<RouteMapSelection>()
    val selection = _selection.share()

    private val _errorsMinLength = MutableLiveData<ErrorsMinLength>()
    val errorsMinLength = _errorsMinLength.share()

    init {
        findTradingTeams()
        _errorsMinLength.value = ErrorsMinLength()
    }

    fun initSelection(selection: RouteMapSelection) {
        _selection.value = selection
    }

    fun findTradingAgentsAndTeams(searchString: String) {
        if (verifyMinLength(searchString)) {
            safeLaunch {
                searchRepository.findTradingAgentsAndTeams(searchString).collect {
                    _foundTradingAgents.value = it
                }
            }
        }
    }

    private fun findTradingTeams() {
        safeLaunch {
            searchRepository.findTradingTeams().collect {
                _foundTradingTeams.value = it
            }
        }
    }

    fun updateTradingAgentSelection(tradingAgent: ServerPair?) {

        val tradingAgentTeam =
            (_foundTradingAgents.value as? Success<List<TradingAgentsAndTeams>>)?.value?.firstOrNull { it.tradingAgent.id == tradingAgent?.id }?.tradingTeam

        val outletsTTNotTA =
            if (tradingAgent == null) false else _selection.value?.outletsTTNotTA ?: false
        val outletsNotRouteTTButInOtherTT =
            if (tradingAgentTeam == null) false else _selection.value?.outletsNotRouteTTButInOtherTT
                ?: false

        _selection.value =
            _selection.value?.copy(
                tradingAgent = tradingAgent,
                tradingTeam = tradingAgentTeam,
                outletsTTNotTA = outletsTTNotTA,
                outletsNotRouteTTButInOtherTT = outletsNotRouteTTButInOtherTT
            )
    }

    fun updateTradingTeamSelection(tradingTeam: ServerPair?) {

        val outletsNotRouteTTButInOtherTT =
            if (tradingTeam == null) false else _selection.value?.outletsNotRouteTTButInOtherTT
                ?: false

        _selection.value = _selection.value?.copy(
            tradingTeam = tradingTeam,
            outletsNotRouteTTButInOtherTT = outletsNotRouteTTButInOtherTT
        )
    }

    fun changeOutletsInRouteTASelection() {
        _selection.value?.outletsWithVisits?.let {
            _selection.value = _selection.value?.copy(outletsWithVisits = !it)
        }
    }

    fun changeOutletsTANotRouteSelection() {
        _selection.value?.outletsWithoutVisits?.let {
            _selection.value = _selection.value?.copy(outletsWithoutVisits = !it)
        }
    }

    fun changeOutletsTTNotTASelection() {
        _selection.value?.outletsTTNotTA?.let {
            _selection.value = _selection.value?.copy(outletsTTNotTA = !it)
        }
    }

    fun changeOutletsNotRouteTTButInOtherTTSelection() {
        _selection.value?.outletsNotRouteTTButInOtherTT?.let {
            _selection.value = _selection.value?.copy(outletsNotRouteTTButInOtherTT = !it)
        }
    }

    fun changePromisingOutletsSelection() {
        _selection.value?.promisingOutlets?.let {
            _selection.value = _selection.value?.copy(promisingOutlets = !it)
        }
    }

    fun checkTradingTeamEmpty(): Boolean = _selection.value?.tradingTeam == null

    //click
    // 0-common click
    //1-right drawable click
    fun handleViewClick(
        itemsList: List<ServerPair>,
        updaterSelection: (ServerPair?) -> Unit,
        anchor: View,
        click: Int,
        emptyChecker: () -> Boolean
    ) {

        if (_selection.value?.tradingAgent == null)
            if (click == 0 || emptyChecker())
                popupWindowForSelections(
                    anchor.context,
                    itemsList,
                    updaterSelection
                ).showAsDropDown(anchor)
            else updaterSelection(null)

    }

    private fun verifyMinLength(searchString: String): Boolean {

        return (searchString.length >= Const.MIN_LENGTH_SEARCH_STRING).also {
            if (!it) {
                _errorsMinLength.value =
                    _errorsMinLength.value?.copy(minLengthTradingAgentError = true)
            }
        }
    }

    fun clearSelection() {

        _selection.value?.let {
            _selection.value = RouteMapSelection(it.day)
        }
    }
}

data class ErrorsMinLength(
    val minLengthTradingAgentError: Boolean = false,
    val minLengthTradingTeamError: Boolean = false
)