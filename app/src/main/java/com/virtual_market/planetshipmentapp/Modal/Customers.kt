package com.virtual_market.planetshipmentapp.Modal

import com.google.gson.annotations.SerializedName

class Customers {
    @SerializedName("MobNo")
    var mobNo: String? = null

    @SerializedName("Email")
    var email: String? = null

    @SerializedName("CustomerCode")
    var customerCode: String? = null

    @SerializedName("FirstName")
    var firstName: String? = null

    @SerializedName("LastName")
    var lastName: String? = null

    @SerializedName("CustomerName")
    var customerName: String? = null

    override fun toString(): String {
        return "Customers{" +
                "mobNo = '" + mobNo + '\'' +
                ",email = '" + email + '\'' +
                ",customerCode = '" + customerCode + '\'' +
                ",firstName = '" + firstName + '\'' +
                ",lastName = '" + lastName + '\'' +
                ",customerName = '" + customerName + '\'' +
                "}"
    }
}