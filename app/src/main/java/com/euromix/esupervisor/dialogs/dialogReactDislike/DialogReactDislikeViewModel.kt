package com.euromix.esupervisor.dialogs.dialogReactDislike

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.tasks.TasksRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.Date

class DialogReactDislikeViewModel @AssistedInject constructor(
    @Assisted abilityCreateTask: Boolean,
    @Assisted private val id: String?,
    private val tasksRepository: TasksRepository
) : BaseViewModel() {

    private val _viewState = MutableLiveData(ViewState())
    val viewState = _viewState.share()

    init {
        _viewState.value = ViewState(abilityCreateTask = abilityCreateTask)
    }

    private fun getDeadline() {

        safeLaunch {

            id?.let {
                tasksRepository.getNextVisitDate(it).collect { result ->
                    _viewState.value = _viewState.value?.copy(deadline = result)
                }
            }
        }
    }

    fun reload() {
        getDeadline()
    }

    fun onChangeCreateTask(createTask: Boolean) {
        _viewState.value = _viewState.value?.copy(createTask = createTask)
        if (createTask) getDeadline()
    }

    fun setDeadline(deadline: Date) {
        _viewState.value = _viewState.value?.copy(deadline = Success(deadline))
    }

    @AssistedFactory
    interface Factory {
        fun create(abilityCreateTask: Boolean, id: String?): DialogReactDislikeViewModel
    }

    data class ViewState(
        val createTask: Boolean = false,
        val abilityCreateTask: Boolean = false,
        val deadline: Result<Date> = Empty()
    )
}