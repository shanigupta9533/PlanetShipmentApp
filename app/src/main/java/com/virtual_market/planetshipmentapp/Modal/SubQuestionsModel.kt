package com.virtual_market.planetshipmentapp.Modal

class SubQuestionsModel {

    var AnswerId:String?=null
    var QuestionId:String?=null
    var Answer:String?=null
    var Answers:ArrayList<AnswersModel>?=null

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        other as SubQuestionsModel

        if (AnswerId != other.AnswerId) return false
        if (QuestionId != other.QuestionId) return false
        if (Answer != other.Answer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = AnswerId?.hashCode() ?: 0
        result = 31 * result + (QuestionId?.hashCode() ?: 0)
        result = 31 * result + (Answer?.hashCode() ?: 0)
        return result
    }


}