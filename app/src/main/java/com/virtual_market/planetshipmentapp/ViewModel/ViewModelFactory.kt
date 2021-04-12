package com.virtual_market.planetshipmentapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.virtual_market.virtualmarket.api.ApiInterface

class ViewModelFactory(private val apiHelper: ApiInterface) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(apiHelper) as T
        }

        if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
            return OrdersViewModel(apiHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}