package com.virtual_market.planetshipmentapp.Modal

class ProductWithSubProduct {

    var isSubProduct:Boolean=false
    var isProduct:Boolean=false
    var serialId:String=""

    override fun equals(other: Any?): Boolean {
        other as ProductWithSubProduct
        if (serialId.equals(other.serialId,true)) return true

        return false
    }

    override fun hashCode(): Int {
        var result = isSubProduct.hashCode()
        result = 31 * result + isProduct.hashCode()
        result = 31 * result + serialId.hashCode()
        return result
    }

    override fun toString(): String {
        return "ProductWithSubProduct(isSubProduct=$isSubProduct, isProduct=$isProduct, serialId='$serialId')"
    }


}