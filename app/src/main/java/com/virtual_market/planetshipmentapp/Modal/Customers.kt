package com.virtual_market.planetshipmentapp.Modal

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Customers() : Parcelable {
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

    constructor(parcel: Parcel) : this() {
        mobNo = parcel.readString()
        email = parcel.readString()
        customerCode = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
        customerName = parcel.readString()
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mobNo)
        parcel.writeString(email)
        parcel.writeString(customerCode)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(customerName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Customers> {
        override fun createFromParcel(parcel: Parcel): Customers {
            return Customers(parcel)
        }

        override fun newArray(size: Int): Array<Customers?> {
            return arrayOfNulls(size)
        }
    }
}