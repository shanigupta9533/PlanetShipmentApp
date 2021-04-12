package com.virtual_market.planetshipmentapp.Modal

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.homedecor.planet.EntitiesOrder.BillingAddress

class Address() : Parcelable {
    @SerializedName("billing_address")
    private var billingAddress: BillingAddress? = null

    @SerializedName("shipping_address")
    private var shippingAddress: ShippingAddress? = null

    constructor(parcel: Parcel) : this() {

        this.shippingAddress=parcel.readParcelable(ShippingAddress::class.java.getClassLoader())

    }

    fun setBillingAddress(billingAddress: BillingAddress?) {
        this.billingAddress = billingAddress
    }

    fun getBillingAddress(): BillingAddress? {
        return billingAddress
    }

    fun setShippingAddress(shippingAddress: ShippingAddress?) {
        this.shippingAddress = shippingAddress
    }

    fun getShippingAddress(): ShippingAddress? {
        return shippingAddress
    }

    override fun toString(): String {
        return "Address{" +
                "billing_address = '" + billingAddress + '\'' +
                ",shipping_address = '" + shippingAddress + '\'' +
                "}"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeParcelable(this.shippingAddress, flags)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}