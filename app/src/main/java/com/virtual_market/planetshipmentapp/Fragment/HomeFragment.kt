package com.virtual_market.planetshipmentapp.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.homedecor.planet.EntitiesOrder.BillingAddress
import com.homedecor.planet.EntitiesOrder.Coupons
import com.virtual_market.planetshipmentapp.Adapter.ShowProductAdapter
import com.virtual_market.planetshipmentapp.Modal.*
import com.virtual_market.planetshipmentapp.MyUils.*
import com.virtual_market.planetshipmentapp.MyUils.NoPaginate.paginate.NoPaginate
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.FragmentHomeBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.fragment_confirmation_dialog.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_logout_dialog.view.*
import kotlinx.android.synthetic.main.fragment_logout_dialog.view.text
import kotlinx.android.synthetic.main.no_data_found.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import kotlinx.android.synthetic.main.progress_bar_layout.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class HomeFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var isSearchApply: Boolean = false
    private var uridate: String? = null
    private var hashmap: HashMap<String, String>? = null
    private var mySharedPreferences: MySharedPreferences? = null
    private lateinit var responseUserLogin: ResponseUserLogin
    private lateinit var root: View
    private lateinit var showProductAdapter: ShowProductAdapter
    private lateinit var fragmentactivity: FragmentActivity
    private lateinit var binding: FragmentHomeBinding
    private var viewModel: OrdersViewModel? = null
    private lateinit var progressBar: RelativeLayout
    private lateinit var noDataFound: RelativeLayout
    private lateinit var refreshButton: RelativeLayout
    private var page: Int = 0
    private lateinit var handler: Handler
    private val delay: Long = 500 // 500 milli seconds after user stops typing
    private var last_text_edit: Long = 0
    private lateinit var showModel: ArrayList<ResponseOrders>
    private lateinit var noPaginate: NoPaginate

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentactivity = context as FragmentActivity

    }

    @SuppressLint("NotifyDataSetChanged") override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        mySharedPreferences = MySharedPreferences.getInstance(fragmentactivity.applicationContext)

        mySharedPreferences!!.setBooleanKey(MySharedPreferences.isUpdate, false)

        root = binding.root

        handler = Handler(Looper.myLooper()!!)

        responseUserLogin = (fragmentactivity.application as PlanetShippingApplication).responseUserLogin

        noDataFound = root.findViewById(R.id.no_data_found)
        noDataFound.visibility = View.GONE

        progressBar = root.findViewById(R.id.progress_bar)

        val refreshButtonNoData = root.findViewById<RelativeLayout>(R.id.refresh_button_no_data)

        refreshButtonNoData.setOnClickListener {

            page = 0
            showModel.clear()
            showProductAdapter.notifyDataSetChanged()

            MyUtils.clearAllMap()
            hashmap = HashMap()

            isSearchApply = false
            DoStuff()
            binding.searchView.setText(uridate)
            binding.searchView.setSelection(binding.searchView.length())

        }

        refreshButton = root.findViewById(R.id.refresh_button)

        refreshButton.setOnClickListener {

            page = 0
            showModel.clear()
            showProductAdapter.notifyDataSetChanged()

            MyUtils.clearAllMap()
            hashmap = HashMap()

            isSearchApply = false
            DoStuff()
            binding.searchView.setText(uridate)
            binding.searchView.setText(uridate)
            binding.searchView.setSelection(binding.searchView.length())

        }

        showModel = ArrayList()

        binding.parentOfDate.setOnClickListener {

            val now: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(context!!, this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()

        }

        binding.pullToRefresh.setOnRefreshListener {

            page = 0
            showModel.clear()
            showProductAdapter.notifyDataSetChanged()

            isSearchApply = false
            DoStuff()
            binding.searchView.setText(uridate)
            binding.searchView.setSelection(binding.searchView.length())

            binding.pullToRefresh.setRefreshing(false)
            hashmap = HashMap()

        }


        val gridLayoutManager = GridLayoutManager(context, 1)

        val spanCount = 1 // colums
        val spacing = 15 // spaces
        val includeEdge = true
        binding.recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))

        binding.cancelIcon.setOnClickListener {

            isSearchApply = false
            binding.searchView.setText("")
            DoStuff()

        }

        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                if (charSequence.isNotEmpty()) {
                    binding.cancelIcon.visibility = View.VISIBLE
                } else {
                    binding.cancelIcon.visibility = View.GONE
                }

                handler.removeCallbacks(input_finish_checker)
            }

            override fun afterTextChanged(s: Editable) {

                if (s.isNotEmpty() && isSearchApply) {
                    last_text_edit = System.currentTimeMillis()
                    handler.postDelayed(input_finish_checker, delay)
                } else {
                    DoStuff()
                }

                isSearchApply = true

            }
        })

        binding.recyclerView.layoutManager = gridLayoutManager
        showProductAdapter = ShowProductAdapter(context!!, showModel)
        binding.recyclerView.adapter = showProductAdapter

        setupViewModel()

        MyUtils.clearAllMap()
        hashmap = HashMap()

        noPaginate = NoPaginate.with(binding.recyclerView) // pagination of api
            .setLoadingTriggerThreshold(0).setCustomErrorItem(CustomErrorItem()).setOnLoadMoreListener {

                noPaginate.showLoading(true)
                noPaginate.showError(false)
                homeModelForFetch(hashmap!!)

            }.build()

        binding.clearFilter.setOnClickListener { // clEAR filter

            page = 0
            showModel.clear()
            showProductAdapter.notifyDataSetChanged()
            uridate = ""

            MyUtils.clearAllMap()
            hashmap = HashMap()

            isSearchApply = false
            binding.searchView.setText("")
            DoStuff()

        }

        return binding.root
    }

    private val input_finish_checker = Runnable {
        if (System.currentTimeMillis() > last_text_edit + delay - 500) { // TODO: do what you need here
            // ............
            // ............
            DoStuff()
        }
    }

    @SuppressLint("NotifyDataSetChanged") private fun DoStuff() {

        page = 0
        showModel.clear()
        showProductAdapter.notifyDataSetChanged()

        noPaginate.showLoading(false)
        noPaginate.showError(false)
        noPaginate.setNoMoreItems(false)

    }

    private fun homeModelForFetch(hashmap: HashMap<String, String>) {

        if (TextUtils.isEmpty(uridate)) {
            date_sorting.text = "Whole Data"
        } else {
            date_sorting.text = uridate
        }

        hashmap["offset"] = (page * 10).toString()
        hashmap["limit"] = "10"

        if (TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.driver_id))) {

            responseUserLogin.let {

                hashmap["Location"] = it.Location
                hashmap["EmpId"] = it.EmpId!!

            }

        } else {

            val responseUserTransporter = (fragmentactivity.application as PlanetShippingApplication).responseUserTransporter

            responseUserTransporter.let {

                hashmap["Location"] = it.DriverLocation!!
                hashmap["EmpId"] = it.DriverId!!

            }

        }

        if (!TextUtils.isEmpty(binding.searchView.text.toString())) hashmap["Date"] = binding.searchView.text.toString()
        else {
            hashmap["Date"] = ""
            uridate = ""
        }


        onDispatchOrders(hashmap)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        MyUtils.clearAllMap()
        page = 0
        isSearchApply = true
        binding.searchView.setText("${year}-${month + 1}-${dayOfMonth}")
        binding.searchView.setSelection(binding.searchView.length())

        var dayOfMonth1 = ""
        var month1 = ""
        dayOfMonth1 = if (dayOfMonth < 10) "0${dayOfMonth}"
        else dayOfMonth.toString()

        if (month + 1 < 10) month1 = "0${month + 1}"
        else month1 = (month + 1).toString()

        uridate = "${year}-${month1}-${dayOfMonth1}"

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitClient.apiInterface)).get(OrdersViewModel::class.java)

        setupObservers()

        viewModel!!.noInternet.observe(viewLifecycleOwner, {


        })

        viewModel!!.errorMessage.observe(viewLifecycleOwner, {

            MyUtils.createToast(fragmentactivity.applicationContext, it)

            if (page == 0) {
                no_internet_connection.visibility = View.VISIBLE

            }

            noPaginate.showError(true)
            noPaginate.showLoading(false)

        })

        viewModel!!.loading.observe(viewLifecycleOwner, {

            if (it && page == 0) {

                progressBar.visibility = View.VISIBLE
                noDataFound.visibility = View.GONE
                no_internet_connection.visibility = View.GONE

            } else {

                progressBar.visibility = View.GONE

            }

        })

    }

    private fun setupObservers() {

        viewModel!!.orderDispatch.observe(viewLifecycleOwner, {

            if (page == 0 && it.success.equals("failure")) {

                MyUtils.createToast(fragmentactivity.applicationContext, it.message)

                if (it.message.equals("No Data Available")) {

                    noDataFound.visibility = View.VISIBLE
                    noPaginate.setNoMoreItems(true)

                } else {

                    noDataFound.visibility = View.GONE

                }

                return@observe

            } else if (page > 0 && it.success.equals("failure") && it.message.equals("No Data Available")) {

                noPaginate.setNoMoreItems(true)

            } else {

                noDataFound.visibility = View.GONE
                binding.pullToRefresh.setRefreshing(false)
                val orders = it.Orders

                parseDataEachOrderJson(orders)

            }

        })

    }

    private fun findIdsFromStringTransporter(orders: java.util.ArrayList<ResponseOrders>): ArrayList<ResponseOrders> {

        val responseOrdersArray = ArrayList<ResponseOrders>()

        var responseOrders: ResponseOrders

        orders.forEach {

            responseOrders = it

            if (!TextUtils.isEmpty(it.Transporters)) {

                val split = it.Transporters!!.split(",")

                split.forEach {

                    if (mySharedPreferences!!.getStringkey(MySharedPreferences.driver_id).equals(it, true)) {

                        responseOrdersArray.add(responseOrders)
                        return@forEach

                    }

                }

            }

        }

        return responseOrdersArray

    }

    private fun findIdsFromStringHelper(orders: java.util.ArrayList<ResponseOrders>): ArrayList<ResponseOrders> {

        val responseOrdersArray = ArrayList<ResponseOrders>()

        var responseOrders: ResponseOrders

        orders.forEach {

            responseOrders = it

            if (!TextUtils.isEmpty(it.Helpers)) {

                val split = it.Helpers!!.split(",")

                split.forEach {

                    if (responseUserLogin.EmpId!!.equals(it, true)) {
                        responseOrdersArray.add(responseOrders)
                        return@forEach
                    }


                }

            }

        }

        return responseOrdersArray

    }

    private fun findIdsFromStringFitter(orders: ArrayList<ResponseOrders>): ArrayList<ResponseOrders> {

        val responseOrdersArray = ArrayList<ResponseOrders>()

        var responseOrders: ResponseOrders

        orders.forEach {

            responseOrders = it

            if (!TextUtils.isEmpty(it.Fitters)) {

                val split = it.Fitters!!.split(",")

                split.forEach {

                    if (responseUserLogin.EmpId!!.equals(it, true)) {
                        responseOrdersArray.add(responseOrders)
                        return@forEach
                    }

                }

            }

        }

        return responseOrdersArray

    }

    override fun onResume() {
        super.onResume()

        if (mySharedPreferences!!.getBooleanKey(MySharedPreferences.isUpdate)) {

            if (hashmap == null) {

                val c = Calendar.getInstance()
                val year = c[Calendar.YEAR]
                val month = c[Calendar.MONTH]
                val dayOfMonth = c[Calendar.DAY_OF_MONTH]
                no_internet_connection.visibility = View.GONE
                MyUtils.clearAllMap()
                hashmap = MyUtils.setHashmap("Date", "${dayOfMonth}-${month + 1}-${year}")

            }

            onDispatchOrders(hashmap!!)
            mySharedPreferences!!.setBooleanKey(MySharedPreferences.isUpdate, false)


        }

    }

    private fun parseDataEachOrderJson(ordJsonString: ArrayList<ResponseOrders>?) {

        thread {

            ordJsonString!!.forEach {

                val ordJson = OrdJson()
                val productItemArrayList: ArrayList<ProductItem> = ArrayList()
                val address = Address()
                try {

                    val json = JSONObject(it.OrdJson!!)
                    val `object` = json.getString("Customers")
                    val jsonC = JSONObject(`object`)
                    val objectAddress = json["address"]
                    val customers = Customers()
                    customers.customerCode = jsonC.getString("CustomerCode")
                    customers.customerName = jsonC.getString("CustomerName")
                    customers.email = jsonC.getString("Email")
                    customers.mobNo = jsonC.getString("MobNo")
                    customers.firstName = jsonC.getString("FirstName")
                    customers.lastName = jsonC.getString("LastName")
                    ordJson.setCustomers(customers)
                    try {
                        val objectCoupons = json["Coupons"]
                        if (!TextUtils.isEmpty(objectCoupons.toString())) {
                            val jsonCoupons = JSONObject(objectCoupons.toString())
                            if (jsonCoupons.has("Amount") && !jsonCoupons.isNull("Amount")) {
                                val coupons = Coupons()
                                coupons.amount = jsonCoupons.getString("Amount")
                                coupons.couponCode = jsonCoupons.getString("CouponCode")
                                coupons.couponId = jsonCoupons.getInt("CouponId")
                                coupons.couponName = jsonCoupons.getString("CouponName")
                                coupons.couponType = jsonCoupons.getString("CouponType")
                                coupons.discount = jsonCoupons.getString("Discount")
                                ordJson.setCoupons(coupons)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    val jsonA = JSONObject(objectAddress.toString())
                    val objectAddressBilling = jsonA["billing_address"]
                    val objectAddressShipping = jsonA["shipping_address"]
                    val billingAddress = BillingAddress()
                    val shippingAddress = ShippingAddress()
                    val jsonAB = JSONObject(objectAddressBilling.toString())
                    val jsonAS = JSONObject(objectAddressShipping.toString())
                    billingAddress.addCity = jsonAB.getString("AddCity")
                    billingAddress.addFullName = jsonAB.getString("AddFullName")
                    billingAddress.addLandmark = jsonAB.getString("AddLandmark")
                    billingAddress.addLine1 = jsonAB.getString("AddLine1")
                    billingAddress.addLine2 = jsonAB.getString("AddLine2")
                    billingAddress.addMobNo = jsonAB.getString("AddMobNo")
                    billingAddress.addPinCode = jsonAB.getString("AddPinCode")
                    billingAddress.addressId = jsonAB.getInt("AddressId")
                    billingAddress.addState = jsonAB.getString("AddState")
                    billingAddress.addType = jsonAB.getString("AddType")
                    billingAddress.companyName = jsonAB.getString("CompanyName")
                    billingAddress.gSTIN = jsonAB.getString("GSTIN")
                    address.setBillingAddress(billingAddress)
                    shippingAddress.addCity = jsonAS.getString("AddCity")
                    shippingAddress.addFullName = jsonAS.getString("AddFullName")
                    shippingAddress.addLandmark = jsonAS.getString("AddLandmark")
                    shippingAddress.addLine1 = jsonAS.getString("AddLine1")
                    shippingAddress.addLine2 = jsonAS.getString("AddLine2")
                    shippingAddress.addMobNo = jsonAS.getString("AddMobNo")
                    shippingAddress.addPinCode = jsonAS.getString("AddPinCode")
                    shippingAddress.addressId = jsonAS.getInt("AddressId")
                    shippingAddress.addState = jsonAS.getString("AddState")
                    shippingAddress.addType = jsonAS.getString("AddType")
                    shippingAddress.companyName = jsonAS.getString("CompanyName")
                    shippingAddress.gSTIN = jsonAS.getString("GSTIN")
                    address.setShippingAddress(shippingAddress)
                    ordJson.setAddress(address)
                    val jsonArray: JSONArray? = json.getJSONArray("Product")
                    if (jsonArray != null) {

                        //Iterating JSON array
                        for (i in 0 until jsonArray.length()) {
                            val jsonPD = JSONObject(jsonArray[i].toString())

                            //Adding each element of JSON array into ArrayList
                            val productItem = ProductItem()
                            productItem.brandCode = jsonPD.getString("BrandCode")
                            productItem.brandName = jsonPD.getString("BrandName")
                            productItem.detailName = jsonPD.getString("DetailName")
                            productItem.gSTPercentage = jsonPD.getString("GSTPercentage")
                            productItem.hSNCode = jsonPD.getString("HSNCode")
                            productItem.itemCode = jsonPD.getString("ItemCode")
                            productItem.mainImage = jsonPD.getString("MainImage")
                            productItem.qty = jsonPD.getInt("Qty")
                            productItem.sellAmt = jsonPD.getString("SellAmt")
                            productItem.mRP = jsonPD.getString("MRP")
                            productItem.OrderCod = it.OrdCode
                            productItemArrayList.add(productItem)
                        }

                        it.main_image = productItemArrayList[0].mainImage

                        ordJson.setProduct(productItemArrayList)

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                it.ordJsonList = ordJson

            }

            fragmentactivity.runOnUiThread {

                binding.dateSorting.text = hashmap!!["Date"]

                if (TextUtils.isEmpty(uridate)) {

                    showModel.addAll(ordJsonString)

                } else {

                    ordJsonString.forEach { // syncronized by datewise android

                        // todo check delivery date
                        if (!TextUtils.isEmpty(it.DeliveryDate) && it.DeliveryDate.equals(uridate, true)) {

                            showModel.add(it)

                        }

                    }

                }

                showProductAdapter.notifyItemRangeInserted(ordJsonString.size, showModel.size)

                when {
                    showModel.size > 0 -> {

                        page++
                        noPaginate.showLoading(false)
                        noPaginate.showError(false)
                        noDataFound.visibility = View.GONE

                    }
                    page == 0 && showModel.isEmpty() -> {

                        //noDataFound.visibility = View.VISIBLE

                    }
                    else -> {

                        noPaginate.setNoMoreItems(true)

                    }
                }

            }

        }

    }

    private fun onDispatchOrders(hashMap: HashMap<String, String>) {

        viewModel!!.dispatchOrders(hashMap)

    }
}