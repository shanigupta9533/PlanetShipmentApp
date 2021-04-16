package com.virtual_market.planetshipmentapp.Activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.virtual_market.planetshipmentapp.Adapter.InstallationListModel
import com.virtual_market.planetshipmentapp.Adapter.InstallationsAdapter
import com.virtual_market.planetshipmentapp.Adapter.ShowProductAdapter
import com.virtual_market.planetshipmentapp.Modal.ResponseOrders
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.MyUils.GridSpacingItemDecoration
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.ActivityInstallationImagesBinding
import com.virtual_market.planetshipmentapp.databinding.FragmentHomeBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.no_internet_connection.*
import java.util.*
import kotlin.collections.ArrayList

class InstallationImagesActivity : AppCompatActivity() {

    private var ordCode: String?=null
    private lateinit var activity:ActivityInstallationImagesBinding
    private lateinit var responseUserLogin: ResponseUserLogin
    private lateinit var installationsAdapter: InstallationsAdapter
    private var viewModel: OrdersViewModel? = null
    private lateinit var progressBar: RelativeLayout
    private lateinit var noDataFound: RelativeLayout
    private lateinit var refreshButton: RelativeLayout
    private lateinit var showModel: ArrayList<InstallationListModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=DataBindingUtil.setContentView(this,R.layout.activity_installation_images)

        ordCode = intent.getStringExtra("ordCode")

        responseUserLogin =
            (application as PlanetShippingApplication).responseUserLogin

        noDataFound = findViewById(R.id.no_data_found)
        noDataFound.visibility = View.GONE

        progressBar = findViewById(R.id.progress_bar)

        val refreshButtonNoData = findViewById<RelativeLayout>(R.id.refresh_button_no_data)

        refreshButtonNoData.setOnClickListener {

            noDataFound.visibility = View.GONE

            no_internet_connection.visibility = View.GONE

            setupObservers()

        }

        refreshButton = findViewById(R.id.refresh_button)

        refreshButton.setOnClickListener {

            no_internet_connection.visibility = View.GONE

            setupObservers()

        }

        showModel = ArrayList()

        activity.pullToRefresh.setOnRefreshListener {

            setupObservers()

            Handler(Looper.myLooper()!!).postDelayed({

                activity.pullToRefresh.setRefreshing(false)

            }, 2500)

        }


        val gridLayoutManager = GridLayoutManager(this, 3)

        val spanCount = 1 // colums
        val spacing = 5 // spaces
        val includeEdge = true
        activity.recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )

        activity.recyclerView.layoutManager = gridLayoutManager
        installationsAdapter = InstallationsAdapter(this, showModel)
        activity.recyclerView.adapter = installationsAdapter

        installationsAdapter.setOnClickListener(object : InstallationsAdapter.OnClickListener{

            override fun onClick(image: String?) {

               val intent=Intent(this@InstallationImagesActivity,ImagesBigActivity::class.java)
                intent.putExtra("image",image)
               startActivity(intent)

            }

        })

        setupViewModel()

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiInterface)
        ).get(OrdersViewModel::class.java)

        setupObservers()

        viewModel!!.noInternet.observe(this, {
            if (it) {
                no_internet_connection.visibility = View.VISIBLE
                MyUtils.createToast(this, "No Internet Connection!")
            } else {

                no_internet_connection.visibility = View.GONE

            }
        })

        viewModel!!.errorMessage.observe(this, {

            MyUtils.createToast(this.applicationContext, it)

            no_internet_connection.visibility = View.VISIBLE

        })

        viewModel!!.loading.observe(this, {
            if (it) {
                progressBar.visibility = View.VISIBLE
                noDataFound.visibility = View.GONE
                no_internet_connection.visibility = View.GONE
            } else progressBar.visibility = View.GONE
        })

    }

    private fun setupObservers() {

        viewModel!!.getInstallationImages("2021041")

        viewModel!!.installationImages.removeObservers(this)

        viewModel!!.installationImages.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(this.applicationContext, it.message)

                if (it.message.equals("No Data Available")) {

                    noDataFound.visibility = View.VISIBLE

                } else {

                    noDataFound.visibility = View.GONE

                }

                return@observe

            } else {

                noDataFound.visibility = View.GONE
                activity.pullToRefresh.setRefreshing(false)
                val images = it.Images

                showModel.clear()
                showModel.addAll(images!!)

                installationsAdapter.notifyDataSetChanged()


            }

        })

    }

}