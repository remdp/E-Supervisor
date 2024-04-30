package com.euromix.esupervisor.dialogs.dialogReactDislike

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.tasks.TasksRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.Date

class DialogReactDislikeViewModel @AssistedInject constructor(
    @Assisted abilityCreateTask: Boolean,
    @Assisted private val id: String?,
    private val tasksRepository: TasksRepository
) : BaseViewModel() {

    private var _viewState: ViewState = ViewState(abilityCreateTask = abilityCreateTask)
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        _viewStateEvent.publishEvent()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> {
                handleSuccess(result.value as Date)
            }

            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccess(value: Date) {
        _viewState =
            _viewState.copy(isLoading = false, error = null, deadline = value)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getDeadline() {
        safeLaunch {
            id?.let {
                tasksRepository.getNextVisitDate(it).collect { result ->
                    updateViewState(result)
                }
            }
        }
    }

    fun reload() {
        getDeadline()
    }

    fun onChangeCreateTask(createTask: Boolean) {
        _viewState = _viewState.copy(createTask = createTask)
        if (createTask) getDeadline() else _viewStateEvent.publishEvent()
    }

    fun setDeadline(deadline: Date) {
        _viewState = _viewState.copy(deadline = deadline)
    }

    @AssistedFactory
    interface Factory {
        fun create(abilityCreateTask: Boolean, id: String?): DialogReactDislikeViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val createTask: Boolean = false,
        val abilityCreateTask: Boolean,
        val deadline: Date? = null
    ) : BaseViewState()
}