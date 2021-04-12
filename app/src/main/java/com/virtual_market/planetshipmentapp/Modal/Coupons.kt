package com.homedecor.planet.EntitiesOrder

import com.google.gson.annotations.SerializedName

class Coupons {
    @SerializedName("CouponCode")
    var couponCode: String? = null

    @SerializedName("Discount")
    var discount: String? = null

    @SerializedName("CouponName")
    var couponName: String? = null

    @SerializedName("CouponId")
    var couponId = 0

    @SerializedName("Amount")
    var amount: String? = null

    @SerializedName("CouponType")
    var couponType: String? = null

    override fun toString(): String {
        return "Coupons{" +
                "couponCode = '" + couponCode + '\'' +
                ",discount = '" + discount + '\'' +
                ",couponName = '" + couponName + '\'' +
                ",couponId = '" + couponId + '\'' +
                ",amount = '" + amount + '\'' +
                ",couponType = '" + couponType + '\'' +
                "}"
    }
}