package com.euromix.esupervisor.screens.main.tabs.tasks.selection

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.tasks.entities.TasksCreateOutletsSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OutletsSelectionViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {

    private val initialSearchSelections = mutableListOf<List<SelectionItem>>()
    private val _searchSelections = MutableLiveData<Result<List<List<SelectionItem>>>>()
    val searchSelections = _searchSelections.share()

    private val taSelection = mutableListOf<SelectionItem>()
    private val _taSelectionEvent = MutableLiveEvent<List<SelectionItem>>()
    val taSelectionEvent = _taSelectionEvent.share()

    private var oitSelection = mutableListOf<SelectionItem>()
    private val _oitSelectionEvent = MutableLiveEvent<List<SelectionItem>>()
    val oitSelectionEvent = _oitSelectionEvent.share()

    init {
        searchSelectionsForCreateTasks()
    }

    fun searchSelectionsForCreateTasks() {
        safeLaunch {
            searchRepository.searchSelectionsForCreateTasks().collect { result ->

                var selectionItems = mutableListOf<List<SelectionItem>>()

                val convertedResult = when (result) {
                    is Success -> {

                        val taItems = result.value[INDEX_TA_ITEM]
                        val oitItems = result.value[INDEX_OIT_ITEM]

                        val taItemsSelection = taItems.map {
                            SelectionItem(
                                marked = false, serverPair = it
                            )
                        }

                        val oitItemsSelection = oitItems.map {
                            SelectionItem(
                                marked = false, serverPair = it
                            )
                        }

                        selectionItems.add(INDEX_TA_ITEM, taItemsSelection)
                        selectionItems.add(INDEX_OIT_ITEM, oitItemsSelection)

                        initialSearchSelections.add(INDEX_TA_ITEM, taItemsSelection)
                        initialSearchSelections.add(INDEX_OIT_ITEM, oitItemsSelection)

                        taSelection.addAll(taItemsSelection)
                        oitSelection.addAll(oitItemsSelection)

                        Success(selectionItems.toList())

                    }

                    is Error -> Error(result.error)
                    is Empty -> Empty()
                    else -> Pending()
                }
                _searchSelections.value = convertedResult
            }
        }
    }

    fun outletsSelection() =
        TasksCreateOutletsSelection(
            tradingAgents = taSelection.filter { it.marked }.map { it.serverPair },
            outletsInnerTypes = oitSelection.filter { it.marked }.map { it.serverPair })

    fun drawableForParentCheckBox(index: Int): Int {

        val items = if (index == INDEX_TA_ITEM) taSelection else oitSelection

        val isMarked = items.find { it.marked } != null
        val isUnMarked = items.find { !it.marked } != null

        return if (isMarked && isUnMarked) R.drawable.ic_checkbox_white_indeterminate
        else if (isMarked) R.drawable.ic_checkbox_white_on
        else R.drawable.ic_checkbox_white_off

    }

    fun changeMark(upLevelPos: Int, mark: Boolean, downLevelPos: Int? = null) {

        val currentSS = if (upLevelPos == INDEX_TA_ITEM) taSelection else oitSelection

        if (downLevelPos == null) currentSS.forEach { it.marked = mark }
        else currentSS[downLevelPos].marked = mark

    }

    fun drawableForChildCheckBox(mark: Boolean) =
        if (mark) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off

    fun changeText(upLevelPos: Int, text: Editable) {

        val currentSS = if (upLevelPos == INDEX_TA_ITEM) taSelection else oitSelection
        val event = if (upLevelPos == INDEX_TA_ITEM) _taSelectionEvent else _oitSelectionEvent

        var newISS = initialSearchSelections[upLevelPos].map { it.copy() }

        if (text.isNotBlank()) {
            newISS = newISS.filter {
                it.serverPair.presentation.contains(
                    text.toString(), ignoreCase = true
                )
            }
        }

        newISS.forEach { currentISSItem ->
            val foundSSItem = currentSS.find { it.serverPair == currentISSItem.serverPair }
            currentISSItem.marked = foundSSItem?.marked ?: false
        }

        currentSS.clear()
        currentSS.addAll(newISS)
        event.publishEvent(currentSS)
    }

    fun verifyTA() = taSelection.any { it.marked }


    companion object {

        const val INDEX_TA_ITEM = 0
        const val INDEX_OIT_ITEM = 1

    }
}