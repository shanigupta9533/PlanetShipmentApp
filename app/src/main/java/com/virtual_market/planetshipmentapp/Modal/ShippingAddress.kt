package com.virtual_market.planetshipmentapp.Modal

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class ShippingAddress() : Parcelable {
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

    constructor(parcel: Parcel) : this() {
        addLandmark = parcel.readString()
        gSTIN = parcel.readString()
        addPinCode = parcel.readString()
        customerCode = parcel.readString()
        addFullName = parcel.readString()
        companyName = parcel.readString()
        addMobNo = parcel.readString()
        addCity = parcel.readString()
        addState = parcel.readString()
        addType = parcel.readString()
        addLine1 = parcel.readString()
        addressId = parcel.readInt()
        addLine2 = parcel.readString()
    }

    override fun toString(): String {
        return "ShippingAddress{" +
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addLandmark)
        parcel.writeString(gSTIN)
        parcel.writeString(addPinCode)
        parcel.writeString(customerCode)
        parcel.writeString(addFullName)
        parcel.writeString(companyName)
        parcel.writeString(addMobNo)
        parcel.writeString(addCity)
        parcel.writeString(addState)
        parcel.writeString(addType)
        parcel.writeString(addLine1)
        parcel.writeInt(addressId)
        parcel.writeString(addLine2)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShippingAddress> {
        override fun createFromParcel(parcel: Parcel): ShippingAddress {
            return ShippingAddress(parcel)
        }

        override fun newArray(size: Int): Array<ShippingAddress?> {
            return arrayOfNulls(size)
        }
    }
}