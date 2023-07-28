package com.euromix.esupervisor.screens.main.tabs.tasks.createTask

import android.text.Editable
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.tasks.TasksRepository
import com.euromix.esupervisor.app.model.tasks.entities.TasksCreateOutletsSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.tabs.tasks.selection.SelectionItemOutlet
import com.euromix.esupervisor.sources.tasks.createTask.entities.OutletsForCreateTaskRequestEntity
import com.euromix.esupervisor.sources.tasks.createTask.entities.TasksCreateRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val searchRepository: SearchRepository, private val tasksRepository: TasksRepository
) : BaseViewModel() {

    private val initialOutlets = mutableListOf<SelectionItemOutlet>()
    private val _outlets = MutableLiveData<Result<MutableList<SelectionItemOutlet>>>()
    val outlets = _outlets.share()

    private val _foundTasksType = MutableLiveData<Result<List<ServerPair>>>()
    val foundTasksType = _foundTasksType.share()

    private val _tasksCreationResult = MutableLiveEvent<Result<String>>()
    val tasksCreationResult = _tasksCreationResult.share()

    private var chosenTA = listOf<String>()
    private var chosenOutletsInnerTypes: List<String>? = listOf()

    private val _outletsSelection = MutableLiveEvent<TasksCreateOutletsSelection?>()
    val outletsSelection = _outletsSelection.share()

    var deadline: Date? = null
    private val _chosenTasksType = MutableLiveData<ServerPair?>()
    val chosenTasksType = _chosenTasksType.share()

    init {
        updateChosenTaskType(null)
        findTasksType()
    }

    private fun findTasksType() {
        viewModelScope.safeLaunch {
            searchRepository.findTasksType().collect {
                _foundTasksType.value = it
            }
        }
    }

    fun createTasks(description: String) {
        viewModelScope.safeLaunch {
            val chosenOutlets = (outlets.value as Success).value
                .filter { it.marked }
                .map { it.outlet.serverPair.id }

            if (chosenOutlets.isNotEmpty()) {

                val cf = tasksRepository.createTasks(
                    TasksCreateRequestEntity(
                        deadline = App.formattedDate(deadline ?: Calendar.getInstance().time),
                        taskTypeId = _chosenTasksType.value!!.id,
                        tradingAgentIds = chosenTA,
                        description = description,
                        outletsIds = chosenOutlets
                    )
                )
                cf.collect { _tasksCreationResult.publishEvent(it) }
            }
        }
    }

    fun findOutletsForCreateTask(selection: TasksCreateOutletsSelection?) {
        viewModelScope.safeLaunch {

            val request = OutletsForCreateTaskRequestEntity(
                tradingAgents = selection?.tradingAgents?.map { it.id },
                outletsInnerTypes = selection?.outletsInnerTypes?.map { it.id }
            )

            initialOutlets.clear()

            if (request.isEmpty()) {
                _outlets.value = Success(mutableListOf())
            } else {
                searchRepository.findOutletsForCreateTask(request).collect { result ->
                    if (result is Success) {
                        result.value.map { it.toSelectionItemOutlet() }
                            .also { selectionItemOutlets ->
                                _outlets.value = Success(selectionItemOutlets.toMutableList())
                                initialOutlets.addAll(selectionItemOutlets.map { it.copy() })
                            }
                    } else if (result is Pending) {
                        _outlets.value = Pending()
                    } else {
                        //todo fix it
                        //_outlets.value = Error(result as Throwable)
                    }
                }
            }
        }
    }

    fun updateOutletsSelection(selection: TasksCreateOutletsSelection) {
        chosenTA = selection.tradingAgents.map { it.id }
        chosenOutletsInnerTypes = selection.outletsInnerTypes?.map { it.id }
        _outletsSelection.publishEvent(selection)
    }

    fun updateChosenTaskType(taskType: ServerPair?) {
        _chosenTasksType.value = taskType
    }

    fun checkTaskTypeEmpty(): Boolean = _chosenTasksType.value == null

    fun changeMark(checked: Boolean, selectionItemOutlet: SelectionItemOutlet? = null) {

        val items = (_outlets.value as Success).value

        if (selectionItemOutlet == null) {
            items.forEach { it.marked = checked }
        } else {
            items.find { it.outlet == selectionItemOutlet.outlet }?.marked = checked
            //  initialOutlets.find { it.outlet == selectionItemOutlet.outlet }?.marked = checked
        }
    }

    fun filteredBy(text: Editable?) {

        val newList = if (text.isNullOrBlank()) initialOutlets
        else initialOutlets.filter {
            it.outlet.serverPair.presentation.contains(
                text.toString(),
                ignoreCase = true
            ) || it.outlet.owner.contains(
                text.toString(),
                ignoreCase = true
            )
        }.toMutableList()

        _outlets.value?.let { result ->
            val currentList = (result as Success).value
            newList.forEach { itemNewList ->
                val foundItem = currentList.find { it.outlet == itemNewList.outlet }
                itemNewList.marked = foundItem?.marked ?: false
            }
        }

        _outlets.value = Success(newList)
    }

    fun drawableForParentCheckBox(): Int {

        val items = (_outlets.value as Success).value

        val isMarked = items.find { it.marked } != null
        val isUnMarked = items.find { !it.marked } != null

        return if (isMarked && isUnMarked) R.drawable.ic_checkbox_white_indeterminate
        else if (isMarked) R.drawable.ic_checkbox_white_on
        else R.drawable.ic_checkbox_white_off

    }

    fun drawableForChildCheckBox(mark: Boolean) =
        if (mark) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off


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

    fun verifyPossibilityCreation(description: String): List<Int> {

        val errors = mutableListOf<Int>()

        val checkDeadline = deadline != null
        if (!checkDeadline) errors.add(R.string.deadline)

        val checkChosenTasksType = chosenTasksType.value != null
        if (!checkChosenTasksType) errors.add(R.string.task_type)

        val checkDescription = description.isNotBlank()
        if(!checkDescription) errors.add(R.string.description)

        val checkTradingAgents = chosenTA.isNotEmpty()
        if (!checkTradingAgents) errors.add(R.string.trading_agents)

        val checkOutlets =
            if (outlets.value == null) false else (outlets.value as Success).value.any { it.marked }
        if (!checkOutlets) errors.add(R.string.outlets)

        return errors
    }


}