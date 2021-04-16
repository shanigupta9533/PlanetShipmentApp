package com.virtual_market.planetshipmentapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.virtual_market.planetshipmentapp.R

class FeedbackDialogFragment : DialogFragment() {

    private var inflate:View?=null
    private lateinit var onClickListener: FeedbackDialogFragment.OnClickListener

    interface OnClickListener {

        fun onClick(toString: String)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

        this.onClickListener = onClickListener

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_feedback_dialog, container, false)

        inflate!!.findViewById<TextView>(R.id.cancel).setOnClickListener { dismiss() }
        val note_text = inflate!!.findViewById<TextView>(R.id.note_text)
        inflate!!.findViewById<TextView>(R.id.feedback).setOnClickListener {

            onClickListener.onClick(note_text.text.toString())

        }

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_dialog_min)
        }

        return inflate
    }
}