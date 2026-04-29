package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list

import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsRepository
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitSupervisor
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.VisitsSupervisorsListSelection
import com.euromix.esupervisor.app.screens.Scrollable
import com.euromix.esupervisor.app.screens.ScrollableDelegate
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.utils.toJsonString
import com.euromix.esupervisor.app.utils.toLocalDate
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.visitsSupervisors.entities.RepeatStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.TransferStoreCheckRequestEntity
import com.euromix.esupervisor.sources.visitsSupervisors.entities.VisitsSupervisorsRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VisitsSupervisorListViewModel @Inject constructor(private val visitsSupervisorsRepository: VisitsSupervisorsRepository) :
    BaseViewModel(), Scrollable by ScrollableDelegate() {

    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    private val _repeatStoreCheckCreationResult = MutableLiveEvent<Result<String>>()
    val repeatStoreCheckCreationResult = _repeatStoreCheckCreationResult.share()

    private val _transferStoreCheckResult = MutableLiveEvent<Result<String>>()
    val transferStoreCheckResult = _transferStoreCheckResult.share()

    init {
        observeSearchQuery()
     //   reload()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(500L) // (!!) Чекаємо 500 мс
            .distinctUntilChanged() // Не реагуємо, якщо текст не змінився
            .onEach { query ->
                // 4. ТІЛЬКИ ТЕПЕР оновлюємо основний viewState
                _viewState.update {
                    it.copy(searchString = query)
                        .deriveFilteredItems()
                        .deriveFilteredMarks()
                        .deriveTotalMark()
                        .deriveDisplayVisits()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateViewState(result: Result<List<VisitSupervisor>>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value)
            is Error -> handleError(result.error)
            else -> {}
        }
    }

    private fun handlePendingState() {
        _viewState.update {
            it.copy(isLoading = true, error = null, totalMark = false)
                .deriveDisplayVisits()
        }
    }

    private fun handleSuccess(value: List<VisitSupervisor>) {
        _viewState.update { currentState ->
            currentState.copy(
                isLoading = false,
                error = null,
                visitsSupervisors = value,
                markedIds = emptySet(),
                totalMark = false
            )
                .deriveFilteredItems()
                .deriveDisplayVisits()
        }
        triggerScrollToTop()
    }

    private fun handleError(error: Throwable) {
        _viewState.update {
            it.copy(isLoading = false, error = error, displayVisits = emptyList())
                .deriveDisplayVisits()
        }
    }

    private fun fetchVisitsSupervisors() {
        safeLaunch {
            visitsSupervisorsRepository.getVisitsSupervisors(requestFromSelection()).collect {
                updateViewState(it)
            }
        }
    }

    private fun requestFromSelection() = VisitsSupervisorsRequestEntity(
        startDate = _viewState.value.selection.period?.first?.toJsonString(),
        endDate = _viewState.value.selection.period?.second?.toJsonString(),
        onlyMyVisits = _viewState.value.selection.onlyMyVisits,
        supervisors = _viewState.value.selection.supervisors
    )

    private fun ViewState.deriveFilteredItems(): ViewState {
        val filteredVisits = when (this.quickFilter) {
            VisitFilter.ALL -> this.visitsSupervisors
            VisitFilter.COMPLETED -> this.visitsSupervisors.filter { it.isDone }
            VisitFilter.UNCOMPLETED -> this.visitsSupervisors.filter { !it.isDone }
        }.filter {
            if (this.searchString.isNotEmpty()) it.partner.contains(
                this.searchString, ignoreCase = true
            ) || it.outlet.contains(this.searchString, ignoreCase = true)
            else true
        }

        return this.copy(
            filteredIdsCanBeChanged = filteredVisits
                .filter { it.canBeRepeated }
                .map { it.extId },
            filteredIds = filteredVisits.map { it.extId }
        )
    }

    private fun ViewState.deriveFilteredMarks(): ViewState {
        val filteredIdsSet = this.filteredIds.toSet()
        val newMarks = this.markedIds.intersect(filteredIdsSet)
        return this.copy(markedIds = newMarks)
    }

    private fun ViewState.toggleMark(extId: String): ViewState {
        val newMarks = if (extId in markedIds) {
            markedIds - extId
        } else {
            markedIds + extId
        }
        return this.copy(markedIds = newMarks)
    }

    private fun ViewState.deriveTotalMark(): ViewState {
        val hasMarkedItems = markedIds.isNotEmpty()
        val hasUnmarkedItems = markedIds.size != filteredIdsCanBeChanged.size
        return this.copy(
            totalMark = when {
                hasMarkedItems && hasUnmarkedItems -> null
                hasMarkedItems -> true
                else -> false
            }
        )
    }

    fun reload() = fetchVisitsSupervisors()

    fun changePeriod(date: Date) {
        val localDate = date.toLocalDate()
        _viewState.update {
            it.copy(
                selection = it.selection.copy(period = localDate to localDate),
                isLoading = true
            )
                .deriveDisplayVisits()
        }
        fetchVisitsSupervisors()
    }

    fun changeSelection(onlyMyVisits: Boolean, selection: List<String>) {
        _viewState.update {
            it.copy(
                selection = it.selection.copy(
                    onlyMyVisits = onlyMyVisits,
                    supervisors = selection,
                ),
                isLoading = true
            ).deriveDisplayVisits()
        }
        fetchVisitsSupervisors()
    }

    fun changeShowMarks() {
        _viewState.update {
            it.copy(
                showMarks = !it.showMarks,
                markedIds = emptySet(),
                totalMark = false
            ).deriveDisplayVisits()
        }
    }

    fun changeMarks() {
        _viewState.update { currentState ->
            val newTotalMark = !(currentState.totalMark ?: true)
            val newMarks = if (newTotalMark) {
                currentState.filteredIdsCanBeChanged.toSet()
            } else {
                emptySet()
            }
            currentState.copy(
                totalMark = newTotalMark,
                markedIds = newMarks
            )
                .deriveDisplayVisits()
                .deriveSelectionType()
        }
    }

    fun changeMark(extId: String) {
        _viewState.update {
            it.toggleMark(extId)
                .deriveTotalMark()
                .deriveDisplayVisits()
                .deriveSelectionType()
        }
    }

    fun changeSearchString(searchString: String) {
        _searchQuery.value = searchString
    }

    fun setupQuickFilter(quickFilter: VisitFilter) {
        _viewState.update {
            it.copy(quickFilter = quickFilter, scrollToTop = true)
                .deriveFilteredItems()
                .deriveFilteredMarks()
                .deriveTotalMark()
                .deriveDisplayVisits()
                .deriveSelectionType()
        }
    }

    private fun ViewState.deriveDisplayVisits(): ViewState {
        val filteredSet = this.filteredIds.toSet()

        val newList = this.visitsSupervisors
            .filter { it.extId in filteredSet }
            .map {
                it.copy(
                    showMark = this.showMarks,
                    mark = (it.extId in this.markedIds)
                )
            }

        return this.copy(displayVisits = newList)
    }

    private fun ViewState.deriveSelectionType(): ViewState {
        val selectedItems = this.visitsSupervisors.filter { it.extId in this.markedIds }

        val filter = if (selectedItems.isEmpty()) {
            VisitFilter.ALL
        } else {

            val allCompleted = selectedItems.all { it.isDone }
            val allUncompleted = selectedItems.all { !it.isDone }

            when {
                allCompleted -> VisitFilter.COMPLETED
                allUncompleted -> VisitFilter.UNCOMPLETED
                else -> VisitFilter.ALL
            }
        }



        return this.copy(selectionType = filter)
    }

    fun onScrolledToTop() {
        _viewState.update { it.copy(scrollToTop = false) }
    }

    fun createRepeatStoreChecks(date: LocalDate) {
        safeLaunch {
            visitsSupervisorsRepository.createRepeatStoreCheck(
                RepeatStoreCheckRequestEntity(
                    date = date.toJsonString(),
                    visitsSupervisorIds = _viewState.value.markedIds.toList()
                )
            ).collect {
                if (it is Success) {
                    reload()
                }
                _repeatStoreCheckCreationResult.publishEvent(it)
            }
        }
    }

    fun transferStoreChecks(date: LocalDate) {
        safeLaunch {
            visitsSupervisorsRepository.transferStoreCheck(
                TransferStoreCheckRequestEntity(
                    date = date.toJsonString(),
                    visitsSupervisorIds = _viewState.value.markedIds.toList()
                )
            ).collect {
                if (it is Success) {
                    reload()
                }
                _transferStoreCheckResult.publishEvent(it)
            }
        }
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
        val filteredIdsCanBeChanged: List<String> = listOf(),
        val markedIds: Set<String> = emptySet(),
        val searchString: String = "",
        val showMarks: Boolean = false,
        val totalMark: Boolean? = false,
        val quickFilter: VisitFilter = VisitFilter.ALL,
        val scrollToTop: Boolean = false,
        val displayVisits: List<VisitSupervisor> = emptyList(),
        val selectionType: VisitFilter = VisitFilter.ALL
    ) : BaseViewState()
}

enum class VisitFilter {
    ALL,
    COMPLETED,
    UNCOMPLETED
}