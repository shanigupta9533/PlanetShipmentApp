package com.virtual_market.planetshipmentapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.LoginViewModel
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.ActivitySplashBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.no_internet_connection.*

class SplashActivity : AppCompatActivity() {

    private var mySharedPreferences: MySharedPreferences?=null
    private lateinit var viewModel: LoginViewModel
    private lateinit var activity:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=DataBindingUtil.setContentView(this,R.layout.activity_splash)

        mySharedPreferences = MySharedPreferences.getInstance(applicationContext)

        Handler(Looper.myLooper()!!).postDelayed(Runnable {

            setupViewModel()

        },1000)


        activity.refreshButton.setOnClickListener {

            activity.progressBar.visibility = View.VISIBLE
            activity.noInternetConnection.visibility= View.GONE

            if(!TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.user_id)))
                setupObservers()
            else if(!TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.driver_id)))
                setUpTrasformers()
            else {
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }

        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiInterface)
        ).get(LoginViewModel::class.java)

        Handler(Looper.myLooper()!!).postDelayed(Runnable {

            if(!TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.user_id)))
              setupObservers()
            else if(!TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.driver_id)))
                setUpTrasformers()
            else {
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }


        },1000)

        viewModel.noInternet.observe(this, {
            if (it) {
                activity.noInternetConnection.visibility= View.VISIBLE
                MyUtils.createToast(this, "No Internet Connection!")
            } else{
                activity.noInternetConnection.visibility= View.GONE
            }
        })

        viewModel.errorMessage.observe(this, {

            MyUtils.createToast(this.applicationContext, it)

            Log.i("TAG", "setupViewModel: "+it)

            activity.noInternetConnection.visibility = View.VISIBLE

        })

        viewModel.loading.observe(this, {
            if (it) {
                activity.progressBar.visibility = View.VISIBLE
                activity.noInternetConnection.visibility= View.GONE
            } else activity.progressBar.visibility = View.GONE
        })

    }

    private fun setUpTrasformers() {

        viewModel.getTransporter()

        viewModel.allTransporters.removeObservers(this)

        viewModel.allTransporters.observe(this,{

            if(it.success.equals("success")) {

                it.Transporters!!.forEach {

                    if (mySharedPreferences!!.getStringkey(MySharedPreferences.driver_id).equals(it.DriverId)) {

                        (application as PlanetShippingApplication).responseUserTransporter=it
                    }

                }

                startActivity(Intent(this,MainActivity::class.java))
                finishAffinity()

            }

        })

    }

    private fun setupObservers() {

        viewModel.loginUserWithUserId(mySharedPreferences!!.getStringkey(MySharedPreferences.user_id))

        viewModel.user.removeObservers(this)

        viewModel.user.observe(this,{

            if(it.success.equals("success")){

                (application as PlanetShippingApplication).responseUserLogin=it

                startActivity(Intent(this,MainActivity::class.java))

            }

            finishAffinity()

        })

    }

}