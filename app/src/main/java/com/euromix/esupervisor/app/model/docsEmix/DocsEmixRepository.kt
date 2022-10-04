package com.euromix.esupervisor.app.model.docsEmix

import com.euromix.esupervisor.app.model.*
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.app.model.wrapBackendExceptions
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class DocsEmixRepository(
    private val docsEmixSource: DocsEmixSource
) {

    private val docsEmixLazyFlowSubject = LazyFlowSubject<Unit, List<DocEmix>> {
        //wrapBackendExceptions {
        docsEmixSource.getDocsEmix()
        //}

    }

    fun getDocsEmix(): Flow<Result<List<DocEmix>>> {
        return docsEmixLazyFlowSubject.listen(Unit)
    }

    fun reloadDocsEmix() {
        docsEmixLazyFlowSubject.reloadArgument(Unit)
    }

}