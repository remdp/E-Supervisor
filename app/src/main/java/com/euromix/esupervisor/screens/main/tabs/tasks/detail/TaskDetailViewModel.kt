package com.euromix.esupervisor.screens.main.tabs.tasks.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.taskDetail.TaskDetailRepository
import com.euromix.esupervisor.app.model.taskDetail.entities.TaskDetail
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TaskDetailViewModel @AssistedInject constructor(
    @Assisted private val id: String,
    private val taskDetailRepository: TaskDetailRepository
) : BaseViewModel() {

    private val _taskDetail = MutableLiveData<Result<TaskDetail>>()
    val taskDetail = _taskDetail.share()

    init {
        getTaskDetail()
    }

    fun getTaskDetail() {
        viewModelScope.safeLaunch {
            taskDetailRepository.getTask(id).collect { result ->
                _taskDetail.value = result
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): TaskDetailViewModel
    }

}