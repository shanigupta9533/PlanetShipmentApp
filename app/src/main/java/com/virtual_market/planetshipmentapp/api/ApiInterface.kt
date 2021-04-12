package com.virtual_market.virtualmarket.api

import com.virtual_market.planetshipmentapp.Modal.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    //Salesman Login
    @FormUrlEncoded
    @POST("EL")
    suspend fun createUserByEmail(@FieldMap request: HashMap<String, String>): Response<ResponseUserLogin>

    //Dispatch all orders
    @FormUrlEncoded
    @POST("DO")
    suspend fun OrdersDispatch(@FieldMap map: HashMap<String,String>): Response<ResponseDispatchOrders>

    //loginWithUserId
    @FormUrlEncoded
    @POST("ED")
    suspend fun createUserByUserId(@Field("EmpId") id:String): Response<ResponseUserLogin>

    //orderDispatchByParts
    @FormUrlEncoded
    @POST("SNL")
    suspend fun orderDispatchByParts(@Field("OrdCode") id:String): Response<SerialProductModel>


    //orderDispatchByParts
    @FormUrlEncoded
    @POST("CD2")
    suspend fun hiddingCD2(@Field("Code") id:String): Response<CD2Modal>

    //employees details
    @GET("AE")
    suspend fun employeesDetails(): Response<EmployeeDetailsModal>

    //transporter details
    @GET("TE")
    suspend fun getAllTransporters(): Response<TransportersModal>

    //send ids on server
    @FormUrlEncoded
    @POST("UO")
    suspend fun sendIdsOnServer(@FieldMap hashmap: HashMap<String, String>): Response<UpdateOrderModel>

    //send ids on server
    @FormUrlEncoded
    @POST("TL")
    suspend fun userTransportersLogin(@FieldMap hashmap: HashMap<String, String>): Response<ResponseTransporter>

    //serilized model of code
    @FormUrlEncoded
    @POST("SNL")
    suspend fun serializedModel(@Field("Code") code:String): Response<SerialProductModel>

}