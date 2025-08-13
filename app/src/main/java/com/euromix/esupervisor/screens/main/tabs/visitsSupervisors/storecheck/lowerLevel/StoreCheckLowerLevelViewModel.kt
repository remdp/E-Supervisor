package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.lowerLevel

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.storeCheck.StoreCheckRepository
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevel
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevelRow
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.Event
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.topLevel.StoreCheckTopLevelViewModel.ViewState
import com.euromix.esupervisor.sources.storeCheck.entities.Row
import com.euromix.esupervisor.sources.storeCheck.entities.StoreCheckRequestEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class StoreCheckLowerLevelViewModel @AssistedInject constructor(
    @Assisted("extId") private val extId: String,
    @Assisted("topLevelExtId") private val topLevelExtId: String?,
    private val storeCheckRepository: StoreCheckRepository
) : BaseViewModel() {

    private val _navigateBackEvent = MutableLiveEvent<Unit>()
    val navigateBackEvent = _navigateBackEvent.share()

    private var _viewState: ViewState? = null
    val viewState: ViewState?
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        _viewState =
            ViewState(extId = extId, topLevelExtId = topLevelExtId, data = StoreCheckLowerLevel())
        reload()
    }

    private fun <T> updateViewState(result: Result<T>) {

        _viewState?.let {
            when (result) {
                is Pending -> handlePendingState()
                is Success -> handleSuccess(result.value)
                is Error -> handleError(result.error)
                else -> {}
            }
            _viewStateEvent.publishEvent(it)
        }
    }

    private fun handlePendingState() {
        _viewState = _viewState?.copy(isLoading = true, error = null)
    }

    private fun <T> handleSuccess(value: T) {

        when (value) {
            is StoreCheckLowerLevel -> _viewState = _viewState?.copy(
                isLoading = false,
                error = null,
                data = StoreCheckLowerLevel(
                    isBasis = value.isBasis,
                    availabilityPos = value.availabilityPos,
                    pos = value.pos,
                    posBasis = value.posBasis,
                    rows = value.rows,
                    storeCheckFormat = value.storeCheckFormat,
                    shelfShare = value.shelfShare,
                    newProducts = value.newProducts,
                    isCheckOut = value.isCheckOut,
                    number = value.number,
                    partner = value.partner,
                    outlet = value.outlet,
                    tradeAgent = value.tradeAgent,
                    tasksCount = value.tasksCount
                )
            )

            else -> _navigateBackEvent.postValue(Event(Unit))
        }
    }


    private fun handleError(error: Throwable) {
        _viewState = _viewState?.copy(isLoading = false, error = error)
    }

    private fun getStoreCheckLowerLevel() {
        safeLaunch {
            _viewState?.let { viewState ->
                storeCheckRepository.getStoreCheckLowerLevel(
                    viewState.extId,
                    viewState.topLevelExtId
                ).collect {
                    updateViewState(it)
                }
            }
        }
    }

    fun postStoreCheck() {
        safeLaunch {

            _viewState?.let { viewState ->
                storeCheckRepository.postStoreCheck(
                    viewState.extId, StoreCheckRequestEntity(
                        pos = viewState.data.pos,
                        rows = viewState.data.rows.map {
                            Row(
                                rowNumber = it.rowNumber,
                                accountingBoolean = it.accountingBoolean,
                                accountingInt = it.accountingInt,
                                amountFacing = it.amountFacing,
                                amountFacingTotal = it.amountFacingTotal,
                                shelfLength = it.shelfLength,
                                shelfLengthTotal = it.shelfLengthTotal,
                                newProducts = it.newProducts
                            )
                        }
                    )).collect { updateViewState(it) }
            }


        }
    }

    fun reload() {
        getStoreCheckLowerLevel()
    }

    fun getListForSubmit(): List<StoreCheckLowerLevelRow> = _viewState?.data?.rows ?: listOf()

    fun onChangedPos(newValue: Boolean) {
        _viewState?.let { state ->
            _viewState = state.copy(data = state.data.copy(pos = newValue))
        }
    }

    fun onChangedData(position: Int, changedData: String, value: Any) {
        _viewState?.let { state ->
            val updatedList = state.data.rows.toMutableList().apply {
                this[position] = when (changedData) {
                    StoreCheckLowerLevelRow.ACCOUNTING_BOOLEAN -> this[position].copy(
                        accountingBoolean = value as Boolean
                    )

                    StoreCheckLowerLevelRow.ACCOUNTING_INT -> this[position].copy(accountingInt = value as Int)
                    StoreCheckLowerLevelRow.AMOUNT_FACING -> this[position].copy(amountFacing = value as Int)
                    StoreCheckLowerLevelRow.AMOUNT_FACING_TOTAL -> this[position].copy(
                        amountFacingTotal = value as Int
                    )

                    StoreCheckLowerLevelRow.SHELF_LENGTH -> this[position].copy(shelfLength = value as Int)
                    StoreCheckLowerLevelRow.SHELF_LENGTH_TOTAL -> this[position].copy(
                        shelfLengthTotal = value as Int
                    )

                    StoreCheckLowerLevelRow.NEW_PRODUCTS -> this[position].copy(newProducts = value as Int)
                    else -> this[position]
                }
            }
            _viewState = state.copy(data = state.data.copy(rows = updatedList))
        }
    }

    fun plusTasksCount() {
        _viewState?.let { state ->
            _viewState = state.copy(data = state.data.copy(tasksCount = state.data.tasksCount + 1))
            _viewStateEvent.publishEvent(state)

        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("extId") extId: String,
            @Assisted("topLevelExtId") topLevelExtId: String?
        ): StoreCheckLowerLevelViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val extId: String,
        val topLevelExtId: String?,
        val data: StoreCheckLowerLevel
    ) : BaseViewState()
}