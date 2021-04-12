package com.virtual_market.planetshipmentapp.Modal

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_table")
class ScanModel() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id = 0

    @ColumnInfo(name = "serial_key")
    var serial_key:String? = ""

    @ColumnInfo(name = "product_id")
    var product_id:String? = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        serial_key = parcel.readString()!!
        product_id = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(serial_key)
        parcel.writeString(product_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScanModel> {
        override fun createFromParcel(parcel: Parcel): ScanModel {
            return ScanModel(parcel)
        }

        override fun newArray(size: Int): Array<ScanModel?> {
            return arrayOfNulls(size)
        }
    }

}