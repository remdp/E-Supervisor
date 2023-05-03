package com.euromix.esupervisor.screens.main.tabs.docsEmix.selection

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.DocEmixOperationType
import com.euromix.esupervisor.app.enums.Status
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.docsEmix.DocsEmixRepository
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.model.docsEmix.entities.ItemSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocsSelectionViewModel @Inject constructor(
    private val docsEmixRepository: DocsEmixRepository
) : BaseViewModel() {

    private val _foundTradingAgents = MutableLiveData<Result<List<ItemSelection>>>()
    val foundTradingAgents = _foundTradingAgents.share()

    private val _foundPartners = MutableLiveData<Result<List<ItemSelection>>>()
    val foundPartners = _foundPartners.share()

    private val _selection = MutableLiveData<DocsEmixSelection?>()
    val selection = _selection.share()

    private val _errorsMinLength = MutableLiveData<ErrorsMinLength>()
    val errorsMinLength = _errorsMinLength.share()

    init {
        _errorsMinLength.value = ErrorsMinLength()
    }

    fun findTradingAgents(searchString: String) {
        if (verifyMinLength(searchString, 0)) {
            viewModelScope.safeLaunch {
                docsEmixRepository.findTradingAgents(searchString).collect {
                    _foundTradingAgents.value = it
                }
            }
        }
    }

    fun findPartners(searchString: String) {
        if (verifyMinLength(searchString, 1)) {
            viewModelScope.safeLaunch {
                docsEmixRepository.findPartners(searchString).collect {
                    _foundPartners.value = it
                }
            }
        }
    }

    fun updateTradingAgentSelection(tradingAgent: ItemSelection?) {
        _selection.value =
            if (_selection.value == null) DocsEmixSelection(tradingAgent = tradingAgent)
            else _selection.value!!.copy(tradingAgent = tradingAgent)
    }

    fun updatePartnerSelection(partner: ItemSelection?) {
        _selection.value = if (_selection.value == null) DocsEmixSelection(partner = partner)
        else _selection.value!!.copy(partner = partner)
    }

    fun updateOperationTypeSelection(operationType: ItemSelection? = null) {
        _selection.value =
            if (_selection.value == null) DocsEmixSelection(operationType = operationType)
            else _selection.value!!.copy(operationType = operationType)
    }

    fun updateStatusSelection(status: ItemSelection? = null) {
        _selection.value = if (_selection.value == null) DocsEmixSelection(status = status)
        else _selection.value!!.copy(status = status)
    }

    fun checkOperationTypeEmpty(): Boolean = _selection.value?.operationType == null
    fun checkStatusEmpty(): Boolean = _selection.value?.status == null


    fun updateSelection(selection: DocsEmixSelection?) {
        _selection.value = selection
    }

    fun clearSelection() {
        _selection.value = DocsEmixSelection(period = _selection.value?.period)
    }

    fun getOperationTypesForChoose(context: Context): List<ItemSelection> {

        val operationTypesForChoose = mutableListOf<ItemSelection>()

        var count = 0
        DocEmixOperationType.operationTypes().forEach {
            operationTypesForChoose.add(
                ItemSelection(
                    count.toString(),
                    DocEmixOperationType.stringRepresentation(context, it)
                )
            )
            count++
        }
        return operationTypesForChoose
    }

    fun getStatusesForChoose(context: Context): List<ItemSelection> {

        val statusesForChoose = mutableListOf<ItemSelection>()

        var count = 0
        Status.statuses().forEach {
            statusesForChoose.add(
                ItemSelection(
                    count.toString(),
                    Status.stringRepresentation(context, it)
                )
            )
            count++
        }
        return statusesForChoose
    }

    //click
    // 0-common click
    //1-right drawable click
    fun handleViewClick(
        itemsList: List<ItemSelection>,
        updaterSelection: (ItemSelection?) -> Unit,
        anchor: View,
        click: Int,
        emptyChecker: () -> Boolean
    ) {

        if (click == 0 || emptyChecker())
            popupWindow(
                anchor.context,
                itemsList,
                updaterSelection
            ).showAsDropDown(anchor)
        else updaterSelection(null)

    }

    fun popupWindow(
        context: Context,
        list: List<ItemSelection>,
        updaterSelection: (ItemSelection) -> Unit
    ): PopupWindow {

        val popupWindow = PopupWindow()

        val adapter: ArrayAdapter<ItemSelection> = PresentationAdapter(
            context, R.layout.selection_dropdown_item, list
        )

        val listView = ListView(context)
        listView.adapter = adapter
        listView.divider = null

        //todo don't work
        val p = ViewGroup.MarginLayoutParams(0, 0)
        p.topMargin = 20
        listView.layoutParams = p
//        val param = listView.layoutParams as ViewGroup.MarginLayoutParams
        //param.setMargins(0,4,0,0)

        // set on item selected
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                updaterSelection.invoke(list[position])
                popupWindow.dismiss()
            }

        // some other visual settings for popup window
        popupWindow.isFocusable = true
        popupWindow.width = WindowManager.LayoutParams.MATCH_PARENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.setBackgroundDrawable(
            App.getDrawable(
                context,
                R.drawable.bg_8dp_white
            )
        )

        popupWindow.contentView = listView
        return popupWindow
    }

    private fun verifyMinLength(searchString: String, indexVerifyField: Int): Boolean {

        return (searchString.length >= MIN_LENGTH_SEARCH_STRING).also {
            if (!it) {
                when (indexVerifyField) {
                    0 -> _errorsMinLength.value =
                        _errorsMinLength.value?.copy(minLengthTradingAgentError = true)
                    else -> _errorsMinLength.value =
                        _errorsMinLength.value?.copy(minLengthPartnerError = true)
                }
            }
        }
    }

    companion object {
        const val MIN_LENGTH_SEARCH_STRING = 3
    }
}

data class ErrorsMinLength(
    val minLengthTradingAgentError: Boolean = false,
    val minLengthPartnerError: Boolean = false
)