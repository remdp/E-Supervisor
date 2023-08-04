package com.euromix.esupervisor.app.model.docsEmix

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docsEmix.entities.DocEmix
import com.euromix.esupervisor.sources.docsEmix.entities.DocsEmixRequestEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocsEmixRepository @Inject constructor(
    private val docsEmixSource: DocsEmixSource
) {
    fun getDocsEmix(request: DocsEmixRequestEntity): Flow<Result<List<DocEmix>>> {

        return callbackFlow {

            try {
                trySend(Pending())
                val res = docsEmixSource.getDocsEmix(request)
                trySend(Success(res))
            } catch (e: Exception) {
                trySend(Error(e))
            }

            awaitClose { }

        }
    }
}