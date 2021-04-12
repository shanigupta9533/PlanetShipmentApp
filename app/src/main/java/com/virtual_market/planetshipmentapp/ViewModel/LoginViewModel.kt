package com.virtual_market.planetshipmentapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtual_market.planetshipmentapp.Modal.ResponseTransporter
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.Modal.TransportersModal
import com.virtual_market.planetshipmentapp.Repository.LoginRepository
import com.virtual_market.planetshipmentapp.Repository.OrdersRepository
import com.virtual_market.virtualmarket.api.ApiInterface
import com.virtual_market.virtualmarket.api.Result
import kotlinx.coroutines.launch

class LoginViewModel(private var apiclient: ApiInterface): ViewModel() {

    val user: MutableLiveData<ResponseUserLogin> by lazy {
        MutableLiveData<ResponseUserLogin>()
    }

    val transporterUser: MutableLiveData<ResponseTransporter> by lazy {
        MutableLiveData<ResponseTransporter>()
    }

    val allTransporters: MutableLiveData<TransportersModal> by lazy {
        MutableLiveData<TransportersModal>()
    }

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val noInternet: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val loading: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun loginUserWithUserId(id:String)
    {
        viewModelScope.launch {
            loading.postValue(true)
            val result = LoginRepository(apiclient).userLoginWithUserId(id)
            loading.postValue(false)
            when(result)
            {
                is Result.Success ->
                {
                    user.postValue(result.data)

                }
                is Result.Error ->
                {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }
    }

    fun loginUserWithEmail(request: HashMap<String, String>)
    {
        viewModelScope.launch {
            loading.postValue(true)
            val result = LoginRepository(apiclient).userLogin(request)
            loading.postValue(false)
            when(result)
            {
                is Result.Success ->
                {
                    user.postValue(result.data)

                }
                is Result.Error ->
                {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }
    }


    fun getTransporter() {

        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).getAllTransporters()
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    allTransporters.postValue(result.data)
                }
                is Result.Error -> {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }

    }

    fun loginTransportersWithEmail(request: HashMap<String, String>)
    {
        viewModelScope.launch {
            loading.postValue(true)
            val result = LoginRepository(apiclient).userLoginWithTransporters(request)
            loading.postValue(false)
            when(result)
            {
                is Result.Success ->
                {
                    transporterUser.postValue(result.data)

                }
                is Result.Error ->
                {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }
    }

}