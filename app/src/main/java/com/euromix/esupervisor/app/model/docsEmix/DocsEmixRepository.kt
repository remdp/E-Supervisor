package com.euromix.esupervisor.app.model.docsEmix

import com.euromix.esupervisor.app.model.*
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocsEmixRepository @Inject constructor(
    private val docsEmixSource: DocsEmixSource
) {

    private val docsEmixLazyFlowSubject = LazyFlowSubject<Unit, List<DocEmix>> {
        docsEmixSource.getDocsEmix()
    }

    fun getDocsEmix(): Flow<Result<List<DocEmix>>> {
        return docsEmixLazyFlowSubject.listen(Unit)
    }

    fun reloadDocsEmix() {
        docsEmixLazyFlowSubject.reloadArgument(Unit)
    }
}