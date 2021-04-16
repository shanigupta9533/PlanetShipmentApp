package com.virtual_market.planetshipmentapp.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.virtual_market.planetshipmentapp.Adapter.NestedProductAndPartsAdapter
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.R

class ConfirmationDialogFragment : DialogFragment() {

    private var mySharedPreferences: MySharedPreferences?=null
    private var inflate: View?=null
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var onClickListener: ConfirmationDialogFragment.OnClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentActivity=context as FragmentActivity

    }

    interface OnClickListener {

        fun onClick()

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

        this.onClickListener = onClickListener

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        inflate = inflater.inflate(R.layout.fragment_confirmation_dialog, container, false)

        mySharedPreferences= MySharedPreferences.getInstance(fragmentActivity)

        // [END config_signin]
        inflate!!.findViewById<View>(R.id.no)
            .setOnClickListener{ dismiss() }

        inflate!!.findViewById<View>(R.id.yes)
            .setOnClickListener{

                onClickListener.onClick()

                dismiss()

            }

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_dialog_min)
        }

        return inflate
    }
}