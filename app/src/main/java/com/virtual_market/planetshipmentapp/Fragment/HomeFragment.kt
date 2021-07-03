package com.virtual_market.planetshipmentapp.Fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.homedecor.planet.EntitiesOrder.BillingAddress
import com.homedecor.planet.EntitiesOrder.Coupons
import com.virtual_market.planetshipmentapp.Adapter.ShowProductAdapter
import com.virtual_market.planetshipmentapp.Modal.*
import com.virtual_market.planetshipmentapp.MyUils.GridSpacingItemDecoration
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.FragmentHomeBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.no_data_found.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import kotlinx.android.synthetic.main.progress_bar_layout.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class HomeFragment : Fragment(), DatePickerDialog.OnDateSetListener {

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
    private lateinit var showModel: ArrayList<ResponseOrders>

    override fun onAttach(context: Context) {
        super.onAttach(context)

        fragmentactivity = context as FragmentActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        mySharedPreferences = MySharedPreferences.getInstance(fragmentactivity.applicationContext)

        mySharedPreferences!!.setBooleanKey(MySharedPreferences.isUpdate, false)

        root = binding.root

        responseUserLogin =
            (fragmentactivity.application as PlanetShippingApplication).responseUserLogin

        noDataFound = root.findViewById(R.id.no_data_found)
        noDataFound.visibility = View.GONE

        progressBar = root.findViewById(R.id.progress_bar)

        val refreshButtonNoData = root.findViewById<RelativeLayout>(R.id.refresh_button_no_data)

        refreshButtonNoData.setOnClickListener {

            noDataFound.visibility = View.GONE

            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val dayOfMonth = c[Calendar.DAY_OF_MONTH]

            no_internet_connection.visibility = View.GONE
            MyUtils.clearAllMap()
            hashmap = MyUtils.setHashmap("Date", "${dayOfMonth}-${month + 1}-${year}")
            onDispatchOrders(hashmap!!)

        }

        refreshButton = root.findViewById(R.id.refresh_button)

        refreshButton.setOnClickListener {

            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val dayOfMonth = c[Calendar.DAY_OF_MONTH]

            no_internet_connection.visibility = View.GONE
            MyUtils.clearAllMap()
            hashmap = MyUtils.setHashmap("Date", "${dayOfMonth}-${month + 1}-${year}")
            onDispatchOrders(hashmap!!)

        }

        showModel = ArrayList()

        binding.parentOfDate.setOnClickListener {

            val now: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context!!,
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()

        }

        binding.pullToRefresh.setOnRefreshListener {

            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val dayOfMonth = c[Calendar.DAY_OF_MONTH]

            MyUtils.clearAllMap()
            hashmap = MyUtils.setHashmap("Date", "${dayOfMonth}-${month + 1}-${year}")

            uridate = null

            onDispatchOrders(hashmap!!)

            Handler(Looper.myLooper()!!).postDelayed({

                binding.pullToRefresh.setRefreshing(false)

            }, 2500)

        }


        val gridLayoutManager = GridLayoutManager(context, 1)

        val spanCount = 1 // colums
        val spacing = 15 // spaces
        val includeEdge = true
        binding.recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )

        binding.recyclerView.layoutManager = gridLayoutManager
        showProductAdapter = ShowProductAdapter(context!!, showModel)
        binding.recyclerView.adapter = showProductAdapter

        setupViewModel()

        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val dayOfMonth = c[Calendar.DAY_OF_MONTH]

        MyUtils.clearAllMap()
        hashmap = MyUtils.setHashmap("Date", "${dayOfMonth}-${month + 1}-${year}")
        onDispatchOrders(hashmap!!)

        binding.clearFilter.setOnClickListener {

            uridate=""
            MyUtils.clearAllMap()
            onDispatchOrders(hashmap!!)

        }

        return binding.root
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        MyUtils.clearAllMap()
        hashmap = MyUtils.setHashmap("Date", "${dayOfMonth}-${month + 1}-${year}")

        var dayOfMonth1 = ""
        var month1 = ""
        if (dayOfMonth < 10)
            dayOfMonth1 = "0${dayOfMonth}"
        else
            dayOfMonth1 = dayOfMonth.toString()

        if (month + 1 < 10)
            month1 = "0${month + 1}"
        else
            month1 = (month + 1).toString()

        uridate = "${year}-${month1}-${dayOfMonth1}"

        onDispatchOrders(hashmap!!)

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiInterface)
        ).get(OrdersViewModel::class.java)

        setupObservers()

        viewModel!!.noInternet.observe(viewLifecycleOwner, {
            if (it) {
                no_internet_connection.visibility = View.VISIBLE
                MyUtils.createToast(fragmentactivity, "No Internet Connection!")
            } else {

                no_internet_connection.visibility = View.GONE

            }
        })

        viewModel!!.errorMessage.observe(viewLifecycleOwner, {

            MyUtils.createToast(fragmentactivity.applicationContext, it)

            no_internet_connection.visibility = View.VISIBLE

        })

        viewModel!!.loading.observe(viewLifecycleOwner, {
            if (it) {
                progressBar.visibility = View.VISIBLE
                noDataFound.visibility = View.GONE
                no_internet_connection.visibility = View.GONE
            } else progressBar.visibility = View.GONE
        })

    }

    private fun setupObservers() {

        viewModel!!.orderDispatch.observe(viewLifecycleOwner, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(fragmentactivity.applicationContext, it.message)

                if (it.message.equals("No Data Available")) {

                    noDataFound.visibility = View.VISIBLE

                } else {

                    noDataFound.visibility = View.GONE

                }

                return@observe

            } else {

                noDataFound.visibility = View.GONE
                binding.pullToRefresh.setRefreshing(false)
                val orders = it.Orders

                if (responseUserLogin.Role.equals("Fitter")) {

                    val orderAssignOnFitter = findIdsFromStringFitter(orders!!)
                    showModel.clear()
                    parseDataEachOrderJson(orderAssignOnFitter)

                } else if (responseUserLogin.Role.equals("Helper")) {

                    val orderAssignOnHelper = findIdsFromStringHelper(orders!!)
                    showModel.clear()
                    parseDataEachOrderJson(orderAssignOnHelper)

                } else if (!TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.driver_id))) {

                    val orderAssignOnTransporter = findIdsFromStringTransporter(orders!!)
                    showModel.clear()
                    parseDataEachOrderJson(orderAssignOnTransporter)

                } else {

                    showModel.clear()
                    parseDataEachOrderJson(orders)

                }

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

    private fun parseDataEachOrderJson(
        ordJsonString: ArrayList<ResponseOrders>?
    ) {

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

                        if (!TextUtils.isEmpty(it.DeliveryDate) && it.DeliveryDate.equals(
                                uridate,
                                true
                            )
                        ) {

                            showModel.add(it)

                        }

                    }

                }

                Collections.sort(showModel,
                    Comparator<ResponseOrders> { o1, o2 -> o1.DeliveryDate!!.compareTo(o2.DeliveryDate!!) })

                showModel.reverse()

                showProductAdapter.notifyDataSetChanged()

                if (showModel.size > 0)
                    noDataFound.visibility = View.GONE
                else
                    noDataFound.visibility = View.VISIBLE

            }

        }

    }

    private fun onDispatchOrders(hashMap: HashMap<String, String>) {

        viewModel!!.dispatchOrders(hashMap)

    }
}