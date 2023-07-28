package com.euromix.esupervisor.screens.main.tabs.tasks.selection

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.enums.TaskState
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.tasks.entities.TasksSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksSelectionViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {

    private val _foundPartners = MutableLiveData<Result<List<ServerPair>>>()
    val foundPartners = _foundPartners.share()

    private val _foundTasksType = MutableLiveData<Result<List<ServerPair>>>()
    val foundTasksType = _foundTasksType.share()

    private val _foundExecutors = MutableLiveData<Result<List<ServerPair>>>()
    val foundExecutors = _foundExecutors.share()

    private val _selection = MutableLiveData<TasksSelection?>()
    val selection = _selection.share()

    private val _errorsMinLength = MutableLiveData<ErrorsMinLength>()
    val errorsMinLength = _errorsMinLength.share()

    init {
        findTasksType()
        _errorsMinLength.value = ErrorsMinLength()
    }

    fun findPartners(searchString: String) {
        if (verifyMinLength(searchString, 1)) {
            viewModelScope.safeLaunch {
                searchRepository.findPartners(searchString).collect {
                    _foundPartners.value = it
                }
            }
        }
    }

    private fun findTasksType() {
        viewModelScope.safeLaunch {
            searchRepository.findTasksType().collect {
                 _foundTasksType.value = it
            }
        }
    }

    fun findExecutors(searchString: String) {
        if (verifyMinLength(searchString, 2)) {
            viewModelScope.safeLaunch {
                searchRepository.findExecutors(searchString).collect {
                    _foundExecutors.value = it
                }
            }
        }
    }

    fun updatePartnerSelection(partner: ServerPair?) {
        _selection.value = if (_selection.value == null) TasksSelection(partner = partner)
        else _selection.value!!.copy(partner = partner)
    }

    fun updateTasksTypeSelection(taskType: ServerPair?) {
        _selection.value =
            if (_selection.value == null) TasksSelection(taskType = taskType)
            else _selection.value!!.copy(taskType = taskType)
    }

    fun updateTaskStateSelection(taskState: ServerPair? = null) {
        _selection.value = if (_selection.value == null) TasksSelection(taskState = taskState)
        else _selection.value!!.copy(taskState = taskState)
    }

    fun updateExecutorsSelection(executor: ServerPair?) {
        _selection.value = if (_selection.value == null) TasksSelection(executor = executor)
        else _selection.value!!.copy(executor = executor)
    }

    fun updateOnlyMy(onlyMy: Boolean) {
        _selection.value = if (_selection.value == null) TasksSelection(onlyMy = onlyMy)
        else _selection.value!!.copy(onlyMy = onlyMy)
    }

    fun updateOnlyOverdue(onlyOverdue: Boolean) {
        _selection.value = if (_selection.value == null) TasksSelection(onlyOverdue = onlyOverdue)
        else _selection.value!!.copy(onlyOverdue = onlyOverdue)
    }

    fun checkTaskStateEmpty(): Boolean = _selection.value?.taskState == null
    fun checkTaskTypeEmpty(): Boolean = _selection.value?.taskType == null

    fun updateSelection(selection: TasksSelection?) {
        _selection.value = selection
    }

    fun clearSelection() {
        _selection.value = TasksSelection(period = _selection.value?.period)
    }

    fun getTasksStateForChoose(context: Context): List<ServerPair> {

        val taskStatesForChoose = mutableListOf<ServerPair>()

        var count = 0
        TaskState.taskStates().forEach {
            taskStatesForChoose.add(
                ServerPair(
                    count.toString(),
                    TaskState.stringRepresentation(context, it)
                )
            )
            count++
        }
        return taskStatesForChoose
    }

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

    private fun verifyMinLength(searchString: String, indexVerifyField: Int): Boolean {

        return (searchString.length >= Const.MIN_LENGTH_SEARCH_STRING).also {
            if (!it) {
                when (indexVerifyField) {
                    0 -> _errorsMinLength.value =
                        _errorsMinLength.value?.copy(minLengthPartnerError = true)
                    else -> _errorsMinLength.value =
                        _errorsMinLength.value?.copy(minLengthExecutorError = true)
                }
            }
        }
    }
}

data class ErrorsMinLength(
    val minLengthPartnerError: Boolean = false,
    val minLengthExecutorError: Boolean = false
)