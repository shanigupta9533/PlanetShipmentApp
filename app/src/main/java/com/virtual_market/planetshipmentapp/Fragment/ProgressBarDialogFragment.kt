package com.virtual_market.planetshipmentapp.Fragment

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.virtual_market.planetshipmentapp.R

class ProgressBarDialogFragment : DialogFragment() {

    private var keywords: String = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? { // Inflate the layout for this fragment

        val inflate = inflater.inflate(R.layout.fragment_progress_bar_dialog, container, false)

        if(arguments!=null)
            keywords = arguments!!.getString("keywords", "")

        val load = inflate.findViewById<TextView>(R.id.loading)

        if(TextUtils.isEmpty(keywords)){
            load.text = "Loading..."
        } else {
            load.text = keywords
        }

        return inflate
    }
}