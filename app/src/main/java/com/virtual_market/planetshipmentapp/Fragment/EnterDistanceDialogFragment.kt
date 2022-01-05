package com.virtual_market.planetshipmentapp.Fragment

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.databinding.FragmentEnterDistanceDialogBinding

class EnterDistanceDialogFragment : DialogFragment() {

    private lateinit var binding:FragmentEnterDistanceDialogBinding
    private var onClickListener: OnClickListener?=null
    private lateinit var fragmentActivity:FragmentActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentActivity= context as FragmentActivity

    }

    interface OnClickListener {

        fun onClick(distance:String)

    }

    fun setOnClickListener(onClickListener: OnClickListener) {

        this.onClickListener=onClickListener

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEnterDistanceDialogBinding.inflate(inflater, container, false)

        binding.saveBio.setOnClickListener {

            if(TextUtils.isEmpty(binding.bioEdit.text.toString())){

                binding.bioEdit.error = "Empty values is not allowed..."
                binding.bioEdit.requestFocus()

                return@setOnClickListener

            }

            onClickListener?.onClick(binding.bioEdit.text.toString())
            dismiss()
        }

        return binding.root

    }
}