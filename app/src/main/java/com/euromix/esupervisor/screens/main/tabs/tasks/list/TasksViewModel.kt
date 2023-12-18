package com.euromix.esupervisor.screens.main.tabs.tasks.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App.Companion.dateToJsonString
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.tasks.TasksRepository
import com.euromix.esupervisor.app.model.tasks.entities.Task
import com.euromix.esupervisor.app.model.tasks.entities.TasksSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.sources.tasks.list.entities.TasksRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : BaseViewModel() {

    private val _tasks = MutableLiveData<Result<List<Task>>>()
    val tasks = _tasks.share()

    private val _selection = MutableLiveData<TasksSelection?>()
    val selection = _selection.share()

    fun reload() {
        getTasks()
    }
    private fun getTasks() {
        viewModelScope.launch {
            tasksRepository.getTasks(requestFromSelection()).collect {
                _tasks.value = it
            }
        }
    }

    private fun requestFromSelection() =
        TasksRequestEntity(
            startDate = _selection.value?.period?.let { dateToJsonString(it.first) },
            endDate = _selection.value?.period?.let { dateToJsonString(it.second) },
            partnerId = _selection.value?.partner?.id,
            taskTypeId = _selection.value?.taskType?.id,
            taskStateId = _selection.value?.taskState?.id,
            executorId = _selection.value?.executor?.id,
            onlyMy = _selection.value?.onlyMy,
            onlyOverdue = _selection.value?.onlyOverdue
        )

    fun updatePeriod(period: Pair<Date, Date>?) {
        _selection.value = if (_selection.value == null) TasksSelection(
            period = period ?: _selection.value?.period
        )
        else _selection.value!!.copy(period = period)
    }

    fun updateSelection(selection: TasksSelection?) {
        if ((_selection.value == null && selection == null) || selection != null)
            _selection.value = selection
    }
}