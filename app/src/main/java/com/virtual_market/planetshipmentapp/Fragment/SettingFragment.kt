package com.virtual_market.planetshipmentapp.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.virtual_market.planetshipmentapp.Activity.ChangePasswordActivity
import com.virtual_market.planetshipmentapp.Activity.CustomerSupportActivity
import com.virtual_market.planetshipmentapp.Activity.FeedbackActivity
import com.virtual_market.planetshipmentapp.Modal.ResponseTransporter
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.databinding.FragmentSettingBinding
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {

    private lateinit var responseUserLogin: ResponseUserLogin
    private lateinit var responseUserTransporter: ResponseTransporter
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var binding: FragmentSettingBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentActivity=context as FragmentActivity

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding=FragmentSettingBinding.inflate(inflater, container, false)

        val mySharedPreferences=MySharedPreferences.getInstance(fragmentActivity.applicationContext)

        if(!TextUtils.isEmpty(mySharedPreferences.getStringkey(MySharedPreferences.driver_id))) {
            responseUserTransporter =
                (fragmentActivity.applicationContext as PlanetShippingApplication).responseUserTransporter

            binding.nameUser.text=responseUserTransporter.DriverName
            binding.emailUser.text="Role : Transporter"

        } else{

            responseUserLogin =
                (fragmentActivity.applicationContext as PlanetShippingApplication).responseUserLogin

            binding.nameUser.text=responseUserLogin.FirstName+" "+responseUserLogin.MiddleName+" "+responseUserLogin.LastName
            binding.emailUser.text="Role : ${responseUserLogin.Role}"

        }

        binding.teamSupport.setOnClickListener {

            startActivity(Intent(context,CustomerSupportActivity::class.java))

        }

        binding.signout.setOnClickListener {

            val logoutDialogFragment= LogoutDialogFragment()
            logoutDialogFragment.show(childFragmentManager,"logoutDialogFragment")

        }

        binding.changePassword.setOnClickListener {

            startActivity(Intent(context,ChangePasswordActivity::class.java))

        }

        binding.feedbackText.setOnClickListener {

            startActivity(Intent(context,FeedbackActivity::class.java))

        }

        return binding.root
    }

}