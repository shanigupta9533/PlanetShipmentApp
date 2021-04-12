package com.homedecor.planet.EntitiesOrder

import com.google.gson.annotations.SerializedName

class BillingAddress {
    @SerializedName("AddLandmark")
    var addLandmark: String? = null

    @SerializedName("GSTIN")
    var gSTIN: String? = null

    @SerializedName("AddPinCode")
    var addPinCode: String? = null

    @SerializedName("CustomerCode")
    var customerCode: String? = null

    @SerializedName("AddFullName")
    var addFullName: String? = null

    @SerializedName("CompanyName")
    var companyName: String? = null

    @SerializedName("AddMobNo")
    var addMobNo: String? = null

    @SerializedName("AddCity")
    var addCity: String? = null

    @SerializedName("AddState")
    var addState: String? = null

    @SerializedName("AddType")
    var addType: String? = null

    @SerializedName("AddLine1")
    var addLine1: String? = null

    @SerializedName("AddressId")
    var addressId = 0

    @SerializedName("AddLine2")
    var addLine2: String? = null

    override fun toString(): String {
        return "BillingAddress{" +
                "addLandmark = '" + addLandmark + '\'' +
                ",gSTIN = '" + gSTIN + '\'' +
                ",addPinCode = '" + addPinCode + '\'' +
                ",customerCode = '" + customerCode + '\'' +
                ",addFullName = '" + addFullName + '\'' +
                ",companyName = '" + companyName + '\'' +
                ",addMobNo = '" + addMobNo + '\'' +
                ",addCity = '" + addCity + '\'' +
                ",addState = '" + addState + '\'' +
                ",addType = '" + addType + '\'' +
                ",addLine1 = '" + addLine1 + '\'' +
                ",addressId = '" + addressId + '\'' +
                ",addLine2 = '" + addLine2 + '\'' +
                "}"
    }
}