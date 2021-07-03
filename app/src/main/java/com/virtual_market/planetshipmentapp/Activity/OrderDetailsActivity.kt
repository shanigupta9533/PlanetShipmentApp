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
import android.os.Handler
import android.os.Looper
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
import com.google.common.base.Charsets.UTF_8
import com.google.common.io.CharStreams
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.properties.Delegates


class OrderDetailsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var distanceGlobal: String="0.0"
    private var isHelperAssign: Boolean=false
    private var isFitterAssign: Boolean=false
    private var totalPriceHelper by Delegates.notNull<Double>()
    private var totalPriceFitter by Delegates.notNull<Double>()
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
    private val itemsCodeProduct = ArrayList<ProductItem>()
    private val fitterArrayList = ArrayList<KeyPairBoolData>()
    private val helperArrayList = ArrayList<KeyPairBoolData>()
    private val transportArraylist = ArrayList<KeyPairBoolData>()
    private lateinit var activity: ActivityOrderDetailsBinding
    private lateinit var showModelSnl: ArrayList<SerialProductListModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = DataBindingUtil.setContentView(this, R.layout.activity_order_details)

        totalPriceFitter = 0.0
        totalPriceHelper = 0.0

        if (intent != null) {

            responseOrders = intent.getParcelableExtra("responseOrders")
            ordJson = intent.getParcelableExtra("ordJson")
            productItem = intent.getParcelableArrayListExtra("productItem")
            customer_details = intent.getParcelableExtra("customer_details")

            if (intent.getParcelableArrayListExtra<ProductItem>("allItems") != null)
                itemsCodeProduct.addAll(intent.getParcelableArrayListExtra("allItems")!!)

        }

        responseUserLogin = (application as PlanetShippingApplication).responseUserLogin

        progressBar = findViewById(R.id.progress_bar)

        refreshButton = findViewById(R.id.refresh_button)

        activity.backButton.setOnClickListener {

            onBackPressed()

        }

        mySharedPreferences = MySharedPreferences.getInstance(applicationContext)

        showModelSnl = ArrayList()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        activity.allProductList.layoutManager = linearLayoutManager
        nestedProductAndPartsAdapter = NestedProductAndPartsAdapter(this, showModelSnl)

        nestedProductAndPartsAdapter.isOrderdetails(true)

        activity.allProductList.adapter = nestedProductAndPartsAdapter

        setDataOnPage(responseOrders!!)

        setupViewModel()

        // update orders from submitIds button
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

        Handler(Looper.myLooper()!!).postDelayed({

            setUpLocationData()

        },200);

    }

    @SuppressLint("SetTextI18n")
    fun getDistanceViaLatLong(addressOfShipping: String) {

        thread {
            // calculate latitude and longitude by address
            val locationFromAddress: LatLng = getLocationFromAddress(this, addressOfShipping)
            val locationFromAddressOrigin: LatLng = getLocationFromAddress(this, "415004")

            // calculate distance
            if (locationFromAddress.lattitude > 0 && locationFromAddress.longitude > 0) {

                val selectedLocation = Location("locationA")

                val latitude = locationFromAddressOrigin.lattitude
                val longitude = locationFromAddressOrigin.longitude

                if (!TextUtils.isEmpty(latitude.toString()) && !TextUtils.isEmpty(longitude.toString())) {

                    selectedLocation.latitude = latitude.toDouble()
                    selectedLocation.longitude = longitude.toDouble()

                    val nearLocations = Location("locationB")

                    nearLocations.latitude = locationFromAddress.lattitude
                    nearLocations.longitude = locationFromAddress.longitude

                    runOnUiThread {

                        uri =
                            "http://maps.google.com/maps?saddr=" + latitude + "," + longitude + "&daddr=" + nearLocations.latitude + "," + nearLocations.longitude

                    }

                }


            }

        }

    }

    private fun calculateFromDistance(
        itemsCodeProduct: java.util.ArrayList<ProductItem>,
        distance: Float
    ) {

        itemsCodeProduct.forEach {

            if (distance > 0 && distance <= 60) {

                totalPriceFitter += it.indoorFitter!!.toDouble()*it.qty
                totalPriceHelper += it.indoorHelper!!.toDouble()*it.qty

            } else if (distance > 60 && distance <= 150) {

                totalPriceFitter += it.outdoorFitter!!.toDouble()*it.qty
                totalPriceHelper += it.outdoorHelper!!.toDouble()*it.qty

            } else {

                totalPriceFitter += it.dbOutDoorFitter!!.toDouble()*it.qty
                totalPriceHelper += it.dbOutDoorHelper!!.toDouble()*it.qty

            }

        }

        activity.fitterIncentives.setText(totalPriceFitter.toString())
        activity.helperIncentives.setText(totalPriceHelper.toString())

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

    private fun getIdsWithComma(spinner: SingleSpinnerSearch): String {

        val selectedIds = spinner.selectedIds

        return TextUtils.join(",", selectedIds)

    }

    private fun getIdsWithComma2(spinner: MultiSpinnerSearch): String {

        val selectedIds = spinner.selectedIds

        return TextUtils.join(",", selectedIds)

    }

    override fun onResume() {
        super.onResume()

        if(!TextUtils.isEmpty(mySharedPreferences?.getStringkey(MySharedPreferences.shippedOrDelivered))){

            activity.orderStatus.text=mySharedPreferences?.getStringkey(MySharedPreferences.shippedOrDelivered)
            responseOrders?.OrdStatus=mySharedPreferences?.getStringkey(MySharedPreferences.shippedOrDelivered)

            mySharedPreferences!!.setBooleanKey(MySharedPreferences.isUpdate, true)

            mySharedPreferences!!.setStringKey(MySharedPreferences.shippedOrDelivered, "")

        }

    }

    fun getDistance(pincode1:String, pincode2:String): String? {

        progressBar?.visibility = View.VISIBLE

        var parsedDistance: String = "";
        var response: String
        val thread = Thread {
            try {
                val url =
                    URL("https://maps.googleapis.com/maps/api/distancematrix/json?destinations=$pincode1&origins=$pincode2&units=imperial&key=AIzaSyAQkGs-Bxr1PEfpsq7G8XhAOYsoi6NRKao")
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                val `in`: InputStream = BufferedInputStream(conn.inputStream)

                response = CharStreams.toString(
                    InputStreamReader(
                        `in`, UTF_8
                    )
                )

                val jsonObject = JSONObject(response)
                val rows: JSONArray = jsonObject.getJSONArray("rows")

                    val elements: JSONObject = rows.getJSONObject(0)

                    if(elements.length()>0){
                        val elementsArray: JSONArray = elements.getJSONArray("elements")
                        val distanceObject: JSONObject = elementsArray.getJSONObject(0)
                        val distance:JSONObject=distanceObject.getJSONObject("distance")

                        if(!distance.isNull("value")){

                            runOnUiThread {

                                val kilometerInLong:Long=distance.getString("value").toLong()/1000 + 1
                                activity.shippingDistance.text = "Distance to Address : $kilometerInLong K.M"
                                distanceGlobal=kilometerInLong.toString()

                                if (!TextUtils.isEmpty(distanceGlobal) &&
                                    !TextUtils.isEmpty(responseOrders?.FitterIncentives) &&
                                    !TextUtils.isEmpty(responseOrders?.HelperIncentives) &&
                                    !TextUtils.isEmpty(responseOrders?.TransportCharges) &&
                                    responseOrders?.FitterIncentives?.toFloat() == 0f &&
                                    responseOrders?.HelperIncentives?.toFloat() == 0f &&
                                    responseOrders?.TransportCharges?.toFloat() == 0f
                                ) {

                                    calculateFromDistance(itemsCodeProduct, distanceGlobal.toFloat())
                                    callTranporterApi(distanceGlobal.toFloat())

                                } else if (!TextUtils.isEmpty(responseOrders?.FitterIncentives) &&
                                    !TextUtils.isEmpty(responseOrders?.HelperIncentives) &&
                                    !TextUtils.isEmpty(responseOrders?.TransportCharges)
                                ) {

                                    activity.fitterIncentives.setText(responseOrders?.FitterIncentives)
                                    activity.helperIncentives.setText(responseOrders?.HelperIncentives)
                                    activity.transportCharges.setText(responseOrders?.TransportCharges)

                                    isFitterAssign=true
                                    isHelperAssign=true

                                    callTranporterApi(distanceGlobal.toFloat())

                                }

                            }

                        }

                    }

//                runOnUiThread {
//
//                    activity.shippingDistance.text = "Distance to Address : $parsedDistance K.M"
//
//                }

            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        thread.start()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return parsedDistance
    }

    @SuppressLint("SetTextI18n")
    private fun setDataOnPage(responseOrders: ResponseOrders) {

        val address = ordJson!!.getAddress()

        activity.orderNo.text = responseOrders.OrdCode
        activity.empName.text = responseOrders.Emp_name
        activity.deliveryType.text = responseOrders.DeliveryType
        activity.date.text = "Delivery Date: " + responseOrders.DeliveryDate
        activity.noOfItems.text = responseOrders.ItemCount
//    todo    activity.address.text = address!!.getShippingAddress()!!.addType

        var landmark:String?=""
        var line2:String?=""

       if(!TextUtils.isEmpty(address?.getShippingAddress()!!.addLandmark)){
           landmark="\n"+address.getShippingAddress()!!.addLandmark
       }

        if(!TextUtils.isEmpty(address.getShippingAddress()!!.addLine2)){
            line2="\n"+address.getShippingAddress()!!.addLine2
        }

        activity.address2.text =
            "${address.getShippingAddress()!!.addType} : "+address.getShippingAddress()!!.addLine1 + "$landmark " +"$line2 " + address.getShippingAddress()!!.addCity + "\n" + address.getShippingAddress()!!.addPinCode + " " + address.getShippingAddress()!!.addState
//   todo     activity.address3.text =
//            address.getShippingAddress()!!.addLine2 + " " + address.getShippingAddress()!!.addFullName + " " + address.getShippingAddress()!!.companyName + " " + address.getShippingAddress()!!.addMobNo

        activity.customerName.text = customer_details!!.customerName
        activity.phoneNumber.text = customer_details!!.mobNo

        if (!TextUtils.isEmpty(responseOrders.OrdStatus) && !responseOrders.OrdStatus!!.equals(
                "Created",
                true
            )
        )
            activity.orderStatus.text = responseOrders.OrdStatus
        else
            activity.orderStatus.text = "Pending"

        // todo disable delivered with spinner
        if (!TextUtils.isEmpty(responseOrders.OrdStatus) && responseOrders.OrdStatus!!.equals(
                "Delivered",
                true
            )
        ) {
            activity.fitterSpinner.isEnabled = false
            activity.helperSpinner.isEnabled = false
            activity.transportSpinner.isEnabled = false
            activity.dateOfDelivery.isEnabled=false
        }

        activity.parentOfPhone.setOnClickListener {

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + customer_details!!.mobNo)
            startActivity(intent)

        }


        activity.installationImages.setOnClickListener {

            val intent = Intent(this, InstallationImagesActivity::class.java)
            intent.putExtra("ordCode", responseOrders.OrdCode)
            intent.putExtra("delivery_status", responseOrders.OrdStatus)
            startActivity(intent)

        }

        activity.deliveryDateSubmit.setOnClickListener {

            MyUtils.clearAllMap()
            MyUtils.setHashmap("DeliveryDate", dateOfDelivery.text.toString())
            MyUtils.setHashmap("OrdCode", responseOrders.OrdCode)
            val hashmap = MyUtils.setHashmap("OrdStatus", responseOrders.OrdStatus)
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
            MyUtils.setHashmap("Fitters", getIdsWithComma2(activity.fitterSpinner))
            MyUtils.setHashmap("Helpers", getIdsWithComma2(activity.helperSpinner))
            MyUtils.setHashmap("TransportCharges", activity.transportCharges.text.toString())
            MyUtils.setHashmap("FitterIncentives", activity.fitterIncentives.text.toString())
            MyUtils.setHashmap("HelperIncentives", activity.helperIncentives.text.toString())
            MyUtils.setHashmap("OrdCode", responseOrders.OrdCode)
            MyUtils.setHashmap("OrdStatus", responseOrders.OrdStatus)
            val hashmap =
                MyUtils.setHashmap("Transporters", getIdsWithComma(activity.transportSpinner))

            sendDataOnServer(hashmap)

        }

//        activity.transportCharges.setText(responseOrders.TransportCharges)

        if (responseUserLogin.Role.equals("Fitter")) {

            activity.helperSpinner.isEnabled = false
            activity.fitterSpinner.isEnabled = false
            activity.transportSpinner.isEnabled = false
            activity.transportCharges.isEnabled = false
            activity.fitterIncentives.isEnabled = false
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
            activity.fitterIncentives.isEnabled = false
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
            activity.fitterIncentives.isEnabled = false
            activity.helperIncentives.isEnabled = false
            activity.parentOfButton.visibility = View.GONE

        }

        activity.scanProduct.setOnClickListener {

            val intent = Intent(this, QrCodeWithProductActivity::class.java)
            intent.putExtra("orderCode", responseOrders.OrdCode)
            intent.putExtra("spoCode", responseOrders.SapOrdCode)
            intent.putExtra("customerCode", customer_details!!.customerCode)
            startActivity(intent)

        }

    }

    private fun sendDataOnServer(hashmap: HashMap<String, String>) {

        viewModel.sendIdsOnServers(hashmap)

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

            progressBar!!.visibility = View.GONE

        })

        viewModel.loading.observe(this, {
            if (it) {
                progressBar!!.visibility = View.VISIBLE
                no_internet_connection.visibility = View.GONE
            } else {
                progressBar!!.visibility = View.GONE
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

                return@observe

            } else {

                val items = it.Info
                showModelSnl.clear()
                showModelSnl.addAll(items!!)

                if (showModelSnl.size == 0) {
                    activity.lineOfProduct.visibility = View.GONE
                }

                nestedProductAndPartsAdapter.notifyDataSetChanged()

            }

        })

    }

    private fun callTranporterApi(distance: Float) {

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
                    keyPairBoolData.`object` = it
                    val transporters = responseOrders!!.Transporters
                    transporters!!.split(",").forEach {
                        if (empId.equals(it, true)) {
                            keyPairBoolData.isSelected = true
                        }
                    }

                    keyPairBoolData.name = "${it.DriverName}  ${it.VehicleType} ${it.VehicleNo}"
                    transportArraylist.add(keyPairBoolData)
                }

                onSetTransportSpinnerData(activity.transportSpinner, transportArraylist, distance)

                progressBar?.visibility = View.GONE

            }

        })

    }

    private fun setUpLocationData() {

        val address = ordJson!!.getAddress()

        addressOfShipping =
            address?.getShippingAddress()!!.addPinCode!!

        getDistanceViaLatLong(address.getShippingAddress()!!.addPinCode!!)

        getDistance(
            "415004",
            address.getShippingAddress()!!.addPinCode!!
        )

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

            val totalPriceFitter2 = if (it.isEmpty()) {
                    1 * getFitterCalculatedValue(itemsCodeProduct,distanceGlobal.toFloat())
                } else {
                    it.size * getFitterCalculatedValue(itemsCodeProduct,distanceGlobal.toFloat())
                }

                activity.fitterIncentives.setText(totalPriceFitter2.toString())


        }

    }

    private fun getFitterCalculatedValue(
        itemsCodeProduct: java.util.ArrayList<ProductItem>,
        distance: Float
    ): Double {

        var totalPriceFitter:Double=0.0

        itemsCodeProduct.forEach {

            totalPriceFitter += if (distance > 0 && distance <= 60) {

                it.indoorFitter!!.toDouble()*it.qty

            } else if (distance > 60 && distance <= 150) {

                it.outdoorFitter!!.toDouble()*it.qty

            } else {

                it.dbOutDoorFitter!!.toDouble()*it.qty

            }

        }

        return totalPriceFitter

    }

    private fun getHelperCalculatedValue(
        itemsCodeProduct: java.util.ArrayList<ProductItem>,
        distance: Float
    ): Double {

        var totalPriceHelper:Double=0.0

        itemsCodeProduct.forEach {

            totalPriceHelper += if (distance > 0 && distance <= 60) {

                it.indoorHelper!!.toDouble()*it.qty

            } else if (distance > 60 && distance <= 150) {

                it.outdoorHelper!!.toDouble()*it.qty

            } else {

                it.dbOutDoorHelper!!.toDouble()*it.qty

            }

        }

        return totalPriceHelper

    }

    private fun onSetTransportSpinnerData(
        spinner: SingleSpinnerSearch,
        transportArraylist: ArrayList<KeyPairBoolData>,
        distance: Float
    ) {

        progressBar?.visibility = View.GONE

        spinner.isSearchEnabled = true
        spinner.setSearchHint("Search Transport")
        spinner.setEmptyTitle("Not Data Found!")

        spinner.setItems(transportArraylist, object : SingleSpinnerListener {
            override fun onItemsSelected(selectedItem: KeyPairBoolData) {

                val responseTransporter: ResponseTransporter =
                    selectedItem.`object` as ResponseTransporter

                if (responseTransporter.KmRate != null) {

                    val kmRate: Float = responseTransporter.KmRate?.toFloat()!!

                    val calculate:String = if(kmRate<10)
                        (10 * distance).toString()
                    else
                        (kmRate * distance).toString()

                    activity.transportCharges.setText(calculate)
                }

            }

            override fun onClear() {

            }
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

        spinner.setItems(helperArrayList) {

            val totalPriceHelper2 = if (it.isEmpty()) {
                    1 * getHelperCalculatedValue(itemsCodeProduct,distanceGlobal.toFloat())
                } else {
                    it.size * getHelperCalculatedValue(itemsCodeProduct,distanceGlobal.toFloat())
                }

                activity.helperIncentives.setText(totalPriceHelper2.toString())

        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        activity.dateOfDelivery.text = "$year-${month + 1}-$dayOfMonth"

    }

}