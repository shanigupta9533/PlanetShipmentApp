package com.virtual_market.planetshipmentapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.virtual_market.planetshipmentapp.Adapter.ShowProductAdapter
import com.virtual_market.planetshipmentapp.Modal.ProductItem
import com.virtual_market.planetshipmentapp.Modal.ResponseOrders
import com.virtual_market.planetshipmentapp.R

class ProductFragment : Fragment() {

    private lateinit var showModel: ArrayList<ResponseOrders>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val inflate = inflater.inflate(R.layout.fragment_product, container, false)

        showModel = ArrayList()

        val recyclerView=inflate.findViewById<RecyclerView>(R.id.recyclerview)
        val gridLayoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = gridLayoutManager
        val showProductAdapter = ShowProductAdapter(context!!, showModel)
        recyclerView.adapter = showProductAdapter

        return inflate
    }

}