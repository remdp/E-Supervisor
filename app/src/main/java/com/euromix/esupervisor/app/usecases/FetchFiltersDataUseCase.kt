package com.euromix.esupervisor.app.usecases

import com.euromix.esupervisor.app.enums.FilterSource
import kotlinx.coroutines.flow.Flow
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.common.SearchRepository
import com.euromix.esupervisor.app.model.filter.entities.FilterSelection
import javax.inject.Inject

interface FetchFiltersDataUseCase {
    operator fun invoke(date: String): Flow<Result<FilterSelection>>
}

class FetchFiltersDataUseCaseFactory @Inject constructor(
    private val fetchFiltersForCreateTasksUseCaseImpl: FetchFiltersForCreateTasksUseCaseImpl,
    private val fetchFiltersForVisitsSupervisorUseCaseImpl: FetchFiltersForVisitsSupervisorUseCaseImpl

) {
    fun create(source: FilterSource): FetchFiltersDataUseCase {
        return when (source) {
            FilterSource.FROM_CREATE_TASK_FRAGMENT -> fetchFiltersForCreateTasksUseCaseImpl
            FilterSource.FROM_VISITS_SUPERVISORS_FRAGMENT -> fetchFiltersForVisitsSupervisorUseCaseImpl
            else -> throw IllegalArgumentException("Unsupported filter source: $source")
        }
    }
}

class FetchFiltersForCreateTasksUseCaseImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : FetchFiltersDataUseCase {

    override operator fun invoke(date: String): Flow<Result<FilterSelection>> =
        searchRepository.selectionsForCreateTasks()
}

class FetchFiltersForVisitsSupervisorUseCaseImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : FetchFiltersDataUseCase {

    override operator fun invoke(date: String): Flow<Result<FilterSelection>> =
        searchRepository.selectionsForVisitsSupervisors(date)
}