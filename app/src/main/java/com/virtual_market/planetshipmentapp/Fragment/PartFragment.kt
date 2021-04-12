package com.virtual_market.planetshipmentapp.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.virtual_market.planetshipmentapp.Adapter.ShowPartitianAdapter
import com.virtual_market.planetshipmentapp.Adapter.ShowProductAdapter
import com.virtual_market.planetshipmentapp.Modal.ProductItem
import com.virtual_market.planetshipmentapp.Modal.ResponsePartModel
import com.virtual_market.planetshipmentapp.Modal.ResponsePartsModel
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.FragmentHomeBinding
import com.virtual_market.planetshipmentapp.databinding.FragmentPartBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.no_internet_connection.*
import java.util.*
import kotlin.collections.ArrayList

class PartFragment : Fragment() {

    private lateinit var viewModel: OrdersViewModel
    private lateinit var showModel: ArrayList<ResponsePartsModel>
    private lateinit var inflate: View
    private lateinit var showProductAdapter: ShowPartitianAdapter
    private lateinit var fragmentactivity: FragmentActivity
    private lateinit var binding: FragmentPartBinding
    private lateinit var progressBar: RelativeLayout
    private lateinit var noDataFound: RelativeLayout
    private lateinit var refreshButton: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentPartBinding.inflate(inflater, container, false)

        inflate = binding.root

        noDataFound=inflate.findViewById(R.id.no_data_found)
        noDataFound.visibility=View.GONE

        progressBar=inflate.findViewById(R.id.progress_bar)

        refreshButton=inflate.findViewById(R.id.refresh_button)

        showModel= ArrayList()

//        val recyclerView=inflate.findViewById<RecyclerView>(R.id.recyclerview)
//        val gridLayoutManager = GridLayoutManager(context, 1)
//        recyclerView.layoutManager = gridLayoutManager
//        showProductAdapter = ShowPartitianAdapter(context!!, showModel)
//        recyclerView.adapter = showProductAdapter

        setupViewModel()

        return inflate
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiInterface)
        ).get(OrdersViewModel::class.java)

//        setupObservers()

        refreshButton.setOnClickListener {

//           setupObservers()

        }

        binding.pullToRefresh.setOnRefreshListener {

            Handler(Looper.myLooper()!!).postDelayed({

                binding.pullToRefresh.setRefreshing(false)

            }, 2500)

        }


        viewModel.noInternet.observe(viewLifecycleOwner, {
            if (it) {
                no_internet_connection.visibility=View.VISIBLE
                MyUtils.createToast(context, "No Internet Connection!")
            } else{

                no_internet_connection.visibility=View.GONE

            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, {

            MyUtils.createToast(context!!.applicationContext, it)

            no_internet_connection.visibility=View.VISIBLE

        })

        viewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                progressBar.visibility = View.VISIBLE
                noDataFound.visibility=View.GONE
                no_internet_connection.visibility=View.GONE
            } else progressBar.visibility = View.GONE
        })

    }

//    private fun setupObservers() {
//
//        viewModel.dispatchOrdersByParts("50")
//
//        viewModel.orderDispatchByParts.removeObservers(viewLifecycleOwner)
//
//        viewModel.orderDispatchByParts.observe(viewLifecycleOwner, {
//
//            if (it.success.equals("failure")) {
//
//                MyUtils.createToast(context!!.applicationContext, it.message)
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