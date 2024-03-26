package com.euromix.esupervisor.screens.main.tabs.visits.changeType

import com.euromix.esupervisor.app.enums.VisitType
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visits.VisitsRepository
import com.euromix.esupervisor.app.model.visits.entities.ChangeVisitTypeReason
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.visits.entities.VisitsChangeTypeRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangeVisitTypeViewModel @Inject constructor(
    private val visitsRepository: VisitsRepository
) : BaseViewModel() {

    private var _viewState = ViewState(reasons = listOf())
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    private val _changeVisitTypeEvent = MutableLiveEvent<String>()
    val changeVisitTypeEvent = _changeVisitTypeEvent.share()

    init {
        reload()
    }

    private fun getReasons() {
        safeLaunch {
            visitsRepository.getChangeTypeVisitReasons().collect { result ->

                when (result) {
                    is Pending ->
                        _viewState = _viewState.copy(isLoading = true, error = null)

                    is Success -> {
                        _viewState =
                            _viewState.copy(isLoading = false, error = null, reasons = result.value)
                    }

                    is Error -> _viewState =
                        _viewState.copy(isLoading = false, error = result.error)

                    else -> {}
                }
                _viewStateEvent.publishEvent()
            }
        }
    }

    fun changeVisitsType(
        visitType: VisitType,
        ids: List<String>,
        reasonId: String,
        comment: String
    ) {

        _viewState = _viewState.copy(comment = comment)
        safeLaunch {
            visitsRepository.changeVisitsType(
                VisitsChangeTypeRequestEntity(
                    VisitType.visitTypes().indexOf(visitType), ids, reasonId, comment
                )
            ).collect { result ->
                when (result) {
                    is Pending -> {
                        _viewState = _viewState.copy(isLoading = true, error = null)
                        _viewStateEvent.publishEvent()
                    }

                    is Success -> {
                        _changeVisitTypeEvent.publishEvent(result.value.ifEmpty { "0" })

                    }

                    is Error -> {
                        _viewState = _viewState.copy(isLoading = false, error = result.error)
                        _viewStateEvent.publishEvent()
                    }

                    else -> {}
                }
            }
        }
    }

    fun reload() {
        getReasons()
    }

    fun changeReason(reason: ChangeVisitTypeReason) {
        _viewState = _viewState.copy(currentReason = reason)
        _viewStateEvent.publishEvent()
    }

    fun changeReasonText(reason: String) {
        _viewState = _viewState.copy(comment = reason)
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val reasons: List<ChangeVisitTypeReason>,
        val currentReason: ChangeVisitTypeReason? = null,
        val comment: String = ""
    ) : BaseViewState()
}

