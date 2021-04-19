package com.virtual_market.planetshipmentapp.Modal

class ProductWithSubProduct {

    var isSubProduct:Boolean=false
    var isProduct:Boolean=false
    var serialId:String=""

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        other as ProductWithSubProduct
        if (serialId == other.serialId) return true

        return true
    }

    override fun hashCode(): Int {
        var result = isSubProduct.hashCode()
        result = 31 * result + isProduct.hashCode()
        result = 31 * result + serialId.hashCode()
        return result
    }

    override fun toString(): String {
        return "ProductWithSubProduct(isSubProduct=$isSubProduct, isProduct=$isProduct, isSerialNumber='$serialId')"
    }


}