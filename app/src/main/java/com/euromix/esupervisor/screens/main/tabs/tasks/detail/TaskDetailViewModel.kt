package com.euromix.esupervisor.screens.main.tabs.tasks.detail

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
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

    private val _viewState = MutableLiveData(ViewState(needLoading = true))
    val viewState = _viewState.share()
    private fun updateResult(result: Result<*>) {
        viewState.value?.let { stateValue ->
            when (result) {
                is Pending -> _viewState.value =
                    stateValue.copy(needLoading = false, result = Pending())

                is Success -> _viewState.value =
                    viewState.value?.copy(result = Success(result.value as TaskDetail))

                is Error -> _viewState.value =
                    stateValue.copy(
                        needLoading = false,
                        result = Error(result.error)
                    )

                else -> _viewState.value = stateValue.copy(needLoading = false)
            }
        }
    }

    private fun getTaskDetail() {
        safeLaunch {
            taskDetailRepository.getTask(id).collect { result ->
                updateResult(result)
            }
        }
    }

    fun afterUpdateState() {
        viewState.value?.let { stateValue ->
            if (stateValue.needLoading) {
                getTaskDetail()
            }
        }
    }

    fun reload() {
        getTaskDetail()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): TaskDetailViewModel
    }

    data class ViewState(
        val needLoading: Boolean = true,
        val result: Result<TaskDetail> = Empty()
    )
}