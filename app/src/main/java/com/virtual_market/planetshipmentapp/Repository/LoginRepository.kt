package com.virtual_market.planetshipmentapp.Repository

import com.virtual_market.planetshipmentapp.Modal.ResponseTransporter
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.virtualmarket.api.ApiInterface
import com.virtual_market.virtualmarket.api.Result
import com.virtual_market.virtualmarket.api.SafeApiRequest

class LoginRepository(private val apiClient: ApiInterface) : SafeApiRequest {

    suspend fun userLogin(request: HashMap<String, String>): Result<ResponseUserLogin> {
        return safeApiCall { apiClient.createUserByEmail(request) }
    }

    suspend fun userLoginWithUserId(id:String): Result<ResponseUserLogin> {
        return safeApiCall { apiClient.createUserByUserId(id) }
    }

    suspend fun userLoginWithTransporters(request: HashMap<String, String>): Result<ResponseTransporter> {
        return safeApiCall { apiClient.userTransportersLogin(request) }
    }

}