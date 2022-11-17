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

            if (e.code() == 403) throw AuthException(e)

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

        val jsonFromServer = e.response()?.errorBody()?.string()

        val stringError = try {
            errorAdapter.fromJson(jsonFromServer)?.error
        }catch (e: Exception){
            jsonFromServer
        }

        return BackendException(e.code(), e, stringError ?: e.message)
    }

    class ErrorResponseBody(
        val error: String
    )
}