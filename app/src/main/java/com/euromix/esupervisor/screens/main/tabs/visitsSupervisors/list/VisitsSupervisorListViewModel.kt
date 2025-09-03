package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsRepository
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitsSupervisorsListSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.utils.toJsonString
import com.euromix.esupervisor.app.utils.toLocalDate
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.visitsSupervisors.entities.RepeatStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitsSupervisorsRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VisitsSupervisorListViewModel @Inject constructor(private val visitsSupervisorsRepository: VisitsSupervisorsRepository) :
    BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _repeatStoreCheckCreationResult = MutableLiveEvent<Result<String>>()
    val repeatStoreCheckCreationResult = _repeatStoreCheckCreationResult.share()

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        reload()
    }

    private fun updateViewState(result: Result<List<VisitSupervisor>>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value)
            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent(_viewState)
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccess(value: List<VisitSupervisor>) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            totalMark = false,
            visitsSupervisors = value.map {
                it.copy(
                    showMark = _viewState.showMarks,
                    mark = false
                )
            })
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun fetchVisitsSupervisors() {
        safeLaunch {
            visitsSupervisorsRepository.getVisitsSupervisors(requestFromSelection()).collect {
                updateViewState(it)
            }
        }
    }

    private fun requestFromSelection() = VisitsSupervisorsRequestEntity(
        startDate = _viewState.selection.period?.first?.toJsonString(),
        endDate = _viewState.selection.period?.second?.toJsonString(),
        onlyMyVisits = _viewState.selection.onlyMyVisits,
        supervisors = _viewState.selection.supervisors
    )

    private fun filterItems(searchString: String) =
        _viewState.visitsSupervisors.filter {
            searchString.isEmpty() ||
                    it.partner.contains(searchString, true) ||
                    it.outlet.contains(searchString, true)
        }.map { it.id }

    private fun allMarksState(visitsSupervisors: List<VisitSupervisor>): Boolean? {
        val distinctStates = visitsSupervisors.map { it.mark }.distinct()
        return when {
            distinctStates.size == 1 -> distinctStates.first()
            else -> null
        }
    }

    fun reload() = fetchVisitsSupervisors()

    fun changePeriod(date: Date) {
        val localDate = date.toLocalDate()
        _viewState = _viewState.copy(
            selection = _viewState.selection.copy(
                period = localDate to localDate
            )
        )
        fetchVisitsSupervisors()
    }

    fun changeSelection(onlyMyVisits: Boolean, selection: List<String>){
        _viewState = _viewState.copy(
            selection = _viewState.selection.copy(
                onlyMyVisits = onlyMyVisits,
                supervisors = selection
            )
        )
        fetchVisitsSupervisors()
    }

    fun createRepeatStoreChecks(date: LocalDate) {
        safeLaunch {
            visitsSupervisorsRepository.createRepeatStoreCheck(
                RepeatStoreCheckRequestEntity(
                    date = date.toJsonString(),
                    visitsSupervisorIds = _viewState.visitsSupervisors.filter { it.mark }
                        .map { it.id })
            ).collect {
                _repeatStoreCheckCreationResult.publishEvent(it)
            }
        }
    }

    fun changeSearchString(searchString: String) {

        val filteredIds = filterItems(searchString)

        val idSet = filteredIds.toSet()
        val newVisitsSupervisors =
            _viewState.visitsSupervisors.map { if (it.id !in idSet) it.copy(mark = false) else it }


        _viewState = _viewState.copy(
            searchString = searchString,
            totalMark = allMarksState(newVisitsSupervisors),
            filteredIds = filterItems(searchString),
            visitsSupervisors = newVisitsSupervisors
        )
        _viewStateEvent.publishEvent(_viewState)
    }

    fun getListForSubmit(): List<VisitSupervisor> =
        _viewState.visitsSupervisors.filter { it.id in _viewState.filteredIds || _viewState.searchString.isEmpty() }

    fun changeShowMarks() {

        _viewState = _viewState.copy(
            showMarks = !_viewState.showMarks,
            totalMark = false,
            visitsSupervisors = _viewState.visitsSupervisors.map {
                it.copy(
                    showMark = !_viewState.showMarks,
                    mark = false
                )
            })

        _viewStateEvent.publishEvent(_viewState)
    }

    fun changeMarks() {

        _viewState = _viewState.copy(
            totalMark = _viewState.totalMark != true,
            visitsSupervisors = _viewState.visitsSupervisors.map { it.copy(mark = _viewState.totalMark != true) })

        _viewStateEvent.publishEvent(_viewState)
    }

    fun changeMark(id: String) {

        val newVisitsSupervisors =
            _viewState.visitsSupervisors.map { item ->
                if (item.id == id) item.copy(mark = !item.mark) else item
            }

        _viewState = _viewState.copy(
            totalMark = allMarksState(newVisitsSupervisors),
            visitsSupervisors = newVisitsSupervisors
        )

        _viewStateEvent.publishEvent(_viewState)
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val selection: VisitsSupervisorsListSelection = VisitsSupervisorsListSelection(
            period = Pair(
                LocalDate.now(),
                LocalDate.now()
            )
        ),
        val visitsSupervisors: List<VisitSupervisor> = listOf(),
        val filteredIds: List<String> = listOf(),
        val searchString: String = "",
        val showMarks: Boolean = false,
        val totalMark: Boolean? = false
    ) : BaseViewState()
}