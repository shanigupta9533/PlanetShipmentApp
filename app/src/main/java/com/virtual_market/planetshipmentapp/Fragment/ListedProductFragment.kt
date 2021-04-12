package com.virtual_market.planetshipmentapp.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.virtual_market.planetshipmentapp.Adapter.DeliveredProductAdapterr
import com.virtual_market.planetshipmentapp.Adapter.ShowProductAdapter
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.databinding.FragmentHomeBinding
import com.virtual_market.planetshipmentapp.databinding.FragmentListedProductBinding

class ListedProductFragment : Fragment() {

    private lateinit var binding:FragmentListedProductBinding
    private lateinit var showModel:ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentListedProductBinding.inflate(inflater, container, false)

        showModel=ArrayList()

        for(i in 0..30){

            showModel.add("")

        }

        binding.pullToRefresh.setOnRefreshListener {

            Handler(Looper.myLooper()!!).postDelayed({

                binding.pullToRefresh.setRefreshing(false)

            },2500)

        }


        val gridLayoutManager= GridLayoutManager(context,2)
        binding.recyclerView.layoutManager=gridLayoutManager
        val deliveredProductAdapterr= DeliveredProductAdapterr(context!!,showModel)
        binding.recyclerView.adapter=deliveredProductAdapterr

        return binding.root
    }

}