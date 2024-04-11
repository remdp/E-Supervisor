package com.euromix.esupervisor.screens.main.tabs.visits.list

import com.euromix.esupervisor.app.enums.VisitType
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
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
    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    private var _selection: VisitsListSelection = VisitsListSelection(period = Pair(Date(), Date()))
    val selection: VisitsListSelection
        get() = _selection
    private val _selectionEvent = MutableLiveEvent<VisitsListSelection>()
    val selectionEvent = _selectionEvent.share()

    private var currentJob: Job? = null

    init {
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        if (result !is Pending) currentJob = null

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
        _viewState = _viewState.copy(isLoading = false, error = null)
        _viewState.visits.clear()
        _viewState.visits.addAll(value)
        _viewState.filteredIds.clear()
        _viewState.filteredIdsCanBeChanged.clear()
        _viewState.markedIds.clear()
        setFilteredItems()
    }


    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getVisits() {

        currentJob?.cancel()

        currentJob = safeLaunch {
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

        _viewState.filteredIds.clear()
        _viewState.filteredIdsCanBeChanged.clear()

        _viewState.filteredIds.addAll(filteredVisits.map { it.extId })
        _viewState.filteredIdsCanBeChanged.addAll(filteredVisits.filter { it.canBeChanged }
            .map { it.extId })
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
            Pair(totalVisits, _viewState.visits.size),
            Pair(VisitsListViewModel.regularVisits, regularVisits.size),
            Pair(VisitsListViewModel.regularVisitsDone, regularVisitsDone.size),
            Pair(VisitsListViewModel.remoteVisits, remoteVisits.size),
            Pair(VisitsListViewModel.remoteVisitsDone, remoteVisitsDone.size),
            Pair(VisitsListViewModel.oneTimeVisits, oneTimeVisits.size),
            Pair(VisitsListViewModel.oneTimeVisitsDone, oneTimeVisitsDone.size)
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
    fun quickFilter() = _viewState.quickFilter
    fun totalMark() = _viewState.totalMark
    fun showMarks() = _viewState.showMarks
    fun searchString() = _viewState.searchString
    fun isLoading() = _viewState.isLoading
    fun error() = _viewState.error

    companion object {
        const val totalVisits = "TOTAL_VISITS"
        const val regularVisits = "REGULAR_VISITS"
        const val regularVisitsDone = "REGULAR_VISITS_DONE"
        const val remoteVisits = "REMOTE_VISITS"
        const val remoteVisitsDone = "REMOTE_VISITS_DONE"
        const val oneTimeVisits = "ONE_TIME_VISITS"
        const val oneTimeVisitsDone = "ONE_TIME_VISITS_DONE"
    }

    data class ViewState(
        val period: Pair<Date, Date>? = null,
        val visits: MutableList<Visit> = mutableListOf(),
        val filteredIds: MutableList<String> = mutableListOf(),
        val filteredIdsCanBeChanged: MutableList<String> = mutableListOf(),
        val markedIds: MutableList<String> = mutableListOf(),
        override val isLoading: Boolean = false,
        val result: Result<List<DocEmix>> = Empty(),
        override val error: Throwable? = null,
        val quickFilter: VisitType? = null,
        val showMarks: Boolean = false,
        val totalMark: Boolean? = false,
        val scrollToTop: Boolean = false,
        val searchString: String = ""
    ) : BaseViewState()
}