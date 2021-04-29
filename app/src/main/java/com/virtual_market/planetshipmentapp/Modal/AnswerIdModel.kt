package com.virtual_market.planetshipmentapp.Modal

class AnswerIdModel {

    var AnswerId:String?=null
    var data:String?=null
    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        other as AnswerIdModel
        if (AnswerId == other.AnswerId) return true

        return false
    }

    override fun hashCode(): Int {
        var result = AnswerId?.hashCode() ?: 0
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "AnswerIdModel(AnswerId=$AnswerId, data=$data)"
    }


}