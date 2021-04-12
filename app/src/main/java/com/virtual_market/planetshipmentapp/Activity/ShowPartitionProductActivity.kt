package com.virtual_market.planetshipmentapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.virtual_market.planetshipmentapp.Adapter.ShowPartitianAdapter
import com.virtual_market.planetshipmentapp.Modal.ResponsePartsModel
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.ActivityShowPartitionProductBinding

import com.virtual_market.planetshipmentapp.databinding.FragmentPartBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.no_internet_connection.*

class ShowPartitionProductActivity : AppCompatActivity() {

    private var orderCode: String?=null
    private lateinit var viewModel: OrdersViewModel
    private lateinit var showModel: ArrayList<ResponsePartsModel>
    private lateinit var showProductAdapter: ShowPartitianAdapter
    private lateinit var progressBar: RelativeLayout
    private lateinit var noDataFound: RelativeLayout
    private lateinit var refreshButton: RelativeLayout
    private lateinit var activity:ActivityShowPartitionProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=DataBindingUtil.setContentView(this,R.layout.activity_show_partition_product)

        if(intent!=null){

            orderCode = intent.getStringExtra("orderCode")

        }

        findViewById<ImageView>(R.id.address).setOnClickListener {

            startActivity(Intent(this,ShowClientAddressActivity::class.java))

        }

        noDataFound=findViewById(R.id.no_data_found)
        noDataFound.visibility=View.GONE

        progressBar=findViewById(R.id.progress_bar)

        refreshButton=findViewById(R.id.refresh_button)

        showModel= ArrayList()

        val recyclerView=findViewById<RecyclerView>(R.id.recyclerview)
        val gridLayoutManager = GridLayoutManager(this, 1)
        recyclerView.layoutManager = gridLayoutManager
//        showProductAdapter = ShowPartitianAdapter(this, showModel)
        recyclerView.adapter = showProductAdapter

//        setupViewModel()
    }

//    private fun setupViewModel() {
//        viewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(RetrofitClient.apiInterface)
//        ).get(OrdersViewModel::class.java)
//
//        setupObservers()
//
//        refreshButton.setOnClickListener {
//
//            setupObservers()
//
//        }
//
//        activity.pullToRefresh.setOnRefreshListener {
//
//            Handler(Looper.myLooper()!!).postDelayed({
//
//                activity.pullToRefresh.setRefreshing(false)
//
//            }, 2500)
//
//        }
//
//
//        viewModel.noInternet.observe(this, {
//            if (it) {
//                no_internet_connection.visibility=View.VISIBLE
//                MyUtils.createToast(this, "No Internet Connection!")
//            } else{
//
//                no_internet_connection.visibility=View.GONE
//
//            }
//        })
//
//        viewModel.errorMessage.observe(this, {
//
//            MyUtils.createToast(this.applicationContext, it)
//
//            no_internet_connection.visibility=View.VISIBLE
//
//        })
//
//        viewModel.loading.observe(this, {
//            if (it) {
//                progressBar.visibility = View.VISIBLE
//                noDataFound.visibility=View.GONE
//                no_internet_connection.visibility=View.GONE
//            } else progressBar.visibility = View.GONE
//        })
//
//    }

//    private fun setupObservers() {
//
//        viewModel.dispatchOrdersByParts(orderCode!!)
//
//        viewModel.orderDispatchByParts.removeObservers(this)
//
//        viewModel.orderDispatchByParts.observe(this, {
//
//            if (it.success.equals("failure")) {
//
//                MyUtils.createToast(this.applicationContext, it.message)
//
//                if (it.message.equals("No Data Available")) {
//
//                    noDataFound.visibility = View.VISIBLE
//
//                } else {
//
//                    noDataFound.visibility = View.GONE
//
//                }
//
//                return@observe
//
//            } else {
//
//                val items = it.Items
//                showModel.clear()
//                items!!.forEach {
//
//                    showModel.addAll(it.parts!!)
//
//                }
//
//
//                noDataFound.visibility = View.GONE
//                showProductAdapter.notifyDataSetChanged()
//
//            }
//
//        })
//
//    }

}