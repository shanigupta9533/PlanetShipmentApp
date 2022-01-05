package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.virtual_market.planetshipmentapp.Modal.AnswersModel
import com.virtual_market.planetshipmentapp.Modal.SubQuestionsModel
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.R


class AnswerOfQuestionAdapter(
    private val context: Context,
    private val responsePost: ArrayList<SubQuestionsModel>
) : RecyclerView.Adapter<AnswerOfQuestionAdapter.viewholder>() {

    private var optionids: String?=null
    private lateinit var onClickListener: OnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate =
            layoutInflater.inflate(R.layout.answer_of_question_adapter_layout, parent, false)
        return viewholder(inflate)
    }

    interface OnClickListener {

        fun onClick(s: Int, answerId: String?)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

        this.onClickListener=onClickListener

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val subQuestionsModel = responsePost[position]
        holder.questions.text=subQuestionsModel.Answer
        holder.bindView(subQuestionsModel.Answers)

        holder.radio_group.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<RadioButton>(i)
            onClickListener.onClick(i,subQuestionsModel.AnswerId)
        }

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val questions=itemView.findViewById<TextView>(R.id.questions)
        val radio_group=itemView.findViewById<RadioGroup>(R.id.radio_group)

        fun bindView(options: ArrayList<AnswersModel>?) {

            options!!.forEach {
                val rbn = RadioButton(context)
                rbn.id = it.OptionId!!.toInt()
                rbn.text = it.Options
                val params =
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                rbn.layoutParams = params

                if(optionids!=null) {

                    optionids!!.split(",").forEach { optionIds ->

                        if (optionIds.toString().equals(it.OptionId!!, true)) {
                            rbn.isChecked = true
                         }

                    }
                }

                radio_group.addView(rbn)

            }

        }

    }

    fun setOptionIds(optionIds: String?) {
        this.optionids=optionIds
    }

}