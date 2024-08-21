package com.euromix.esupervisor.screens.main.tabs.statistics.selection

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.Const.MIN_LENGTH_SEARCH_STRING
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.routes.entities.StatisticsSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsSelectionViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {

    private val _foundTradingAgents = MutableLiveData<Result<List<ServerPair>>>()
    val foundTradingAgents = _foundTradingAgents.share()

    private val _foundBUAndTradingTeams = MutableLiveData<Result<List<List<ServerPair>>>>()
    val foundBUAndTradingTeams = _foundBUAndTradingTeams.share()

    private val _selection = MutableLiveData<StatisticsSelection>()
    val selection = _selection.share()

    private val _errorsMinLength = MutableLiveData<ErrorsMinLength>()
    val errorsMinLength = _errorsMinLength.share()

    private var _jumpCount: Int = 0
    val jumpCount: Int
        get() = _jumpCount


    init {
        findBUAndTradingTeams()
        _errorsMinLength.value = ErrorsMinLength()
    }

    fun initSelection(selection: StatisticsSelection, jumpCount:Int) {
        _selection.value = selection
        _jumpCount = jumpCount
    }

    private fun findBUAndTradingTeams() {
        safeLaunch {
            searchRepository.findBUAndTradingTeams().collect {
                _foundBUAndTradingTeams.value = it
            }
        }
    }

    fun updateBalanceUnitsSelection(balanceUnit: ServerPair?) {
        _selection.value = _selection.value?.copy(
            balanceUnit = balanceUnit
        )
    }

    fun updateTradingTeamSelection(tradingTeam: ServerPair?) {
        _selection.value = _selection.value?.copy(
            tradingTeam = tradingTeam
        )
    }

    fun findTradingAgents(searchString: String) {
        if (verifyMinLength(searchString)) {
            safeLaunch {
                searchRepository.findTradingAgents(searchString).collect {
                    _foundTradingAgents.value = it
                }
            }
        }
    }

    fun updateTradingAgentSelection(tradingAgent: ServerPair?) {
        _selection.value = _selection.value?.copy(tradingAgent = tradingAgent)
    }

    fun clearSelection() {

        _selection.value = when (_jumpCount) {
            2 -> _selection.value?.copy(tradingAgent = null)
            1 -> _selection.value?.copy(tradingTeam = null, tradingAgent = null)
            else -> _selection.value?.copy(
                balanceUnit = null,
                tradingTeam = null,
                tradingAgent = null
            )
        }

    }

    fun checkBalanceUnitEmpty(): Boolean = _selection.value?.balanceUnit == null
    fun checkTradingTeamEmpty(): Boolean = _selection.value?.tradingTeam == null

    //TODO get rid of function duplication
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

        if (click == 0 || emptyChecker())
            popupWindowForSelections(
                anchor.context,
                itemsList,
                updaterSelection
            ).showAsDropDown(anchor)
        else updaterSelection(null)

    }

    private fun verifyMinLength(searchString: String) =
        (searchString.length >= MIN_LENGTH_SEARCH_STRING).also {
            if (!it) {
                _errorsMinLength.value =
                    _errorsMinLength.value?.copy(minLengthTradingAgentError = true)
            }
        }
}

data class ErrorsMinLength(
    val minLengthTradingAgentError: Boolean = false
)