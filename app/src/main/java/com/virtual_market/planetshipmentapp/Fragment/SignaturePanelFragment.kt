package com.virtual_market.planetshipmentapp.Fragment

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.github.gcacace.signaturepad.views.SignaturePad
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.R

class SignaturePanelFragment : DialogFragment() {

    private lateinit var onClickListener: OnClickListener
    private var i: Int=0
    var fragmentActivity:FragmentActivity?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivity = context as FragmentActivity
    }

     interface OnClickListener{
        fun onclick(signatureBitmap: Bitmap)
    }

    public fun setOnClickListener(onClickListener: OnClickListener){

        this.onClickListener=onClickListener

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val inflate = inflater.inflate(R.layout.fragment_signature_panel, container, false)

       val signature_pad = inflate.findViewById<SignaturePad>(R.id.signature_pad)
        val clear_text = inflate.findViewById<Button>(R.id.clear_text)
        val save = inflate.findViewById<Button>(R.id.save)
        val cancel = inflate.findViewById<Button>(R.id.cancel)

        cancel.setOnClickListener {

            dismiss()

        }

        signature_pad!!.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {

            }

            override fun onSigned() {
                i++ // called when users signied
            }

            override fun onClear() {

            }


        })

        clear_text.setOnClickListener {

            signature_pad.clear()

        }

        save.setOnClickListener {

            if(i>5) {
                onClickListener.onclick(signature_pad.transparentSignatureBitmap)
                dismiss()
            } else
                MyUtils.createToast(context,"Please do a valid signed")


        }

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_dialog_min)
        }

        return inflate
    }
}