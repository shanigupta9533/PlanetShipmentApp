package com.virtual_market.virtualmarket.api

sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: String,val noInternet: Boolean) : Result<Nothing>()
}