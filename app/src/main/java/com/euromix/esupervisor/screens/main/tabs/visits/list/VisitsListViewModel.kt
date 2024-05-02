package com.euromix.esupervisor.screens.main.tabs.visits.list

import com.euromix.esupervisor.app.enums.VisitType
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visits.VisitsRepository
import com.euromix.esupervisor.app.model.visits.entities.Visit
import com.euromix.esupervisor.app.model.visits.entities.VisitsListSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.dateToJsonString
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.visits.entities.VisitsRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VisitsListViewModel @Inject constructor(private val visitsRepository: VisitsRepository) :
    BaseViewModel() {

    private var _viewState: ViewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    private var _selection: VisitsListSelection = VisitsListSelection(period = Pair(Date(), Date()))
    val selection: VisitsListSelection
        get() = _selection
    private val _selectionEvent = MutableLiveEvent<VisitsListSelection>()
    val selectionEvent = _selectionEvent.share()

    init {
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value as List<Visit>)
            is Error -> handleError(result.error)
            else -> {}
        }

        _viewStateEvent.publishEvent(_viewState)
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null, totalMark = false)

    }

    private fun handleSuccess(value: List<Visit>) {
        _viewState = _viewState.copy(isLoading = false, error = null, visits = value)

        _viewState.markedIds.clear()
        setFilteredItems()
    }


    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getVisits() {
       safeLaunch {
            visitsRepository.getVisits(requestFromSelection()).collect {
                updateViewState(it)
            }
        }
    }

    private fun requestFromSelection() = VisitsRequestEntity(
        startDate = _selection.period?.first?.dateToJsonString(),
        endDate = _selection.period?.second?.dateToJsonString(),
        tradingAgentId = _selection.tradingAgent?.id
    )

    private fun setFilteredItems() {

        val filteredVisits = when (_viewState.quickFilter) {
            VisitType.REGULAR -> _viewState.visits.filter { it.type == VisitType.REGULAR }
            VisitType.REMOTE -> _viewState.visits.filter { it.type == VisitType.REMOTE }
            VisitType.ONETIME -> _viewState.visits.filter { it.type == VisitType.ONETIME }
            else -> _viewState.visits
        }.filter {
            if (_viewState.searchString.isNotEmpty()) it.partner.contains(
                _viewState.searchString, ignoreCase = true
            ) || it.address.contains(_viewState.searchString, ignoreCase = true)
            else true
        }

        _viewState = _viewState.copy(filteredIdsCanBeChanged = filteredVisits
            .filter { it.canBeChanged }
            .map { it.extId },
            filteredIds = filteredVisits.map { it.extId })

    }

    private fun setMarkedItemsTotalCLick() {

        _viewState.markedIds.clear()
        val totalMark = _viewState.totalMark ?: false

        if (totalMark) _viewState.markedIds.addAll(_viewState.filteredIdsCanBeChanged)
    }

    private fun setMarkedItemsFilterChange() {
        _viewState.markedIds.removeIf { it !in _viewState.filteredIds }
    }

    private fun setMarkedItemsItemClick(extId: String) {
        if (!_viewState.markedIds.remove(extId)) {
            _viewState.markedIds.add(extId)
        }
    }

    private fun setTotalMark() {
        val hasMarkedItems = _viewState.markedIds.size != 0
        val hasUnmarkedItems = _viewState.markedIds.size != _viewState.filteredIdsCanBeChanged.size

        _viewState = _viewState.copy(
            totalMark = when {
                hasMarkedItems && hasUnmarkedItems -> null
                hasMarkedItems -> true
                else -> false
            }
        )
    }

    fun reload() {
        getVisits()
    }

    fun changePeriod(period: Pair<Date, Date>?) {
        _selection = _selection.copy(period = period)
        _selectionEvent.publishEvent(_selection)
    }

    fun updateSelection(selection: VisitsListSelection? = null) {
        _selection = selection ?: _selection
        _selectionEvent.publishEvent(_selection)
    }

    fun changeShowMarks() {

        _viewState = _viewState.copy(showMarks = !_viewState.showMarks, totalMark = false)
        setMarkedItemsTotalCLick()

        _viewStateEvent.publishEvent(_viewState)
    }

    fun changeMarks() {

        val newTotalMark = !(_viewState.totalMark ?: true)
        _viewState = _viewState.copy(totalMark = newTotalMark)
        setMarkedItemsTotalCLick()

        _viewStateEvent.publishEvent(_viewState)
    }

    fun changeMark(extId: String) {

        setMarkedItemsItemClick(extId)
        setTotalMark()

        _viewStateEvent.publishEvent(_viewState)
    }

    fun changeSearchString(searchString: String) {
        _viewState = _viewState.copy(searchString = searchString)
        setFilteredItems()
        setMarkedItemsFilterChange()
        setTotalMark()

        _viewStateEvent.publishEvent(_viewState)
    }

    fun visitsData(): Map<String, Int> {

        val regularVisits = _viewState.visits.filter { it.type == VisitType.REGULAR }
        val regularVisitsDone = regularVisits.filter { it.done }
        val remoteVisits = _viewState.visits.filter { it.type == VisitType.REMOTE }
        val remoteVisitsDone = remoteVisits.filter { it.done }
        val oneTimeVisits = _viewState.visits.filter { it.type == VisitType.ONETIME }
        val oneTimeVisitsDone = oneTimeVisits.filter { it.done }

        return mapOf(
            Pair(TOTAL_VISITS, _viewState.visits.size),
            Pair(REGULAR_VISITS, regularVisits.size),
            Pair(REGULAR_VISITS_DONE, regularVisitsDone.size),
            Pair(REMOTE_VISITS, remoteVisits.size),
            Pair(REMOTE_VISITS_DONE, remoteVisitsDone.size),
            Pair(ONE_TIME_VISITS, oneTimeVisits.size),
            Pair(ONE_TIME_VISITS_DONE, oneTimeVisitsDone.size)
        )
    }

    fun setupQuickFilter(visitType: VisitType? = null) {
        _viewState = _viewState.copy(quickFilter = visitType, scrollToTop = true)
        setFilteredItems()
        setMarkedItemsFilterChange()
        setTotalMark()

        _viewStateEvent.publishEvent(_viewState)
    }

    fun getListForSubmit(): List<Visit> {

        return (if (_viewState.searchString.isNotEmpty() || _viewState.filteredIds.isNotEmpty()) {
            _viewState.visits.filter { it.extId in _viewState.filteredIds }
        } else listOf()).map {
            it.copy(
                showMark = _viewState.showMarks, mark = if (it.extId in _viewState.markedIds) true
                else _viewState.totalMark ?: false
            )
        }
    }

    fun scrollToTop(): Boolean {
        val scrollToTop = _viewState.scrollToTop
        _viewState = _viewState.copy(scrollToTop = false)
        return scrollToTop
    }

    fun markedVisits() = _viewState.markedIds

    companion object {
        const val TOTAL_VISITS = "TOTAL_VISITS"
        const val REGULAR_VISITS = "REGULAR_VISITS"
        const val REGULAR_VISITS_DONE = "REGULAR_VISITS_DONE"
        const val REMOTE_VISITS = "REMOTE_VISITS"
        const val REMOTE_VISITS_DONE = "REMOTE_VISITS_DONE"
        const val ONE_TIME_VISITS = "ONE_TIME_VISITS"
        const val ONE_TIME_VISITS_DONE = "ONE_TIME_VISITS_DONE"
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val period: Pair<Date, Date>? = null,
        val visits: List<Visit> = listOf(),
        val filteredIds: List<String> = listOf(),
        val filteredIdsCanBeChanged: List<String> = listOf(),
        val markedIds: MutableList<String> = mutableListOf(),
        val quickFilter: VisitType? = null,
        val showMarks: Boolean = false,
        val totalMark: Boolean? = false,
        val scrollToTop: Boolean = false,
        val searchString: String = ""
    ) : BaseViewState()
}