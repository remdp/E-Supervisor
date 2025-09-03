package com.euromix.esupervisor.screens.main.tabs.filter

import androidx.lifecycle.SavedStateHandle
import com.euromix.esupervisor.app.enums.FilterSource
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.filter.entities.DetailFilterItem
import com.euromix.esupervisor.app.model.filter.entities.FilterSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.usecases.FetchFiltersDataUseCase
import com.euromix.esupervisor.app.usecases.FetchFiltersDataUseCaseFactory
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.utils.toJsonString
import com.euromix.esupervisor.app.utils.toLocalDate
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    useCaseFactory: FetchFiltersDataUseCaseFactory,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    private val filterSource: FilterSource = savedStateHandle.get<FilterSource>("filterSource")
        ?: throw IllegalStateException("Filter source argument is missing")
    private val useCase: FetchFiltersDataUseCase = useCaseFactory.create(filterSource)

    val date: Long = savedStateHandle["date"]
        ?: throw IllegalArgumentException("Missing 'date' argument")

    init {
        val initialSelection: FilterSelection? = savedStateHandle["initialSelection"]
        if (initialSelection != null) {
            restoreSavedSelection(initialSelection)
        } else {
            loadFilterData()
        }
    }

    private fun updateViewState(result: Result<FilterSelection>) {

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

    private fun handleSuccess(value: FilterSelection) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            selection = value
        )
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun restoreSavedSelection(savedSelection: FilterSelection) {
        _viewState = _viewState.copy(selection = savedSelection)
        _viewStateEvent.publishEvent(_viewState)
    }

    fun loadFilterData() {
        safeLaunch {
            useCase(date.toLocalDate().toJsonString()).collect {
                updateViewState(it)
            }
        }
    }

    fun onFlagChecked(index: Int, isChecked: Boolean) {

        val currentSelection = _viewState.selection
        val currentFlags = currentSelection.flags

        val updatedFlags = currentFlags.mapIndexed { i, flagItem ->
            if (i == index) {
                flagItem.copy(flag = isChecked)
            } else {
                flagItem
            }
        }

        val updatedSelection = currentSelection.copy(flags = updatedFlags)
        _viewState = _viewState.copy(selection = updatedSelection)

        _viewStateEvent.publishEvent(_viewState)
    }

    fun onSearchTextChanged(position: Int, newText: String) {
        val currentList = _viewState.selection.items.toMutableList()
        _viewState.selection.items.toMutableList()
        val itemToUpdate = currentList.getOrNull(position) ?: return

        val newDetailList = itemToUpdate.detailFilterItems.map { detailItem ->
            val presentationText = detailItem.serverPair.presentation
            val isMatched = presentationText.contains(newText, ignoreCase = true)

            if (newText.isBlank() || isMatched) {
                detailItem
            }
            else {
                detailItem.copy(marked = false)
            }
        }

        currentList[position] = itemToUpdate.copy(
            searchString = newText,
            detailFilterItems = newDetailList
        )

        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            selection = _viewState.selection.copy(items = currentList)
        )
        _viewStateEvent.publishEvent(_viewState)
    }

    fun onDetailItemMarked(parentPosition: Int, updatedDetailItem: DetailFilterItem) {
        val currentList = _viewState.selection.items.toMutableList()
        val parentItem = currentList.getOrNull(parentPosition) ?: return

        val newDetailList = parentItem.detailFilterItems.map {
            if (it.serverPair.id == updatedDetailItem.serverPair.id) updatedDetailItem else it
        }

        currentList[parentPosition] = parentItem.copy(detailFilterItems = newDetailList)

        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            selection = _viewState.selection.copy(items = currentList)
        )
        _viewStateEvent.publishEvent(_viewState)

    }

    fun onAllItemsChecked(position: Int, isChecked: Boolean) {
        val currentList = _viewState.selection.items.toMutableList()
        val parentItem = currentList.getOrNull(position) ?: return

        val newDetailList = parentItem.detailFilterItems.map { it.copy(marked = isChecked) }

        currentList[position] = parentItem.copy(detailFilterItems = newDetailList)
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            selection = _viewState.selection.copy(items = currentList)
        )
        _viewStateEvent.publishEvent(_viewState)
    }

    fun onClearFilter() {
        _viewState = _viewState.copy(
            selection = FilterSelection(
                flags = _viewState.selection.flags.map { it.copy(flag = false) },
                items = _viewState.selection.items.map { filterItem ->
                    filterItem.copy(detailFilterItems = filterItem.detailFilterItems.map { detailFilterItem ->
                        detailFilterItem.copy(
                            marked = false
                        )
                    })
                })
        )
        _viewStateEvent.publishEvent(_viewState)
    }

    fun filterIsClear()= _viewState.selection.isFilterClear()

    fun getFilter() = _viewState.selection.items

    fun getSelection() = _viewState.selection

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val selection: FilterSelection = FilterSelection(
            flags = listOf(),
            items = listOf()
        )
    ) : BaseViewState()
}