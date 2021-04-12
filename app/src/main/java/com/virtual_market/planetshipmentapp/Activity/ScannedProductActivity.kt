package com.virtual_market.planetshipmentapp.Activity

import android.content.Intent
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
import com.virtual_market.planetshipmentapp.databinding.ActivityScannedProductBinding

class ScannedProductActivity : AppCompatActivity() {

    private lateinit var activity: ActivityScannedProductBinding
    private lateinit var showModel:ArrayList<ResponseOrders>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=DataBindingUtil.setContentView(this,R.layout.activity_scanned_product)

        activity.scanned.setOnClickListener {

            startActivity(Intent(this,QrCodeScannerActivity::class.java))

        }

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