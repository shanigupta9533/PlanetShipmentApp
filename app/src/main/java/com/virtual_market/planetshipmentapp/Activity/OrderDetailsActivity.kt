package com.virtual_market.planetshipmentapp.Activity

import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidbuts.multispinnerfilter.*
import com.virtual_market.planetshipmentapp.Adapter.OrderDetailsAdapter
import com.virtual_market.planetshipmentapp.Modal.*
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.ActivityOrderDetailsBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.activity_create_menu.view.*
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class OrderDetailsActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {

    private lateinit var responseUserLogin: ResponseUserLogin
    private var addressOfShipping: String? = null
    private lateinit var viewModel: OrdersViewModel
    private var uri: String? = null
    var result: String? = null
    private var mySharedPreferences: MySharedPreferences? = null
    private lateinit var orderDetailsAdapter: OrderDetailsAdapter
    private var productItem: ArrayList<ProductItem>? = null
    private var progressBar: RelativeLayout? = null
    private var refreshButton: RelativeLayout? = null
    private var responseOrders: ResponseOrders? = null
    private var noDataFound: RelativeLayout? = null
    private var ordJson: OrdJson? = null
    private val fitterArrayList = ArrayList<KeyPairBoolData>()
    private val helperArrayList = ArrayList<KeyPairBoolData>()
    private val transportArraylist = ArrayList<KeyPairBoolData>()
    private lateinit var activity: ActivityOrderDetailsBinding
    private lateinit var showModel: ArrayList<ProductItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = DataBindingUtil.setContentView(this, R.layout.activity_order_details)

        if (intent != null) {

            responseOrders = intent.getParcelableExtra("responseOrders")
            ordJson = intent.getParcelableExtra("ordJson")
            productItem = intent.getParcelableArrayListExtra("productItem")

        }

        responseUserLogin = (application as PlanetShippingApplication).responseUserLogin

        noDataFound = findViewById(R.id.no_data_found)
        noDataFound!!.visibility = View.GONE

        progressBar = findViewById(R.id.progress_bar)

        refreshButton = findViewById(R.id.refresh_button)

        showModel = ArrayList()

        setUpLocationData()

        mySharedPreferences = MySharedPreferences.getInstance(this)

        val gridLayoutManager = GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false)
        activity.allProductList.layoutManager = gridLayoutManager
        orderDetailsAdapter = OrderDetailsAdapter(this, showModel)
        activity.allProductList.adapter = orderDetailsAdapter

        setDataOnPage(responseOrders!!)

        setupViewModel()

    }

    fun getDistanceViaLatLong(addressOfShipping: String) {

        thread {

            // calculate langitute and longitude by address
            val locationFromAddress: LatLng = getLocationFromAddress(this, addressOfShipping)!!

            // calculate distance
            if (locationFromAddress != null && locationFromAddress.lattitude > 0 && locationFromAddress.longitude > 0) {

                val selected_location = Location("locationA")

                val latitude = mySharedPreferences!!.getStringkey(MySharedPreferences.myLatitude)
                val longitude = mySharedPreferences!!.getStringkey(MySharedPreferences.myLongitude)

                if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {

                    selected_location.latitude = latitude.toDouble()
                    selected_location.longitude = longitude.toDouble()

                    val near_locations = Location("locationB")

                    near_locations.latitude = locationFromAddress.lattitude
                    near_locations.longitude = locationFromAddress.longitude

                    getAddressFromLocation(near_locations.latitude, near_locations.longitude, this)

                    val distance = selected_location.distanceTo(near_locations)

                    val s = String.format("%.2f", distance / 1000)

                    runOnUiThread {

                        uri =
                            "http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + near_locations.latitude + "," + near_locations.longitude

                        activity.shippingDistance.text = "Distance From Address : $s K.M"

                    }

                }

            }

        }

    }

    fun getAddressFromLocation(lat: Double, long: Double, context: Context?) {
        val thread: Thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val list = geocoder.getFromLocation(
                        lat, long, 1
                    )
                    if (list != null && list.size > 0) {
                        val address = list[0]
                        // sending back first address line and locality
                        result = address.getAddressLine(0)

                    }
                } catch (e: IOException) {
                    Log.e("TAG", "Impossible to connect to Geocoder", e)
                }
            }
        }
        thread.start()
    }

    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        var address: List<Address>? = null
        var p1: LatLng? = null

        // May throw an IOException
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return LatLng(0.0, 0.0)
            }
        } catch (e: IOException) {
            e.printStackTrace()

            if (!TextUtils.isEmpty(addressOfShipping))
                getDistanceViaLatLong(addressOfShipping!!)

        }
        if (address != null && address.size > 0) {
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } else {
            p1 = LatLng(0.0, 0.0)
        }
        return p1
    }

    fun getIdsWithComma(spinner: MultiSpinnerSearch): String {

        val selectedIds = spinner.selectedIds
        val fitterIdsBuilder = StringBuilder()

        selectedIds.forEach {

            fitterIdsBuilder.append(it).append(",")

        }

        return fitterIdsBuilder.toString()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun setDataOnPage(responseOrders: ResponseOrders) {

        val address = ordJson!!.getAddress()

        activity.orderNo.text = responseOrders.OrdCode
        activity.amount.text = responseOrders.OrdAmount
        activity.date.text = responseOrders.DeliveryDate
        activity.noOfItems.text = responseOrders.ItemCount
        activity.totalAmount.text = responseOrders.BookingAmount
        activity.address.text = address!!.getShippingAddress()!!.addType
        activity.address2.text = address.getShippingAddress()!!.addLine1
        activity.address3.text = address.getShippingAddress()!!.addLine2

        showModel.clear()
        showModel.addAll(productItem!!)
        orderDetailsAdapter.notifyDataSetChanged()

        activity.installationParent.visibility = View.GONE
        activity.customizationParent.visibility = View.GONE
        activity.deliveryParent.visibility = View.GONE

        activity.deliveryDateSubmit.setOnClickListener {

            MyUtils.clearAllMap()
            MyUtils.setHashmap("DeliveryDate", dateOfDelivery.text.toString())
            val hashmap = MyUtils.setHashmap("OrdCode", responseOrders.OrdCode)
            sendDataOnServer(hashmap)

        }

        dateOfDelivery.text=responseOrders.DeliveryDate

        activity.dateOfDelivery.setOnClickListener {

            val now: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()

        }

        if (!TextUtils.isEmpty(responseOrders.InstallationCharges) && responseOrders.InstallationCharges!!.trim()
                .toFloat() > 0
        ) {
            activity.installationCharges.text = responseOrders.InstallationCharges
            activity.installationParent.visibility = View.VISIBLE

        }

        if (!TextUtils.isEmpty(responseOrders.CustomizationCharges) && responseOrders.CustomizationCharges!!.trim()
                .toFloat() > 0
        ) {
            activity.customizationCharges.text = responseOrders.CustomizationCharges
            activity.customizationParent.visibility = View.VISIBLE

        }

        if (!TextUtils.isEmpty(responseOrders.DeliveryCharges) && responseOrders.DeliveryCharges!!.trim()
                .toFloat() > 0
        ) {
            activity.deliveryCharges.text = responseOrders.DeliveryCharges
            activity.deliveryParent.visibility = View.VISIBLE

        }

        activity.submitIds.setOnClickListener {

            MyUtils.clearAllMap()
            MyUtils.setHashmap("Fitters", getIdsWithComma(activity.fitterSpinner))
            MyUtils.setHashmap("Helpers", getIdsWithComma(activity.helperSpinner))
            MyUtils.setHashmap("TransportCharges", activity.transportCharges.text.toString())
            MyUtils.setHashmap("OrdCode", responseOrders.OrdCode)
            val hashmap = MyUtils.setHashmap(
                "Transporters",
                getIdsWithComma(activity.transportSpinner)
            )

            sendDataOnServer(hashmap)

        }

        activity.transportCharges.setText(responseOrders.TransportCharges)

        if(responseUserLogin.Role.equals("Fitter")){

            activity.helperSpinner.isEnabled=false
            activity.fitterSpinner.isEnabled=false
            activity.transportSpinner.isEnabled=false
            activity.transportCharges.isEnabled=false

            activity.dateOfDelivery.isEnabled=false

            activity.parentOfButton.visibility=View.GONE

        } else if(responseUserLogin.Role.equals("Helper")){

            activity.helperSpinner.isEnabled=false
            activity.fitterSpinner.isEnabled=false
            activity.transportSpinner.isEnabled=false
            activity.transportCharges.isEnabled=false
            activity.dateOfDelivery.isEnabled=false

            activity.parentOfButton.visibility=View.GONE

        } else if(!TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.driver_id))){

            activity.helperSpinner.isEnabled=false
            activity.fitterSpinner.isEnabled=false
            activity.transportSpinner.isEnabled=false
            activity.dateOfDelivery.isEnabled=false
            activity.transportCharges.isEnabled=false

            activity.parentOfButton.visibility=View.GONE

        }

        activity.totalAmount.text = responseOrders.OrdAmount

        activity.scanProduct.setOnClickListener {

            val intent = Intent(this, QrCodeWithProductActivity::class.java)
            intent.putExtra("orderCode", responseOrders.OrdCode)
            intent.putExtra("spoCode", responseOrders.SapOrdCode)
            startActivity(intent)

        }

    }

    private fun sendDataOnServer(hashmap: HashMap<String, String>) {

        viewModel.sendIdsOnServers(hashmap)

        viewModel.updateOrdersByParts.observe(this, {

            if (it.success.equals("success")) {

                MyUtils.createToast(this, it.message)

                mySharedPreferences!!.setBooleanKey(MySharedPreferences.isUpdate, true)

            } else {

                mySharedPreferences!!.setBooleanKey(MySharedPreferences.isUpdate, true)

                MyUtils.createToast(this, it.message)

            }

        })

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiInterface)
        ).get(OrdersViewModel::class.java)

        setupObservers()

        refreshButton!!.setOnClickListener {

            setupObservers()

        }


        viewModel.noInternet.observe(this, {
            if (it) {
                no_internet_connection.visibility = View.VISIBLE
                MyUtils.createToast(this, "No Internet Connection!")
            } else {
                no_internet_connection.visibility = View.GONE
            }
        })

        viewModel.errorMessage.observe(this, {

            MyUtils.createToast(this.applicationContext, it)

            no_internet_connection.visibility = View.VISIBLE

        })

        viewModel.loading.observe(this, {
            if (it) {
                progressBar!!.visibility = View.VISIBLE
                noDataFound!!.visibility = View.GONE
                no_internet_connection.visibility = View.GONE

            } else {

                progressBar!!.visibility = View.GONE

            }
        })

    }

    private fun setupObservers() {

        viewModel.getEmployeeDetails()

        viewModel.employeeDetailsModal.removeObservers(this)

        viewModel.employeeDetailsModal.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(this.applicationContext, it.message)

                if (it.message.equals("No Data Available")) {

                    noDataFound!!.visibility = View.VISIBLE

                } else {

                    noDataFound!!.visibility = View.GONE

                }

                return@observe

            } else {

                noDataFound!!.visibility = View.GONE
                val employees = it.Employees

                helperArrayList.clear()
                fitterArrayList.clear()

                employees!!.forEach {

                    val empId = it.EmpId
                    if (it.Role.equals("Fitter")) {

                        val keyPairBoolData = KeyPairBoolData()
                        keyPairBoolData.id = empId!!.toLong()
                        keyPairBoolData.isSelected = false
                        val fitters = responseOrders!!.Fitters
                        fitters!!.split(",").forEach {
                            if (empId.equals(it, true)) {
                                keyPairBoolData.isSelected = true
                            }
                        }

                        keyPairBoolData.name = "${it.FirstName} ${it.MiddleName} ${it.LastName}"
                        fitterArrayList.add(keyPairBoolData)

                    } else if (it.Role.equals("Helper")) {

                        val keyPairBoolData = KeyPairBoolData()
                        keyPairBoolData.id = empId!!.toLong()
                        keyPairBoolData.isSelected = false
                        val helpers = responseOrders!!.Helpers
                        helpers!!.split(",").forEach {
                            if (empId.equals(it, true)) {
                                keyPairBoolData.isSelected = true
                            }
                        }

                        keyPairBoolData.name = "${it.FirstName} ${it.MiddleName} ${it.LastName}"
                        helperArrayList.add(keyPairBoolData)

                    }

                }

                callTranporterApi()

                onSetFitterSpinnerData(
                    activity.fitterSpinner,
                    fitterArrayList
                ) // data set on fitter
                onSetHelperSpinnerData(
                    activity.helperSpinner,
                    helperArrayList
                ) // data set on helper

            }

        })

    }

    private fun callTranporterApi() {

        viewModel.getTransporter()

        viewModel.allTransporters.removeObservers(this)

        viewModel.allTransporters.observe(this, {

            if (it.success!!.equals("success")) {

                transportArraylist.clear()

                it.Transporters!!.forEach {

                    val empId = it.DriverId
                    val keyPairBoolData = KeyPairBoolData()
                    keyPairBoolData.id = it.DriverId!!.toLong()
                    keyPairBoolData.isSelected = false
                    val transporters = responseOrders!!.Transporters
                    transporters!!.split(",").forEach {
                        if (empId.equals(it, true)) {
                            keyPairBoolData.isSelected = true
                        }
                    }

                    keyPairBoolData.name = "${it.DriverName}"
                    transportArraylist.add(keyPairBoolData)

                }

                onSetTransportSpinnerData(activity.transportSpinner, transportArraylist)

            }

        })

    }

    private fun setUpLocationData() {

        val address = ordJson!!.getAddress()

        addressOfShipping =
            address!!.getShippingAddress()!!.addLandmark + " " + address.getShippingAddress()!!.addPinCode + " " + " " + address.getShippingAddress()!!.addLine1 + " " + address.getShippingAddress()!!.addLine2 + " " + address.getShippingAddress()!!.addCity + " " + address.getShippingAddress()!!.addState

        getDistanceViaLatLong(addressOfShipping!!)

        activity.openMap.setOnClickListener {

            if (!TextUtils.isEmpty(uri)) {

                try {

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")
                    startActivity(intent)

                } catch (e: ActivityNotFoundException){

                    try {
                        val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(unrestrictedIntent)
                    } catch (innerEx: ActivityNotFoundException) {
                        Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG)
                            .show()
                    }

                }

            }

        }

    }

    private fun onSetFitterSpinnerData(
        spinner: MultiSpinnerSearch,
        fitterArrayList: ArrayList<KeyPairBoolData>
    ) {
        spinner.isSearchEnabled = true
        spinner.isColorSeparation = true
        spinner.setSearchHint("Search Fitter")
        spinner.setEmptyTitle("Not Data Found!")
        spinner.isShowSelectAllButton = false
        spinner.setClearText("Clear")
        spinner.setItems(fitterArrayList, MultiSpinnerListener { items ->

        })

    }

    private fun onSetTransportSpinnerData(
        spinner: MultiSpinnerSearch,
        transportArraylist: ArrayList<KeyPairBoolData>
    ) {

        spinner.isColorSeparation = true
        spinner.isSearchEnabled = true
        spinner.setSearchHint("Search Transport")
        spinner.setClearText("Clear")
        spinner.setEmptyTitle("Not Data Found!")
        spinner.isShowSelectAllButton = false
        spinner.setItems(transportArraylist,
            MultiSpinnerListener { items ->

            })


    }

    private fun onSetHelperSpinnerData(
        spinner: MultiSpinnerSearch,
        helperArrayList: ArrayList<KeyPairBoolData>
    ) {
        spinner.isSearchEnabled = true
        spinner.isColorSeparation = true
        spinner.setSearchHint("Search Helper")
        spinner.setEmptyTitle("Not Data Found!")
        spinner.isShowSelectAllButton = false
        spinner.setClearText("Clear")

        spinner.setItems(helperArrayList,
            MultiSpinnerListener { items ->

            })
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        activity.dateOfDelivery.text="$year-${month+1}-$dayOfMonth"

    }

}