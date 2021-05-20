package com.virtual_market.planetshipmentapp.Activity

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidbuts.multispinnerfilter.*
import com.virtual_market.planetshipmentapp.Adapter.NestedProductAndPartsAdapter
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
import kotlinx.android.synthetic.main.activity_qr_code_with_product.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread


class OrderDetailsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var nestedProductAndPartsAdapter: NestedProductAndPartsAdapter
    private var customer_details: Customers? = null
    private lateinit var responseUserLogin: ResponseUserLogin
    private var addressOfShipping: String? = null
    private lateinit var viewModel: OrdersViewModel
    private var uri: String? = null
    var result: String? = null
    private var mySharedPreferences: MySharedPreferences? = null
    private var productItem: ArrayList<ProductItem>? = null
    private var progressBar: RelativeLayout? = null
    private var refreshButton: RelativeLayout? = null
    private var responseOrders: ResponseOrders? = null
    private var ordJson: OrdJson? = null
    private val fitterArrayList = ArrayList<KeyPairBoolData>()
    private val helperArrayList = ArrayList<KeyPairBoolData>()
    private val transportArraylist = ArrayList<KeyPairBoolData>()
    private lateinit var activity: ActivityOrderDetailsBinding
    private lateinit var showModelSnl: ArrayList<SerialProductListModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = DataBindingUtil.setContentView(this, R.layout.activity_order_details)

        if (intent != null) {

            responseOrders = intent.getParcelableExtra("responseOrders")
            ordJson = intent.getParcelableExtra("ordJson")
            productItem = intent.getParcelableArrayListExtra("productItem")
            customer_details = intent.getParcelableExtra("customer_details")

        }

        responseUserLogin = (application as PlanetShippingApplication).responseUserLogin

        progressBar = findViewById(R.id.progress_bar)

        refreshButton = findViewById(R.id.refresh_button)

        setUpLocationData()

        activity.backButton.setOnClickListener {

            onBackPressed()

        }

        mySharedPreferences = MySharedPreferences.getInstance(this)

        showModelSnl = ArrayList()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        activity.allProductList.layoutManager = linearLayoutManager
        nestedProductAndPartsAdapter = NestedProductAndPartsAdapter(this, showModelSnl)

        nestedProductAndPartsAdapter.isOrderdetails(true)

        activity.allProductList.adapter = nestedProductAndPartsAdapter

        setDataOnPage(responseOrders!!)

        setupViewModel()

    }

    @SuppressLint("SetTextI18n")
    fun getDistanceViaLatLong(addressOfShipping: String) {

        thread {
            // calculate latitude and longitude by address
            val locationFromAddress: LatLng = getLocationFromAddress(this, addressOfShipping)

            // calculate distance
            if (locationFromAddress.lattitude > 0 && locationFromAddress.longitude > 0) {

                val selectedLocation = Location("locationA")

                val latitude = mySharedPreferences!!.getStringkey(MySharedPreferences.myLatitude)
                val longitude = mySharedPreferences!!.getStringkey(MySharedPreferences.myLongitude)

                if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {

                    selectedLocation.latitude = latitude.toDouble()
                    selectedLocation.longitude = longitude.toDouble()

                    val nearLocations = Location("locationB")

                    nearLocations.latitude = locationFromAddress.lattitude
                    nearLocations.longitude = locationFromAddress.longitude

                    getAddressFromLocation(nearLocations.latitude, nearLocations.longitude, this)

                    val distance = selectedLocation.distanceTo(nearLocations)

                    val s = String.format("%.2f", distance / 1000)

                    runOnUiThread {

                        uri =
                            "http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + nearLocations.latitude + "," + nearLocations.longitude

                        activity.shippingDistance.text = "Distance to Address : $s K.M"

                    }

                }


            }

        }

    }

    private fun getAddressFromLocation(lat: Double, long: Double, context: Context?) {
        val thread: Thread = object : Thread() {
            override fun run() {
                val geoCoder = Geocoder(context, Locale.getDefault())
                try {
                    val list = geoCoder.getFromLocation(
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

    private fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng {
        val coder = Geocoder(context)
        var address: List<Address>? = null
        val p1: LatLng?

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
        if (address != null && address.isNotEmpty()) {
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } else {
            p1 = LatLng(0.0, 0.0)
        }
        return p1
    }

    private fun getIdsWithComma(spinner: MultiSpinnerSearch): String {

        val selectedIds = spinner.selectedIds

        return TextUtils.join(",", selectedIds)

    }

    @SuppressLint("SetTextI18n")
    private fun setDataOnPage(responseOrders: ResponseOrders) {

        val address = ordJson!!.getAddress()

        activity.orderNo.text = responseOrders.OrdCode
        activity.date.text = "Delivery Date: " + responseOrders.DeliveryDate
        activity.noOfItems.text = responseOrders.ItemCount
        activity.address.text = address!!.getShippingAddress()!!.addType
        activity.address2.text =
            address.getShippingAddress()!!.addLine1 + " " + address.getShippingAddress()!!.addLandmark + " " + address.getShippingAddress()!!.addCity + " " + address.getShippingAddress()!!.addPinCode + " " + address.getShippingAddress()!!.addState
        activity.address3.text =
            address.getShippingAddress()!!.addLine2 + " " + address.getShippingAddress()!!.addFullName + " " + address.getShippingAddress()!!.companyName + " " + address.getShippingAddress()!!.addMobNo

        activity.customerName.text = customer_details!!.customerName
        activity.phoneNumber.text = customer_details!!.mobNo

        activity.helperIncentives.setText(responseOrders.HelperIncentives)
        activity.vehicleIncentives.setText(responseOrders.VehicleIncentives)

        if (!TextUtils.isEmpty(responseOrders.OrdStatus) && !responseOrders.OrdStatus!!.equals("Created", true))
            activity.orderStatus.text = responseOrders.OrdStatus
        else
            activity.orderStatus.text = "Pending"


        activity.parentOfPhone.setOnClickListener {

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + customer_details!!.mobNo)
            startActivity(intent)

        }


        activity.installationImages.setOnClickListener {

            val intent = Intent(this, InstallationImagesActivity::class.java)
            intent.putExtra("ordCode", responseOrders.OrdCode)
            startActivity(intent)

        }

        activity.deliveryDateSubmit.setOnClickListener {

            MyUtils.clearAllMap()
            MyUtils.setHashmap("DeliveryDate", dateOfDelivery.text.toString())
            val hashmap = MyUtils.setHashmap("OrdCode", responseOrders.OrdCode)
            sendDataOnServer(hashmap)

        }

        dateOfDelivery.text = responseOrders.DeliveryDate

        activity.dateOfDelivery.setOnClickListener {

            val now: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate =
                Date().time - (Date().time % (24 * 60 * 60 * 1000))
            datePickerDialog.show()

        }

        activity.submitIds.setOnClickListener {

            MyUtils.clearAllMap()
            MyUtils.setHashmap("Fitters", getIdsWithComma(activity.fitterSpinner))
            MyUtils.setHashmap("Helpers", getIdsWithComma(activity.helperSpinner))
            MyUtils.setHashmap("TransportCharges", activity.transportCharges.text.toString())
            MyUtils.setHashmap("FitterIncentives", activity.vehicleIncentives.text.toString())
            MyUtils.setHashmap("HelperIncentives", activity.helperIncentives.text.toString())
            MyUtils.setHashmap("OrdCode", responseOrders.OrdCode)
            val hashmap =
                MyUtils.setHashmap("Transporters", getIdsWithComma(activity.transportSpinner))

            sendDataOnServer(hashmap)

        }

        activity.transportCharges.setText(responseOrders.TransportCharges)

        if (responseUserLogin.Role.equals("Fitter")) {

            activity.helperSpinner.isEnabled = false
            activity.fitterSpinner.isEnabled = false
            activity.transportSpinner.isEnabled = false
            activity.transportCharges.isEnabled = false
            activity.vehicleIncentives.isEnabled = false
            activity.helperIncentives.isEnabled = false
            activity.dateOfDelivery.isEnabled = false

            activity.parentOfButton.visibility = View.GONE
            activity.trasnportParent.visibility = View.GONE

            activity.parentOfDeliveryDate.visibility = View.GONE

        } else if (responseUserLogin.Role.equals("Helper")) {

            activity.helperSpinner.isEnabled = false
            activity.fitterSpinner.isEnabled = false
            activity.transportSpinner.isEnabled = false
            activity.transportCharges.isEnabled = false
            activity.dateOfDelivery.isEnabled = false
            activity.vehicleIncentives.isEnabled = false
            activity.helperIncentives.isEnabled = false

            activity.parentOfButton.visibility = View.GONE
            activity.trasnportParent.visibility = View.GONE

            activity.parentOfDeliveryDate.visibility = View.GONE

        } else if (!TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.driver_id))) {

            activity.helperSpinner.isEnabled = false
            activity.fitterSpinner.isEnabled = false
            activity.transportSpinner.isEnabled = false
            activity.dateOfDelivery.isEnabled = false
            activity.transportCharges.isEnabled = false
            activity.vehicleIncentives.isEnabled = false
            activity.helperIncentives.isEnabled = false

            activity.parentOfButton.visibility = View.GONE

        }

        activity.scanProduct.setOnClickListener {

            val intent = Intent(this, QrCodeWithProductActivity::class.java)
            intent.putExtra("orderCode", responseOrders.OrdCode)
            intent.putExtra("spoCode", responseOrders.SapOrdCode)
            intent.putExtra("customerCode",customer_details!!.customerCode)
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

            progressBar!!.visibility = View.GONE

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

            progressBar!!.visibility=View.GONE

        })

        viewModel.loading.observe(this, {
            if (it) {
                progressBar!!.visibility = View.VISIBLE
                no_internet_connection.visibility = View.GONE

            }
        })

    }

    private fun setupObservers() {

        viewModel.getEmployeeDetails()

        viewModel.employeeDetailsModal.removeObservers(this)

        viewModel.employeeDetailsModal.observe(this, { it ->

            if (it.success.equals("failure")) {

                MyUtils.createToast(this.applicationContext, it.message)

                return@observe

            } else {

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

                setupSNLDATA()

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

    private fun setupSNLDATA() {

        progressBar!!.visibility = View.VISIBLE

        viewModel.dispatchOrdersByParts(responseOrders!!.OrdCode!!)

        viewModel.orderDispatchByParts.removeObservers(this)

        viewModel.orderDispatchByParts.observe(this, {

            if (it.success.equals("failure")) {

                activity.lineOfProduct.visibility = View.GONE

                progressBar!!.visibility = View.GONE

                return@observe

            } else {

                val items = it.Info
                showModelSnl.clear()
                showModelSnl.addAll(items!!)

                if (showModelSnl.size == 0) {
                    activity.lineOfProduct.visibility = View.GONE
                }

                nestedProductAndPartsAdapter.notifyDataSetChanged()

                progressBar!!.visibility = View.GONE

            }

        })

    }

    private fun callTranporterApi() {

        viewModel.getTransporter()

        viewModel.allTransporters.removeObservers(this)

        viewModel.allTransporters.observe(this, { it ->

            if (it.success!!.equals("success", true)) {

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

                    keyPairBoolData.name = "${it.DriverName}  ${it.VehicleType} ${it.VehicleNo}"
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

                } catch (e: ActivityNotFoundException) {

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
        spinner.setItems(fitterArrayList) {

        }

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
        with(spinner) {
            setItems(transportArraylist) {

            }
        }


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

        spinner.setItems(helperArrayList){

            }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        activity.dateOfDelivery.text = "$year-${month + 1}-$dayOfMonth"

    }

}