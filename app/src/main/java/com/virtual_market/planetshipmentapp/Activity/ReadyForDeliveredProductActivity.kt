package com.virtual_market.planetshipmentapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.virtual_market.planetshipmentapp.Adapter.ShowProductAdapter
import com.virtual_market.planetshipmentapp.Modal.ProductItem
import com.virtual_market.planetshipmentapp.Modal.ResponseOrders
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.databinding.ActivityReadyForDeliveredProductBinding
import com.virtual_market.planetshipmentapp.databinding.FragmentHomeBinding

class ReadyForDeliveredProductActivity : AppCompatActivity() {

    private lateinit var activity:ActivityReadyForDeliveredProductBinding
    private lateinit var showModel:ArrayList<ResponseOrders>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=DataBindingUtil.setContentView(this,R.layout.activity_ready_for_delivered_product)

        showModel=ArrayList()

        activity.pullToRefresh.setOnRefreshListener {

            Handler(Looper.myLooper()!!).postDelayed({

                activity.pullToRefresh.setRefreshing(false)

            },2500)

        }


        val gridLayoutManager= GridLayoutManager(this,2)
        activity.recyclerView.layoutManager=gridLayoutManager
        val showProductAdapter= ShowProductAdapter(this,showModel)
        activity.recyclerView.adapter=showProductAdapter

    }
}