package com.virtual_market.planetshipmentapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.LoginViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.ActivityLoginBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.activity_show_client_address.*

class LoginActivity : AppCompatActivity() {

    private var isEmployee: Boolean? = false
    private var mySharedPreferences: MySharedPreferences? = null
    private var viewModel: LoginViewModel? = null
    private lateinit var activity: ActivityLoginBinding
    private lateinit var progressbar: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setupViewModel()

        mySharedPreferences = MySharedPreferences.getInstance(applicationContext)

        progressbar = findViewById(R.id.progress_bar)
        progressbar.visibility = View.GONE

        isEmployee = true

        activity.radioGroup.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {

                R.id.employee -> {

                    isEmployee = true

                }

                R.id.transporter -> {

                    isEmployee = false

                }

            }

        }

        viewModel!!.user.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(this, it.message)

                return@observe

            } else {

                progressbar.visibility = View.GONE

                (application as PlanetShippingApplication).responseUserLogin = it

                if(!it.Role.equals("Sales Employee")) {

                    mySharedPreferences!!.setStringKey(MySharedPreferences.user_id, it.EmpId)
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()

                } else{

                    MyUtils.createToast(applicationContext,"Sales Employee restrict from planet shipment app");

                }

            }

        })

        activity.loginButton.setOnClickListener {

            if (TextUtils.isEmpty(activity.mobileNumber.text.toString())) {

                activity.mobileNumber.error = "Email is required*"
                activity.mobileNumber.requestFocus()

            } else if (TextUtils.isEmpty(activity.password.text.toString())) {

                activity.password.error = "Password is required*"
                activity.password.requestFocus()

            } else {

                if (isEmployee!!)
                    setUpObservers()
                else
                    setUpTransporters()

            }

        }

    }

    private fun setUpTransporters() {

        MyUtils.clearAllMap()
        MyUtils.setHashmap("DriverMobile", activity.mobileNumber.text.toString().trim())
        val hashmap = MyUtils.setHashmap("DriverPassword", activity.password.text.toString().trim())
        viewModel!!.loginTransportersWithEmail(hashmap)

        viewModel!!.transporterUser.removeObservers(this)

        viewModel!!.transporterUser.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(this, it.message)

                return@observe

            } else {

                progressbar.visibility = View.GONE

                (application as PlanetShippingApplication).responseUserTransporter = it

                mySharedPreferences!!.setStringKey(MySharedPreferences.driver_id, it.DriverId)

                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()

            }

        })

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiInterface)
        ).get(LoginViewModel::class.java)

        viewModel!!.errorMessage.observe(this,{
            if (it.isNotEmpty()) MyUtils.createToast(applicationContext, it)
        })

        viewModel!!.noInternet.observe(this, {
            if (it) MyUtils.createToast(applicationContext, "Connection Timeout!")
        })

        viewModel!!.loading.observe(this, {
            if (it) {
                progressbar.visibility = View.VISIBLE
            } else progressbar.visibility = View.GONE
        })

    }

    private fun setUpObservers() {

        MyUtils.clearAllMap()

        if (isValidEmail(activity.mobileNumber.text.toString()))
            MyUtils.setHashmap("Email", activity.mobileNumber.text.toString().trim())
        else {
            MyUtils.setHashmap("Mobile", activity.mobileNumber.text.toString().trim())
        }

        val hashmap = MyUtils.setHashmap("password", activity.password.text.toString().trim())

        viewModel!!.loginUserWithEmail(hashmap)

    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
    }
}