package com.euromix.esupervisor.app.model.docEmix

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docEmix.entities.DocEmixDetail
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocEmixDetailRepository @Inject constructor(
    private val docEmixDetailSource: DocEmixDetailSource
) {
    fun getDocEmixDetail(extId: String): Flow<Result<DocEmixDetail>> {


        return callbackFlow {

            try {
                trySend(Pending())
                val res = docEmixDetailSource.getDocEmixDetail(extId)
                trySend(Success(res))
            } catch (e: Exception) {
                trySend(Error(e))
            }

            awaitClose { }

        }
    }

    fun acceptDocEmixDetail(extId: String): Flow<Result<DocEmixDetail>> {

        return callbackFlow {

            try {
                trySend(Pending())
                val res = docEmixDetailSource.acceptDocEmixDetail(extId)
                trySend(Success(res))
            } catch (e: Exception) {
                trySend(Error(e))
            }

            awaitClose { }
        }
    }

    fun rejectDocEmixDetail(extId: String, reason: String): Flow<Result<DocEmixDetail>> {

        return callbackFlow {
            try {
                trySend(Pending())
                val res = docEmixDetailSource.rejectDocEmixDetail(extId, reason)
                trySend(Success(res))
            } catch (e: Exception) {
                trySend(Error(e))
            }

            awaitClose { }
        }

    }
}