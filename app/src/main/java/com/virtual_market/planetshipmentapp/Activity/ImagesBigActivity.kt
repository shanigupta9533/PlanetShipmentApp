package com.virtual_market.planetshipmentapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.no_internet_connection.*
import kotlinx.android.synthetic.main.progress_bar_layout.*

class ImagesBigActivity : AppCompatActivity() {

    private lateinit var responseUserLogin: ResponseUserLogin
    private var image_id: String?=null
    private lateinit var viewModel: OrdersViewModel
    private var mySharedPreferences: MySharedPreferences? = null
    private var imageView: PhotoView?=null
    private lateinit var progress_bar:RelativeLayout
    private lateinit var delete:CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_big)

        mySharedPreferences = MySharedPreferences.getInstance(applicationContext)

        responseUserLogin =
            (application as PlanetShippingApplication).responseUserLogin

        val image = intent.getStringExtra("image")
        image_id = intent.getStringExtra("image_id")

        imageView=findViewById(R.id.imageView)
        progress_bar=findViewById<RelativeLayout>(R.id.progress_bar)

        delete = findViewById(R.id.delete)

        if(!TextUtils.isEmpty(responseUserLogin.Role) && (responseUserLogin.Role.equals("Fitter") || responseUserLogin.Role.equals("Admin",true)) || responseUserLogin.Role.equals("Helper",true)){
            delete.visibility=View.VISIBLE
        } else {
            delete.visibility=View.GONE
        }

        delete.setOnClickListener {

            setupObservers()

        }

        Glide.with(this).load(image).placeholder(R.drawable.ic_logo_brown).error(R.drawable.ic_logo_brown).into(imageView!!)

        setupViewModel()

    }

    private fun setupObservers() {

        viewModel.getDeleteImages(image_id!!)

        viewModel.deleteImages.removeObservers(this)

        viewModel.deleteImages.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(this.applicationContext, it.message)

                return@observe

            } else {

                mySharedPreferences!!.setBooleanKey(MySharedPreferences.isUpdateImages, true)

                MyUtils.createToast(applicationContext,"Image deleted successfully.")

                onBackPressed()

            }

        })


    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiInterface)
        ).get(OrdersViewModel::class.java)

        viewModel.noInternet.observe(this, {
            if (it) {
                MyUtils.createToast(this, "No Internet Connection!")
            }
        })

        viewModel.errorMessage.observe(this, {

            MyUtils.createToast(this.applicationContext, it)

        })

        viewModel.loading.observe(this, {
            if (it) {
                progress_bar.visibility = View.VISIBLE
            } else progress_bar.visibility = View.GONE
        })

    }

}