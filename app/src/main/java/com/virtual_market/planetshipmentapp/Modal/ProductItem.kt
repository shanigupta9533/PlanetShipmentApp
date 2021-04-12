package com.virtual_market.planetshipmentapp.Modal

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


class ProductItem: Parcelable {
    @SerializedName("BrandName")
    var brandName: String? = null

    @SerializedName("MainImage")
    var mainImage: String? = null

    @SerializedName("OrderCod")
    var OrderCod: String? = null

    @SerializedName("ItemCode")
    var itemCode: String? = null

    @SerializedName("SellAmt")
    var sellAmt: String? = null

    @SerializedName("DetailName")
    var detailName: String? = null

    @SerializedName("Qty")
    var qty = 0

    @SerializedName("BrandCode")
    var brandCode: String? = null

    @SerializedName("MRP")
    var mRP: String? = null

    @SerializedName("GSTPercentage")
    var gSTPercentage: String? = null

    @SerializedName("HSNCode")
    var hSNCode: String? = null

    @SerializedName("Parts")
    var parts:ArrayList<ResponsePartsModel>?=null

    @SerializedName("CustomizationCharges")
    var CustomizationCharges:String?=null

    @SerializedName("DeliveryCharges")
    var deliveryCharges:String?=null

    @SerializedName("InstallationCharges")
    var installationCharges:String?=null

    constructor(parcel: Parcel) : this() {
        brandName = parcel.readString()
        mainImage = parcel.readString()
        OrderCod = parcel.readString()
        itemCode = parcel.readString()
        sellAmt = parcel.readString()
        detailName = parcel.readString()
        qty = parcel.readInt()
        brandCode = parcel.readString()
        mRP = parcel.readString()
        gSTPercentage = parcel.readString()
        hSNCode = parcel.readString()
        CustomizationCharges = parcel.readString()
        deliveryCharges = parcel.readString()
        installationCharges = parcel.readString()
    }


    constructor() {}

    override fun toString(): String {
        return "ProductItem{" +
                "brandName = '" + brandName + '\'' +
                ",mainImage = '" + mainImage + '\'' +
                ",itemCode = '" + itemCode + '\'' +
                ",sellAmt = '" + sellAmt + '\'' +
                ",detailName = '" + detailName + '\'' +
                ",qty = '" + qty + '\'' +
                ",brandCode = '" + brandCode + '\'' +
                ",mRP = '" + mRP + '\'' +
                ",gSTPercentage = '" + gSTPercentage + '\'' +
                ",hSNCode = '" + hSNCode + '\'' +
                ",OrderCod = '" + OrderCod + '\'' +
                "}"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(brandName)
        parcel.writeString(mainImage)
        parcel.writeString(OrderCod)
        parcel.writeString(itemCode)
        parcel.writeString(sellAmt)
        parcel.writeString(detailName)
        parcel.writeInt(qty)
        parcel.writeString(brandCode)
        parcel.writeString(mRP)
        parcel.writeString(gSTPercentage)
        parcel.writeString(hSNCode)
        parcel.writeString(CustomizationCharges)
        parcel.writeString(deliveryCharges)
        parcel.writeString(installationCharges)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductItem> {
        override fun createFromParcel(parcel: Parcel): ProductItem {
            return ProductItem(parcel)
        }

        override fun newArray(size: Int): Array<ProductItem?> {
            return arrayOfNulls(size)
        }
    }
}