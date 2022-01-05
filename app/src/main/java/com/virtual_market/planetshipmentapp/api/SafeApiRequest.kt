@file:Suppress("UNREACHABLE_CODE")

package com.virtual_market.virtualmarket.api

import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import retrofit2.Response
import java.lang.Exception

interface SafeApiRequest {

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {

            val myResp = call.invoke()
            if (myResp.isSuccessful) {
                return Result.Success(myResp.body()!!)
            } else {
                return Result.Error(myResp.errorBody()?.string() ?: "Something goes wrong",false)
            }

        } catch (e: Exception) {
            return Result.Error(e.message ?: "No Internet Connection!",true)
        }
    }
}