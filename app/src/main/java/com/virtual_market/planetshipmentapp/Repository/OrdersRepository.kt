package com.virtual_market.planetshipmentapp.Repository

import com.virtual_market.planetshipmentapp.Modal.InstallationModel
import com.virtual_market.planetshipmentapp.Modal.*
import com.virtual_market.virtualmarket.api.ApiInterface
import com.virtual_market.virtualmarket.api.Result
import com.virtual_market.virtualmarket.api.SafeApiRequest


class OrdersRepository(private val apiClient: ApiInterface) : SafeApiRequest {

    suspend fun ordersDispatch(map: HashMap<String, String>): Result<ResponseDispatchOrders> {
        return safeApiCall { apiClient.OrdersDispatch(map) }
    }

    suspend fun ordersDispatchByParts(orderCode:String): Result<SerialProductModel> {
        return safeApiCall { apiClient.orderDispatchByParts(orderCode) }
    }

    suspend fun hittingAp2(orderCode:String): Result<CD2Modal> {
        return safeApiCall { apiClient.hiddingCD2(orderCode) }
    }

    suspend fun updateOrdersUpdateStatus(hashmap: HashMap<String, String>): Result<UpdateOrderModel> {
        return safeApiCall { apiClient.updateOrdersUpdateStatus(hashmap) }
    }

    suspend fun getEmployeeDetailsFromparts(): Result<EmployeeDetailsModal> {
        return safeApiCall { apiClient.employeesDetails() }
    }

    suspend fun sendIdsOnServer(hashmap:HashMap<String,String>): Result<UpdateOrderModel> {
        return safeApiCall { apiClient.sendIdsOnServer(hashmap) }
    }

    suspend fun getAllTransporters(): Result<TransportersModal> {
        return safeApiCall { apiClient.getAllTransporters() }
    }

    suspend fun getAllQuestions(): Result<FeedbackModel> {
        return safeApiCall { apiClient.getQuestions() }
    }

    suspend fun sendDocumentOnServer(code:HashMap<String,String>): Result<SuccessModel> {
        return safeApiCall { apiClient.sendDocumentOnServer(code) }
    }

    suspend fun serializedModel(code:String): Result<SerialProductModel> {
        return safeApiCall { apiClient.serializedModel(code) }
    }

    suspend fun installationImages(code:String): Result<InstallationModel> {
        return safeApiCall { apiClient.installationImages(code) }
    }

    suspend fun feedbackDetails(code:String): Result<FeedbackLoadModels> {
        return safeApiCall { apiClient.feedbackDetails(code) }
    }


}