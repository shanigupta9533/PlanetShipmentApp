package com.virtual_market.planetshipmentapp.Activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.budiyev.android.codescanner.*
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.virtual_market.planetshipmentapp.Adapter.NestedProductAndPartsAdapter
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.activity_qr_code_with_product.*
import kotlinx.android.synthetic.main.no_internet_connection.*

class QrCodeWithProductActivity : AppCompatActivity() {

    private var spoCode: String? = null
    private var productId: String? = null
    private lateinit var nestedProductAndPartsAdapter: NestedProductAndPartsAdapter
    private lateinit var viewModel: OrdersViewModel
    private var noDataFound: RelativeLayout? = null
    private var orderCode: String? = null
    private var distnumber = ArrayList<String>()
    private val arrayList = ArrayList<String>()
    private var scanner: CodeScanner? = null
    private var parent_of_lottie: LinearLayout? = null
    private var scan_again: RelativeLayout? = null
    private var refreshButton: RelativeLayout? = null
    private var progressBar: RelativeLayout? = null
    private lateinit var showModel: ArrayList<SerialProductListModel>
    private var lattie: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_with_product)

        if (intent != null) {

            orderCode = intent.getStringExtra("orderCode")
            spoCode = intent.getStringExtra("spoCode")

        }

        noDataFound = findViewById(R.id.no_data_found)
        noDataFound!!.visibility = View.GONE

        progressBar = findViewById(R.id.progress_bar)

        refreshButton = findViewById(R.id.refresh_button)

        showModel = ArrayList()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        nestedProductAndPartsAdapter = NestedProductAndPartsAdapter(this, showModel)
        recyclerView.adapter = nestedProductAndPartsAdapter

        setupViewModel()

        parent_of_lottie = findViewById(R.id.parent_of_lottie)
        lattie = findViewById(R.id.animationView)
        scan_again = findViewById(R.id.scan_again)

        scan_again!!.setOnClickListener {

            if (scanner != null) {

                parent_of_lottie!!.visibility = View.GONE
                scanner!!.startPreview()

            }

        }

        if (!checkPermission())
            askPermissions()
        else
            openQrCode()

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
            }
        })

    }

    private fun askPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
        )
        val rationale = "Please provide Camera permission so that you can Scan QrCode"
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(
            this /*context*/,
            permissions,
            rationale,
            options,
            object : PermissionHandler() {
                override fun onGranted() {
                    openQrCode()
                }

                override fun onDenied(
                    context: Context,
                    deniedPermissions: java.util.ArrayList<String>
                ) {
                    // permission denied, block the feature.
                }
            })
    }

    private fun openQrCode() {

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        scanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        scanner!!.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        scanner!!.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        scanner!!.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        scanner!!.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        scanner!!.isAutoFocusEnabled = true // Whether to enable auto focus or not
        scanner!!.isFlashEnabled = false // Whether to enable flash or not

        scanner!!.startPreview()

        // Callbacks
        scanner!!.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()

                val scanCodeString = it

                parent_of_lottie!!.visibility = View.VISIBLE
                lattie!!.playAnimation()

                distnumber.add(it.text)
                nestedProductAndPartsAdapter.setProductId(distnumber)
                nestedProductAndPartsAdapter.notifyDataSetChanged()

                val mp: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.sucess_sound)
                mp.start()

            }
        }
        scanner!!.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    }

    private fun setupObservers() {

        viewModel.dispatchOrdersByParts(orderCode!!)

        viewModel.orderDispatchByParts.removeObservers(this)

        viewModel.orderDispatchByParts.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(this.applicationContext, it.message)

                if (it.message.equals("No Data Available")) {

                    noDataFound!!.visibility = View.VISIBLE

                } else {

                    noDataFound!!.visibility = View.GONE

                }

                progressBar!!.visibility = View.GONE

                return@observe

            } else {

                noDataFound!!.visibility = View.GONE

                val items = it.Info
                showModel.clear()
                showModel.addAll(items!!)
                noDataFound!!.visibility = View.GONE
                nestedProductAndPartsAdapter.notifyDataSetChanged()

            }

            progressBar!!.visibility = View.GONE

        })

    }

    override fun onResume() {
        super.onResume()
        if (checkPermission())
            scanner!!.startPreview()
        else
            askPermissions()

    }

    override fun onPause() {
        super.onPause()
        if (checkPermission())
            scanner!!.releaseResources()

    }


    private fun checkPermission(): Boolean {

        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    }

}