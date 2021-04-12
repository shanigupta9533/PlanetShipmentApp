package com.virtual_market.planetshipmentapp.Modal

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.homedecor.planet.EntitiesOrder.Coupons
import java.util.*

class OrdJson() : Parcelable {
    @SerializedName("address")
    private var address: Address? = null

    @SerializedName("Customers")
    private var customers: Customers? = null

    @SerializedName("Product")
    private var product: ArrayList<ProductItem>? = null

    @SerializedName("Coupons")
    private var coupons: Coupons? = null

    constructor(parcel: Parcel) : this() {

        this.address=parcel.readParcelable(Address::class.java.getClassLoader())

    }

    fun setAddress(address: Address?) {
        this.address = address
    }

    fun getAddress(): Address? {
        return address
    }

    fun setCustomers(customers: Customers?) {
        this.customers = customers
    }

    fun getCustomers(): Customers? {
        return customers
    }

    fun setProduct(product: ArrayList<ProductItem>?) {
        this.product = product
    }

    fun getProduct(): ArrayList<ProductItem>? {
        return product
    }

    fun setCoupons(coupons: Coupons?) {
        this.coupons = coupons
    }

    fun getCoupons(): Coupons? {
        return coupons
    }

    override fun toString(): String {
        return "OrdJson{" +
                "address = '" + address + '\'' +
                ",customers = '" + customers + '\'' +
                ",product = '" + product + '\'' +
                ",coupons = '" + coupons + '\'' +
                "}"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeParcelable(this.address, flags)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrdJson> {
        override fun createFromParcel(parcel: Parcel): OrdJson {
            return OrdJson(parcel)
        }

        override fun newArray(size: Int): Array<OrdJson?> {
            return arrayOfNulls(size)
        }
    }

}
