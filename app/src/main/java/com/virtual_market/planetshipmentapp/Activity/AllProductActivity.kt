package com.virtual_market.planetshipmentapp.Activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.DatePicker
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.virtual_market.planetshipmentapp.Adapter.ShowAllProductAdapter
import com.virtual_market.planetshipmentapp.Database.ScanDatabase
import com.virtual_market.planetshipmentapp.Modal.ResponseOrders
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel
import com.virtual_market.planetshipmentapp.MyUils.GridSpacingItemDecoration
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.ActivityAllProductBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.no_internet_connection.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class AllProductActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var scanDatabase: ScanDatabase? = null
    private var dateString: String? = null
    private lateinit var activity: ActivityAllProductBinding
    private var mySharedPreferences: MySharedPreferences? = null
    private lateinit var responseUserLogin: ResponseUserLogin
    private lateinit var showProductAdapter: ShowAllProductAdapter
    private var viewModel: OrdersViewModel? = null
    private lateinit var progressBar: RelativeLayout
    private var dayOfMonth: Int=0
    private var month: Int=0
    private var year: Int=0
    private lateinit var noDataFound: RelativeLayout
    private lateinit var refreshButton: RelativeLayout
    private lateinit var showModel: ArrayList<SerialProductListModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = DataBindingUtil.setContentView(this, R.layout.activity_all_product)

        mySharedPreferences = MySharedPreferences.getInstance(applicationContext)

        responseUserLogin =
            (application as PlanetShippingApplication).responseUserLogin
        scanDatabase = ScanDatabase.getInstance(applicationContext)

        noDataFound = findViewById(R.id.no_data_found)
        noDataFound.visibility = View.GONE

        showModel = ArrayList()

        progressBar = findViewById(R.id.progress_bar)

        val refreshButtonNoData = findViewById<RelativeLayout>(R.id.refresh_button_no_data)

        refreshButtonNoData.setOnClickListener {

            noDataFound.visibility = View.GONE
            no_internet_connection.visibility = View.GONE

            setUpApiCall()


        }

        refreshButton = findViewById(R.id.refresh_button)

        refreshButton.setOnClickListener {

            no_internet_connection.visibility = View.GONE
            setUpApiCall()

        }

        activity.pullToRefresh.setOnRefreshListener {

            setUpApiCall()
            Handler(Looper.myLooper()!!).postDelayed({

                activity.pullToRefresh.setRefreshing(false)

            }, 2500)

        }

        activity.parentOfDate.setOnClickListener {

            if(year==0) {
                val now: Calendar = Calendar.getInstance()
                dayOfMonth = now.get(Calendar.DAY_OF_MONTH)
                month = now.get(Calendar.MONTH)
                year = now.get(Calendar.YEAR)
            }

            val datePickerDialog = DatePickerDialog(
                this,
                this,
                year,
                month,
                dayOfMonth
            )


            datePickerDialog.show()

        }

        val gridLayoutManager = GridLayoutManager(this, 1)

        val spanCount = 1 // colums
        val spacing = 15 // spaces
        val includeEdge = true
        activity.recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )

        activity.recyclerView.layoutManager = gridLayoutManager
        showProductAdapter = ShowAllProductAdapter(this, showModel)
        activity.recyclerView.adapter = showProductAdapter

        setupViewModel()

        setUpApiCall()

    }

    private fun setUpApiCall() {

        if(dateString==null) {

            val c = Calendar.getInstance()
            year = c[Calendar.YEAR]
            month = c[Calendar.MONTH]
            dayOfMonth = c[Calendar.DAY_OF_MONTH]

            dateString = "$year-${month + 1}-$dayOfMonth"
        }

        viewModel!!.serializedModel("$dateString")

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

        viewModel!!.serializedModel.removeObservers(this)

        viewModel!!.serializedModel.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(applicationContext, it.message)

                if (it.message.equals("No Data Available")) {

                    noDataFound.visibility = View.VISIBLE

                } else {

                    noDataFound.visibility = View.GONE

                }

                return@observe

            } else {


                noDataFound.visibility = View.GONE
                activity.pullToRefresh.setRefreshing(false)

                thread {

                    showModel.clear()

                    showModel.addAll(it.Info!!)

                    Collections.sort(showModel,
                        Comparator<SerialProductListModel> { o1, o2 -> o1.Warehouse!!.compareTo(o2.Warehouse!!) })

                    runOnUiThread {

                        showProductAdapter.notifyDataSetChanged()

                    }

                }


            }

        })

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        dateString = "$year-${month + 1}-$dayOfMonth"

        this.year=year
        this.month=month
        this.dayOfMonth=dayOfMonth

        setUpApiCall()

    }

}