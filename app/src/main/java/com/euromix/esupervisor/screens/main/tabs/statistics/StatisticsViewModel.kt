package com.euromix.esupervisor.screens.main.tabs.statistics

import com.euromix.esupervisor.app.enums.Role
import com.euromix.esupervisor.app.enums.ServerType
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.app.model.common.entities.ServerSelectionItem
import com.euromix.esupervisor.app.model.routes.RoutesRepository
import com.euromix.esupervisor.app.model.routes.entities.StatisticsSelection
import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticData
import com.euromix.esupervisor.app.model.routes.entities.VisitsStatisticDetailData
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.dateToJsonString
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.routes.entities.RoutesStatisticDetailRequestEntity
import com.euromix.esupervisor.sources.routes.entities.RoutesStatisticRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Base64
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val routesRepository: RoutesRepository
) :
    BaseViewModel() {

    private lateinit var _viewState: ViewState
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    fun init(){

        val detailLevel = if (accountRepository.getCurrentRole() != Role.DIRECTOR)
            1
        else
            0

        _viewState = ViewState(
            selection = StatisticsSelection(
                period = Pair(Date(), Date()),
                detailLevel = detailLevel
            ),
            jumpCount = detailLevel
        )
        getRoutesStatistics()


    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> {

                (result.value as? List<VisitsStatisticData>)?.let {
                    handleSuccessRoutesStatistic(it)
                }

                (result.value as? VisitsStatisticDetailData)?.let {
                    handleSuccessRoutesStatisticDetail(it)
                }
            }

            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun handleSuccessRoutesStatistic(value: List<VisitsStatisticData>) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            visitsData = value
        )
    }

    private fun handleSuccessRoutesStatisticDetail(value: VisitsStatisticDetailData) {

        val newDataList = _viewState.visitsData.map { item: VisitsStatisticData ->
            if (item.serverObject.serverPair.id == value.id) {
                item.copy(detailData = value)
            } else
                item
        }

        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            visitsData = newDataList
        )
    }

    private fun getRoutesStatistics() {
        safeLaunch {
            _viewState.selection.let { selection ->
                routesRepository.getVisitsStatistic(
                    RoutesStatisticRequestEntity(
                        startDate = selection.period.first.dateToJsonString(),
                        endDate = selection.period.second.dateToJsonString(),
                        balanceUnitId = selection.balanceUnit?.id,
                        tradingTeamId = selection.tradingTeam?.id,
                        tradingAgentId = selection.tradingAgent?.id,
                        detailLevel = selection.detailLevel
                    )
                ).collect {
                    updateViewState(it)
                }
            }
        }
    }

    private fun getVisitsStatisticDetail() {

        _viewState.detailSelection?.let {
            safeLaunch {
                routesRepository.getVisitsStatisticDetail(
                    RoutesStatisticDetailRequestEntity(
                        startDate = _viewState.selection.period.first.dateToJsonString(),
                        endDate = _viewState.selection.period.second.dateToJsonString(),
                        it
                    )
                ).collect {
                    updateViewState(it)
                }
            }
        }
    }


    fun canBack() = when (viewState.jumpCount) {
        1 -> accountRepository.getCurrentRole() == Role.DIRECTOR
        2 -> true
        else -> false
    }


    fun changePeriod(period: Pair<Date, Date>) {
        _viewState = _viewState.copy(selection = _viewState.selection.copy(period = period))
        getRoutesStatistics()
    }

    fun changeSelection(selection: StatisticsSelection) {
        _viewState = _viewState.copy(selection = selection)
        getRoutesStatistics()
    }

    fun onItemClick(item: VisitsStatisticData) {

        if (item.isExpanded) {
            collapseItem(item)
        } else {
            expandItem(
                ServerSelectionItem(
                    item.serverObject.serverPair.id,
                    Base64.getEncoder()
                        .encodeToString(item.serverObject.serverType.toByteArray(Charsets.UTF_8))
                )
            )
        }
    }

    private fun collapseItem(item: VisitsStatisticData) {
        _viewState = _viewState.copy(
            detailSelection = null,
            visitsData = _viewState.visitsData.map {
                if (it.serverObject.serverPair.id == item.serverObject.serverPair.id) {
                    it.copy(isExpanded = !it.isExpanded)
                } else {
                    it
                }
            })

        _viewStateEvent.publishEvent()
    }

    private fun expandItem(itemSelection: ServerSelectionItem) {

        _viewState = _viewState.copy(
            detailSelection = itemSelection,
            visitsData = _viewState.visitsData.map { item ->
                if (item.serverObject.serverPair.id == itemSelection.id) {
                    item.copy(isExpanded = !item.isExpanded)
                } else {
                    item
                }
            })
        getVisitsStatisticDetail()
    }

    fun changeWatchAllManufacturersLogo(id: String) {

        val newDataList = _viewState.visitsData.map { item: VisitsStatisticData ->
            if (item.serverObject.serverPair.id == id) {
                item.detailData?.let { detailData ->
                    item.copy(detailData = detailData.copy(watchAllManufacturersLogo = !detailData.watchAllManufacturersLogo))
                } ?: item
            } else
                item
        }

        _viewState = _viewState.copy(
            visitsData = newDataList
        )

        _viewStateEvent.publishEvent()

    }

    fun decipher(serverObject: ServerObject) {

        val newSelection = when (ServerType.fromTypeName(serverObject.serverType)) {
            ServerType.BALANCE_UNIT -> _viewState.selection.copy(
                balanceUnit = serverObject.serverPair,
                detailLevel = 1
            )

            ServerType.TRADING_TEAM -> _viewState.selection.copy(
                tradingTeam = serverObject.serverPair,
                detailLevel = 2
            )

            ServerType.TRADING_TEAM_HR -> _viewState.selection.copy(
                tradingTeam = serverObject.serverPair,
                detailLevel = 2
            )

            ServerType.TRADING_AGENT -> _viewState.selection.copy(
                tradingAgent = serverObject.serverPair,
                detailLevel = 2
            )
        }

        _viewState = _viewState.copy(
            selection = newSelection,
            jumpCount = _viewState.jumpCount + 1,
            detailSelection = null,
            backStackItems = viewState.backStackItems + serverObject.serverPair.presentation
        )
        getRoutesStatistics()

    }

    fun back() {

        if (canBack()) {
            val newSelection = if (_viewState.selection.tradingTeam != null) {
                _viewState.selection.copy(
                    tradingTeam = null,
                    detailLevel = _viewState.selection.detailLevel - 1
                )
            } else {
                _viewState.selection.copy(
                    balanceUnit = null,
                    detailLevel = _viewState.selection.detailLevel - 1
                )
            }
            _viewState = _viewState.copy(
                selection = newSelection,
                jumpCount = _viewState.jumpCount - 1,
                detailSelection = null,
                backStackItems = viewState.backStackItems.dropLast(1)
            )
            getRoutesStatistics()
        }
    }

    fun reload() {
        getRoutesStatistics()
    }

    fun reloadDetail() {
        getVisitsStatisticDetail()
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val selection: StatisticsSelection,
        val visitsData: List<VisitsStatisticData> = listOf(),
        val detailSelection: ServerSelectionItem? = null,
        val jumpCount: Int = 0,
        val backStackItems: List<String> = listOf()
    ) : BaseViewState()
}
