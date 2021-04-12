package com.virtual_market.planetshipmentapp.Modal

import retrofit2.http.Field

 class RequestUserLogin (

    @Field("Email")
    var Email:String?=null,

    @Field("password")
    var password:String?=null

)