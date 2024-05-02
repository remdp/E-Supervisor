package com.euromix.esupervisor.screens.main.tabs.tasks.detail

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.taskDetail.TaskDetailRepository
import com.euromix.esupervisor.app.model.taskDetail.entities.TaskDetail
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TaskDetailViewModel @AssistedInject constructor(
    @Assisted private val id: String,
    private val taskDetailRepository: TaskDetailRepository
) : BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value as TaskDetail)
            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccess(value: TaskDetail) {
        _viewState = _viewState.copy(isLoading = false, error = null, taskDetail = value)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getTaskDetail() {
        safeLaunch {
            taskDetailRepository.getTask(id).collect { result ->
                updateViewState(result)
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
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val taskDetail: TaskDetail? = null
    ) : BaseViewState()
}