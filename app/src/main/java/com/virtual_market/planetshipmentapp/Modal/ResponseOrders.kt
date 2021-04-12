package com.virtual_market.planetshipmentapp.Modal

import android.os.Parcel
import android.os.Parcelable

class ResponseOrders() : Parcelable {

    var OrdCode:String?=null
    var SapOrdCode:String?=null
    var CustomerCode:String?=null
    var OrdStatus:String?=null
    var OrdAmount:String?=null
    var ItemCount:String?=null
    var PmtMethod:String?=null
    var ShipMethod:String?=null
    var OrdFrom :String?=null
    var OrdDate:String?=null
    var PmtInfo:String?=null
    var BookingAmount:String?=null
    var OrdJson:String?=null
    var main_image:String?=null
    var ordJsonList:OrdJson?=null
    var DeliveryDate:String?=null
    var CouponCode:String?=null
    var InstallationCharges:String?=null
    var DeliveryCharges:String?=null
    var CustomizationCharges:String?=null
    var Helpers:String?=null
    var Fitters:String?=null
    var Transporters:String?=null
    var TransportCharges:String?=null

    constructor(parcel: Parcel) : this() {
        OrdCode = parcel.readString()
        SapOrdCode = parcel.readString()
        CustomerCode = parcel.readString()
        OrdStatus = parcel.readString()
        OrdAmount = parcel.readString()
        ItemCount = parcel.readString()
        PmtMethod = parcel.readString()
        ShipMethod = parcel.readString()
        OrdFrom = parcel.readString()
        OrdDate = parcel.readString()
        PmtInfo = parcel.readString()
        BookingAmount = parcel.readString()
        OrdJson = parcel.readString()
        main_image = parcel.readString()
        ordJsonList =
            parcel.readParcelable(com.virtual_market.planetshipmentapp.Modal.OrdJson::class.java.classLoader)
        DeliveryDate = parcel.readString()
        CouponCode = parcel.readString()
        InstallationCharges = parcel.readString()
        DeliveryCharges = parcel.readString()
        CustomizationCharges = parcel.readString()
        Helpers = parcel.readString()
        Fitters = parcel.readString()
        Transporters = parcel.readString()
        TransportCharges = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(OrdCode)
        parcel.writeString(SapOrdCode)
        parcel.writeString(CustomerCode)
        parcel.writeString(OrdStatus)
        parcel.writeString(OrdAmount)
        parcel.writeString(ItemCount)
        parcel.writeString(PmtMethod)
        parcel.writeString(ShipMethod)
        parcel.writeString(OrdFrom)
        parcel.writeString(OrdDate)
        parcel.writeString(PmtInfo)
        parcel.writeString(BookingAmount)
        parcel.writeString(OrdJson)
        parcel.writeString(main_image)
        parcel.writeParcelable(ordJsonList, flags)
        parcel.writeString(DeliveryDate)
        parcel.writeString(CouponCode)
        parcel.writeString(InstallationCharges)
        parcel.writeString(DeliveryCharges)
        parcel.writeString(CustomizationCharges)
        parcel.writeString(Helpers)
        parcel.writeString(Fitters)
        parcel.writeString(Transporters)
        parcel.writeString(TransportCharges)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResponseOrders> {
        override fun createFromParcel(parcel: Parcel): ResponseOrders {
            return ResponseOrders(parcel)
        }

        override fun newArray(size: Int): Array<ResponseOrders?> {
            return arrayOfNulls(size)
        }
    }


}