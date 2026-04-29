package com.euromix.esupervisor.screens.main.tabs.tasks.createTask

import androidx.lifecycle.MutableLiveData
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.tasks.TasksRepository
import com.euromix.esupervisor.app.model.tasks.entities.TasksCreateOutletsSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.utils.toJsonString
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

    var attachPhoto: Boolean = false

    private var _storeCheckId: String? = null

    private var currentSearchQuery: String = ""

    private var lastRequestParams: OutletsForCreateTaskRequestEntity? = null

    private val _photosBase64 = MutableLiveData<List<String>>(emptyList())
    val photosBase64 = _photosBase64.share()

    init {
        updateChosenTaskType(null)
        findTasksType()
    }

    private fun findTasksType() {
        safeLaunch {
            searchRepository.findTasksType().collect {
                _foundTasksType.value = it
            }
        }
    }

    fun createTasks(description: String) {
        safeLaunch {

            val request = if (_storeCheckId == null) {

                val chosenOutlets = (outlets.value as Success).value
                    .filter { it.marked }
                    .map { it.outlet.serverPair.id }
                if (chosenOutlets.isNotEmpty()) {

                    TasksCreateRequestEntity(
                        deadline = (deadline ?: Calendar.getInstance().time).toJsonString(),
                        taskTypeId = _chosenTasksType.value!!.id,
                        tradingAgentIds = chosenTA,
                        description = description,
                        outletsIds = chosenOutlets,
                        attachPhoto = attachPhoto,
                        photos = _photosBase64.value
                    )
                } else {
                    return@safeLaunch
                }
            } else {
                TasksCreateRequestEntity(
                    deadline = (deadline ?: Calendar.getInstance().time).toJsonString(),
                    taskTypeId = _chosenTasksType.value!!.id,
                    description = description,
                    attachPhoto = attachPhoto,
                    storeCheckId = _storeCheckId,
                    photos = _photosBase64.value
                )
            }

            val cf = tasksRepository.createTasks(request)
            cf.collect { _tasksCreationResult.publishEvent(it) }


        }
    }

    fun findOutletsForCreateTask(selection: TasksCreateOutletsSelection?) {
        safeLaunch {

            val request = OutletsForCreateTaskRequestEntity(
                tradingAgents = selection?.tradingAgents,
                outletsInnerTypes = selection?.outletsInnerTypes
            )

            if (request == lastRequestParams && initialOutlets.isNotEmpty()) {
                if (currentSearchQuery.isNotBlank()) {
                    filteredBy(currentSearchQuery)
                } else {
                    _outlets.value = Success(initialOutlets.toMutableList())
                }
                return@safeLaunch
            }

            lastRequestParams = request
            initialOutlets.clear()
             currentSearchQuery = ""

            initialOutlets.clear()

            if (request.isEmpty()) {
                _outlets.value = Success(mutableListOf())
            } else {
                searchRepository.findOutletsForCreateTask(request).collect { result ->

                    when (result) {
                        is Success -> {
                            result.value.map { it.toSelectionItemOutlet() }
                                .also { selectionItemOutlets ->


                                    val list = selectionItemOutlets.toMutableList()
                                    initialOutlets.addAll(list.map { it.copy() })

                                    if (currentSearchQuery.isNotBlank()) {
                                        filteredBy(currentSearchQuery)
                                    } else {
                                        _outlets.value = Success(list)
                                    }
                                }
                        }

                        is Pending -> _outlets.value = Pending()
                        is Error -> _outlets.value = Error(result.error)
                        else -> {}
                    }
                }
            }
        }
    }

    fun updateOutletsSelection(selection: TasksCreateOutletsSelection) {
        chosenTA = selection.tradingAgents
        chosenOutletsInnerTypes = selection.outletsInnerTypes
        _outletsSelection.publishEvent(selection)
    }

    fun updateChosenTaskType(taskType: ServerPair?) {
        _chosenTasksType.value = taskType
    }

    fun checkTaskTypeEmpty(): Boolean = _chosenTasksType.value == null

    fun changeMark(checked: Boolean, selectionItemOutlet: SelectionItemOutlet? = null) {
        _outlets.value?.let { outletsResult ->
            if (outletsResult is Success) {
                val items = outletsResult.value
                if (selectionItemOutlet == null) {
                    items.forEach { it.marked = checked }
                } else {
                    items.find { it.outlet == selectionItemOutlet.outlet }?.marked = checked
                }
            }
        }

        if (selectionItemOutlet == null) {
            initialOutlets.forEach { it.marked = checked }
        } else {
            initialOutlets.find { it.outlet == selectionItemOutlet.outlet }?.marked = checked
        }
    }

    fun filteredBy(text: Any?) {
        val query = text.toString()

        currentSearchQuery = query

        val newList = if (query.isBlank()) {
            initialOutlets.toMutableList()
        } else {
            initialOutlets.filter {
                it.outlet.serverPair.presentation.contains(query, ignoreCase = true) ||
                        it.outlet.owner.contains(query, ignoreCase = true)
            }.toMutableList()
        }

        _outlets.value = Success(newList)
    }

    fun drawableForParentCheckBox(): Int {

        _outlets.value?.let { outletsResult ->

            val items = (outletsResult as Success).value

            val isMarked = items.find { it.marked } != null
            val isUnMarked = items.find { !it.marked } != null

            return if (isMarked && isUnMarked) R.drawable.ic_checkbox_white_indeterminate
            else if (isMarked) R.drawable.ic_checkbox_white_on
            else R.drawable.ic_checkbox_white_off
        }

        return R.drawable.ic_checkbox_white_off
    }

    fun drawableForChildCheckBox(mark: Boolean) =
        if (mark) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off

    fun setStoreCheckId(storeCheckId: String?) {
        _storeCheckId = storeCheckId
    }

    fun selectPicture(photoBase64String: String) {
        val currentList = _photosBase64.value.orEmpty().toMutableList()
        currentList.add(photoBase64String)
        _photosBase64.value = currentList
    }

    fun removePhoto(position: Int) {
        val currentList = _photosBase64.value.orEmpty().toMutableList()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            _photosBase64.value = currentList
        }
    }

    fun verifyPossibilityCreation(description: String): List<Int> {

        val errors = mutableListOf<Int>()

        val checkDeadline = deadline != null
        if (!checkDeadline) errors.add(R.string.deadline)

        val checkChosenTasksType = chosenTasksType.value != null
        if (!checkChosenTasksType) errors.add(R.string.task_type)

        val checkDescription = description.isNotBlank()
        if (!checkDescription) errors.add(R.string.description)

        if (_storeCheckId == null) {

            val checkTradingAgents = chosenTA.isNotEmpty()
            if (!checkTradingAgents) errors.add(R.string.trading_agents)

            val checkOutlets =
                if (outlets.value == null) false else (outlets.value as Success).value.any { it.marked }
            if (!checkOutlets) errors.add(R.string.outlets)
        }

        return errors
    }
}