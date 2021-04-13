package com.virtual_market.planetshipmentapp.Modal

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "serial_table")
class SerialProductListModel() :Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "serial_id")
    var SerialId:String?=null

    @ColumnInfo(name = "order_code")
    var OrdCode:String?=null

    @ColumnInfo(name = "sap_order_code")
    var SapOrderCode:String?=null

    @ColumnInfo(name = "item_code")
    var ItemCode:String?=null

    @ColumnInfo(name = "ware_house")
    var Warehouse:String?=null

    @ColumnInfo(name = "serial_number")
    var SerialNumber:String?=null

    @ColumnInfo(name = "delivery_date")
    var DeliveryDate:String?=null

    @ColumnInfo(name = "allog_qty")
    var AllocQty:String?=null

    @ColumnInfo(name = "ship_status")
    var ShipStatus:String?=null

    @ColumnInfo(name = "delivery_status")
    var DeliveryStatus:String?=null

    @ColumnInfo(name = "ship_date")
    var ShipDate:String?=null

    @ColumnInfo(name = "detail_name")
    var DetailName:String?=null

    @ColumnInfo(name = "foriegn_name")
    var ForeignName:String?=null

    @ColumnInfo(name = "dellivery_date")
    var DelliveryDate:String?=null

    @Ignore
    var helpers:String?=null

    @Ignore
    var fitters:String?=null

    @Ignore
    var Transporters:String?=null

    @Ignore
    var Details:ArrayList<SerialDetailsModal>?=null

    constructor(parcel: Parcel) : this() {
        SerialId = parcel.readString()
        OrdCode = parcel.readString()
        SapOrderCode = parcel.readString()
        ItemCode = parcel.readString()
        Warehouse = parcel.readString()
        SerialNumber = parcel.readString()
        DeliveryDate = parcel.readString()
        AllocQty = parcel.readString()
        ShipStatus = parcel.readString()
        DeliveryStatus = parcel.readString()
        ShipDate = parcel.readString()
        DetailName = parcel.readString()
        ForeignName = parcel.readString()
        DelliveryDate = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(SerialId)
        parcel.writeString(OrdCode)
        parcel.writeString(SapOrderCode)
        parcel.writeString(ItemCode)
        parcel.writeString(Warehouse)
        parcel.writeString(SerialNumber)
        parcel.writeString(DeliveryDate)
        parcel.writeString(AllocQty)
        parcel.writeString(ShipStatus)
        parcel.writeString(DeliveryStatus)
        parcel.writeString(ShipDate)
        parcel.writeString(DetailName)
        parcel.writeString(ForeignName)
        parcel.writeString(DelliveryDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SerialProductListModel> {
        override fun createFromParcel(parcel: Parcel): SerialProductListModel {
            return SerialProductListModel(parcel)
        }

        override fun newArray(size: Int): Array<SerialProductListModel?> {
            return arrayOfNulls(size)
        }
    }

}