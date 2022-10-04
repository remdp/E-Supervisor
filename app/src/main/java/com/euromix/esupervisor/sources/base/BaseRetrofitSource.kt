package com.euromix.esupervisor.sources.base

import com.euromix.esupervisor.app.model.*
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import java.io.IOException

open class BaseRetrofitSource(retrofitConfig: RetrofitConfig) {

    val retrofit = retrofitConfig.retrofit

    private val moshi = retrofitConfig.moshi
    private val errorAdapter = moshi.adapter(ErrorResponseBody::class.java)

    suspend fun <T> wrapRetrofitException(block: suspend () -> T): T {

        return try {
            block()
        }
        //moshi
        catch (e: JsonDataException) {
            throw ParseBackendResponseException(e)
        } catch (e: JsonEncodingException) {
            throw ParseBackendResponseException(e)
        }
        //retrofit
        catch (e: HttpException) {
            throw createBackendException(e)
        }
        //mostly retrofit
        catch (e: IOException) {
            throw ConnectionException(e)
        } catch (e: AppException) {
            throw e
        }
    }

    private fun createBackendException(e: HttpException): Exception {
        return try {
            val d = e.response()!!.errorBody()!!.string()
            val errorBody: ErrorResponseBody = errorAdapter.fromJson(
                d
            )!!
            if (e.code() == 403)
                 AuthExceptionWithMessage(e,errorBody.error)
            else
                 AuthException(e)
            //BackendException(e.code(), errorBody.error)

        } catch (e: Exception) {
            //throw ParseBackendResponseException(e)
            throw AuthException(e)
        }
    }

    class ErrorResponseBody(
        val error: String
    )
}