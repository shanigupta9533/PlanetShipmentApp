package com.virtual_market.planetshipmentapp.Adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.virtual_market.planetshipmentapp.Modal.AnswerIdModel
import com.virtual_market.planetshipmentapp.Modal.FeedbackUserList
import com.virtual_market.planetshipmentapp.Modal.QuestionsModel
import com.virtual_market.planetshipmentapp.Modal.SubQuestionsModel
import com.virtual_market.planetshipmentapp.R
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class HeadingOfQuestionsAdapter(
    private val context: Context,
    private val responsePost: List<QuestionsModel>) : RecyclerView.Adapter<HeadingOfQuestionsAdapter.viewholder>() {

    private var feedbackUserList: FeedbackUserList?=null
    private val answerIdModelList=ArrayList<AnswerIdModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val layoutInflater = LayoutInflater.from(context)
        val inflate =
            layoutInflater.inflate(R.layout.heading_question_adapter_layout, parent, false)
        return viewholder(inflate)
    }

    interface OnClickListener {

        fun onClick(isChecked: Boolean, position: Int)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        val questionsModel = responsePost[position]
        holder.bindView(questionsModel.SubQuestions!!)

        holder.questions.text="${position+1}. "+questionsModel.Question

    }

    override fun getItemCount(): Int {
        return responsePost.size
    }

    inner class viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val questions=itemView.findViewById<TextView>(R.id.questions)
        var recyclerView:RecyclerView?=null

        init {

            recyclerView = itemView.findViewById(R.id.recyclerView)
            val linearLayoutManager = LinearLayoutManager(context)
            recyclerView!!.layoutManager = linearLayoutManager

        }

        fun bindView(questionsModel: ArrayList<SubQuestionsModel>) {

            val answerOfQuestionAdapter=AnswerOfQuestionAdapter(context,questionsModel)
            recyclerView!!.adapter=answerOfQuestionAdapter

            if(feedbackUserList!=null)
                answerOfQuestionAdapter.setOptionIds(feedbackUserList!!.OptionIds);

            answerOfQuestionAdapter.setOnClickListener(object : AnswerOfQuestionAdapter.OnClickListener{
                override fun onClick(s: Int, answerId: String?) {

                    val answerIdModel=AnswerIdModel()
                    answerIdModel.data=s.toString()
                    answerIdModel.AnswerId=answerId
                    answerIdModelList.remove(answerIdModel)
                    answerIdModelList.add(answerIdModel)

                }
            })

        }

    }

    fun feedbackModels(feedbackUserList:FeedbackUserList){

        this.feedbackUserList=feedbackUserList

    }

    fun answerIdModel(): String? {

        val arrayList=ArrayList<String>()

        Collections.sort(answerIdModelList,
            Comparator<AnswerIdModel> { o1, o2 -> o1.data!!.compareTo(o2.data!!) })

        answerIdModelList.forEach {
            arrayList.add(it.data!!)
        }

        return TextUtils.join(",",arrayList)

    }

}