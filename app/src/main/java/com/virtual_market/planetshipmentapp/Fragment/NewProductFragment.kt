package com.virtual_market.planetshipmentapp.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.virtual_market.planetshipmentapp.Adapter.ShowProductAdapter
import com.virtual_market.planetshipmentapp.Modal.ProductItem
import com.virtual_market.planetshipmentapp.Modal.ResponseOrders
import com.virtual_market.planetshipmentapp.databinding.FragmentNewProductBinding

class NewProduct : Fragment() {

    private lateinit var binding:FragmentNewProductBinding
    private lateinit var showModel:ArrayList<ResponseOrders>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentNewProductBinding.inflate(inflater, container, false)

        showModel=ArrayList()

        binding.pullToRefresh.setOnRefreshListener {

            Handler(Looper.myLooper()!!).postDelayed({

                binding.pullToRefresh.setRefreshing(false)

            },2500)

        }


        val gridLayoutManager= GridLayoutManager(context,2)
        binding.recyclerView.layoutManager=gridLayoutManager
        val showProductAdapter= ShowProductAdapter(context!!,showModel)
        binding.recyclerView.adapter=showProductAdapter

        return binding.root
    }
}