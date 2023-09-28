package com.euromix.esupervisor.dialogs.dialogReactDislike

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.tasks.TasksRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage.ImageViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject

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

        viewModelScope.safeLaunch {

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

//    fun setReason(reason: String) {
//        if (reason != _viewState.value?.reason)
//            _viewState.value = _viewState.value?.copy(reason = reason)
//    }
//
//    fun errors(): List<Int> {
//
//        val errorsList = mutableListOf<Int>()
//
//        if (_viewState.value?.reason.isNullOrBlank()) errorsList.add(R.string.dislike_reason)
//        if (_viewState.value?.deadline !is Success) errorsList.add(R.string.deadline)
//
//        return errorsList
//    }

    @AssistedFactory
    interface Factory {
        fun create(abilityCreateTask: Boolean, id: String?): DialogReactDislikeViewModel
    }

    data class ViewState(
     //   val reason: String = "",
        val createTask: Boolean = false,
        val abilityCreateTask: Boolean = false,
        //  val deadline: Date? = null,
        val deadline: Result<Date> = Empty()
    )

}