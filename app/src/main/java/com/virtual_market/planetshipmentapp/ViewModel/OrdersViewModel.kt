package com.virtual_market.planetshipmentapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.virtual_market.planetshipmentapp.Adapter.InstallationModel
import com.virtual_market.planetshipmentapp.Modal.*
import com.virtual_market.planetshipmentapp.Repository.OrdersRepository
import com.virtual_market.virtualmarket.api.ApiInterface
import com.virtual_market.virtualmarket.api.Result
import kotlinx.coroutines.launch

class OrdersViewModel(private var apiclient: ApiInterface) : ViewModel() {

    val orderDispatch: MutableLiveData<ResponseDispatchOrders> by lazy {
        MutableLiveData<ResponseDispatchOrders>()
    }

    val orderDispatchByParts: MutableLiveData<SerialProductModel> by lazy {
        MutableLiveData<SerialProductModel>()
    }

    val serializedModel: MutableLiveData<SerialProductModel> by lazy {
        MutableLiveData<SerialProductModel>()
    }

    val installationImages: MutableLiveData<InstallationModel> by lazy {
        MutableLiveData<InstallationModel>()
    }

    val documentOnServer: MutableLiveData<SuccessModel> by lazy {
        MutableLiveData<SuccessModel>()
    }

    val allTransporters: MutableLiveData<TransportersModal> by lazy {
        MutableLiveData<TransportersModal>()
    }

    val updateOrdersByParts: MutableLiveData<UpdateOrderModel> by lazy {
        MutableLiveData<UpdateOrderModel>()
    }

    val employeeDetailsModal: MutableLiveData<EmployeeDetailsModal> by lazy {
        MutableLiveData<EmployeeDetailsModal>()
    }

    val ApiCD2: MutableLiveData<CD2Modal> by lazy {
        MutableLiveData<CD2Modal>()
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

    fun dispatchOrdersByParts(orderCode: String) {

        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).ordersDispatchByParts(orderCode)
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    orderDispatchByParts.postValue(result.data)
                }
                is Result.Error -> {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }

    }

    fun getEmployeeDetails() {

        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).getEmployeeDetailsFromparts()
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    employeeDetailsModal.postValue(result.data)
                }
                is Result.Error -> {
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

    fun sendIdsOnServers(hashmap: HashMap<String, String>) {

        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).sendIdsOnServer(hashmap)
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    updateOrdersByParts.postValue(result.data)
                }
                is Result.Error -> {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }

    }

    fun dispatchOrders(map: HashMap<String, String>) {

        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).ordersDispatch(map)
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    orderDispatch.postValue(result.data)
                }
                is Result.Error -> {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }

    }

    fun matchSpoCodeTotheServer(string: String) {
        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).hittingAp2(string)
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    ApiCD2.postValue(result.data)
                }
                is Result.Error -> {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }
    }

    fun serializedModel(code:String) {
        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).serializedModel(code)
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    serializedModel.postValue(result.data)
                }
                is Result.Error -> {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }
    }

    fun getInstallationImages(code:String) {
        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).installationImages(code)
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    installationImages.postValue(result.data)
                }
                is Result.Error -> {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }
    }

    fun sendDocumentOnServer(code:HashMap<String,String>) {
        viewModelScope.launch {
            loading.postValue(true)
            val result = OrdersRepository(apiclient).sendDocumentOnServer(code)
            loading.postValue(false)
            when (result) {
                is Result.Success -> {
                    documentOnServer.postValue(result.data)
                }
                is Result.Error -> {
                    errorMessage.postValue(result.exception)
                    noInternet.postValue(result.noInternet)
                }
            }
        }
    }

}