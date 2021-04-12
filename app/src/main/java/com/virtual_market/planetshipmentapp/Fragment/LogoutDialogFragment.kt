package com.virtual_market.planetshipmentapp.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.virtual_market.planetshipmentapp.Activity.LoginActivity
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.R

class LogoutDialogFragment : DialogFragment() {

    private var mySharedPreferences: MySharedPreferences?=null
    private var inflate: View?=null
    private lateinit var fragmentActivity: FragmentActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentActivity=context as FragmentActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflate = inflater.inflate(R.layout.fragment_logout_dialog, container, false)

        mySharedPreferences= MySharedPreferences.getInstance(fragmentActivity)

        // [END config_signin]
        inflate!!.findViewById<View>(R.id.cancel)
            .setOnClickListener(View.OnClickListener { dismiss() })

        inflate!!.findViewById<View>(R.id.logout_button)
            .setOnClickListener(View.OnClickListener {

                deleteAccount()

            })

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawableResource(R.drawable.round_dialog_min)
        }

        return inflate
    }

    private fun deleteAccount() {

        MySharedPreferences.removeAllData()
        startActivity(Intent(context, LoginActivity::class.java))
        fragmentActivity.finishAffinity()
    }

}