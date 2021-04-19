package com.virtual_market.planetshipmentapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.budiyev.android.codescanner.*
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.virtual_market.planetshipmentapp.Adapter.NestedProductAndPartsAdapter
import com.virtual_market.planetshipmentapp.BuildConfig
import com.virtual_market.planetshipmentapp.Fragment.ConfirmationDialogFragment
import com.virtual_market.planetshipmentapp.Fragment.FeedbackDialogFragment
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.virtualmarket.api.RetrofitClient
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_qr_code_with_product.*
import kotlinx.android.synthetic.main.activity_show_client_address.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.exceptions.UploadError
import net.gotev.uploadservice.exceptions.UserCancelledUploadException
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class QrCodeWithProductActivity : AppCompatActivity() {

    private val REQUEST_CODE_CHOOSE: Int = 1001
    private lateinit var responseUserLogin: ResponseUserLogin
    private var spoCode: String? = null
    private lateinit var nestedProductAndPartsAdapter: NestedProductAndPartsAdapter
    private lateinit var viewModel: OrdersViewModel
    private var noDataFound: RelativeLayout? = null
    private var orderCode: String? = null
    private var text_as_button: TextView? = null
    private var mark_button: RelativeLayout? = null
    private var distnumber = ArrayList<String>()
    private var scanner: CodeScanner? = null
    private var parent_of_lottie: LinearLayout? = null
    private var scan_again: RelativeLayout? = null
    private var refreshButton: RelativeLayout? = null
    private var progressBar: RelativeLayout? = null
    private var feedbackText: RelativeLayout? = null
    private var image_upload: RelativeLayout? = null
    private var button_of_parent: RelativeLayout? = null
    private var parent_of_feedback: LinearLayout? = null
    private lateinit var showModel: ArrayList<SerialProductListModel>
    private var lattie: LottieAnimationView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_with_product)

        responseUserLogin = (applicationContext as PlanetShippingApplication).responseUserLogin

        if (intent != null) {

            orderCode = intent.getStringExtra("orderCode")
            spoCode = intent.getStringExtra("spoCode")

        }

        noDataFound = findViewById(R.id.no_data_found)
        noDataFound!!.visibility = View.GONE

        progressBar = findViewById(R.id.progress_bar)
        text_as_button = findViewById(R.id.text_as_button)
        mark_button = findViewById(R.id.mark_button)
        feedbackText = findViewById(R.id.feedbackText)
        image_upload = findViewById(R.id.image_upload)
        parent_of_feedback = findViewById(R.id.parent_of_feedback)
        button_of_parent = findViewById(R.id.button_of_parent)

        image_upload!!.setOnClickListener {

            askPermissionsForStorage()

        }

        if (responseUserLogin.Role!!.equals("Stores", true)) {

            button_of_parent!!.visibility = View.VISIBLE
            text_as_button!!.text = "Mark Shipped"

        } else if (responseUserLogin.Role.equals("Fitter", true)) {

            button_of_parent!!.visibility = View.VISIBLE
            text_as_button!!.text = "Mark Delivered"

        } else {

            button_of_parent!!.visibility = View.GONE

        }

        feedbackText!!.setOnClickListener {

            val feedbackDialogFragment = FeedbackDialogFragment()
            feedbackDialogFragment.isCancelable = false
            feedbackDialogFragment.show(supportFragmentManager, "feedbackDialogFragment")

            feedbackDialogFragment.setOnClickListener(object :
                FeedbackDialogFragment.OnClickListener {
                override fun onClick(toString: String) {

                    // todo here

                }

            })

        }

        mark_button!!.setOnClickListener {

            val now: Calendar = Calendar.getInstance()
            val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)
            val month = now.get(Calendar.MONTH)
            val year = now.get(Calendar.YEAR)

            var hashmap: HashMap<String, String>?

            if (responseUserLogin.Role.equals("Fitter")) {

                val chekedProduct = isChekedProduct()

                if(!chekedProduct){

                    MyUtils.createToast(this,"Add all the sub products of a product.")

                    return@setOnClickListener
                }

                val arrayList = nestedProductAndPartsAdapter.getSerialId()

                if(arrayList.isEmpty()){

                    MyUtils.createToast(this,"Select A Product")
                    return@setOnClickListener

                }

                val confirmationDialogFragment = ConfirmationDialogFragment()
                confirmationDialogFragment.show(
                    supportFragmentManager,
                    "confirmationDialogFragment"
                )

                confirmationDialogFragment.setOnClickListener(object :
                    ConfirmationDialogFragment.OnClickListener {
                    override fun onClick() {

                        val join = TextUtils.join(",", arrayList)

                        MyUtils.clearAllMap()
                        MyUtils.setHashmap("SerialIds", join)
                        MyUtils.setHashmap("DeliveryStatus", "Delivered")
                        hashmap =
                            MyUtils.setHashmap("DeliveryDate", "${year}-${month}-${dayOfMonth}")

                        sendDocumentOnServer(hashmap, true)
                    }

                })

            } else if (responseUserLogin.Role.equals("Stores")) {

                val chekedProduct = isChekedProduct()// checked a product all subproduct chcked or not

                if(!chekedProduct){

                    MyUtils.createToast(this,"Add all the sub products of a product.")

                    return@setOnClickListener
                }

                val arrayList1 = nestedProductAndPartsAdapter.getSerialId()

                if(arrayList1.isEmpty()){

                    MyUtils.createToast(this,"Select A Product")
                    return@setOnClickListener

                }

                val join = TextUtils.join(",", arrayList1)
                MyUtils.clearAllMap()
                MyUtils.setHashmap("SerialIds", join)
                MyUtils.setHashmap("ShipStatus", "Shipped")
                hashmap = MyUtils.setHashmap("ShipDate", "${year}-${month}-${dayOfMonth}")

                sendDocumentOnServer(hashmap, false)


            }

        }

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

    private fun isChekedProduct():Boolean {
        val checkedArrayList = nestedProductAndPartsAdapter.getCheckedArrayList()

        Log.i("TAG", "isChekedProduct: "+checkedArrayList)

        if(checkedArrayList.isEmpty())
            return false

        checkedArrayList.forEach {
            if(!it.isProduct || !it.isSubProduct){
                return false
            }
        }

        return true
    }

    private fun sendDocumentOnServer(hashmap: HashMap<String, String>?, fitter: Boolean) {

        viewModel.sendDocumentOnServer(hashmap!!)
        viewModel.documentOnServer.removeObservers(this)
        viewModel.documentOnServer.observe(this, {

            if (fitter && it.success!!.equals(
                    "success",
                    true
                )
            ) { // after success open signature pad

                val intent = Intent(this, SignaturePadActivity::class.java)
                intent.putExtra("ordCode",orderCode)
                startActivity(intent)

            }

            MyUtils.createToast(applicationContext, it.message)

            progressBar!!.visibility = View.GONE

        })

    }

    private fun askPermissionsForStorage() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        val rationale = "Please provide Storage And Camera permission so that you can Upload Images"
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
                    openStorage()
                }

                override fun onDenied(
                    context: Context,
                    deniedPermissions: java.util.ArrayList<String>
                ) {
                    // permission denied, block the feature.
                }
            })
    }

    private fun openStorage() {
        Matisse.from(this)
            .choose(MimeType.ofImage(), true)
            .countable(true)
            .maxSelectable(10)
            .capture(true)
            .captureStrategy(
                CaptureStrategy(true, BuildConfig.APPLICATION_ID + ".provider")
            )
            .gridExpectedSize(
                resources.getDimensionPixelSize(R.dimen._100ssp)
            )
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showSingleMediaType(true)
            .originalEnable(true)
            .maxOriginalSize(10)
            .autoHideToolbarOnSingleTap(true)
            .forResult(REQUEST_CODE_CHOOSE)
    }

    private fun compressedImage(storagelist: ArrayList<String>) {

        val files2 = ArrayList<File>()
        progressBar!!.visibility = View.VISIBLE

        Thread {

            for (i in storagelist.indices) {
                try {
                    val compressedCover = Compressor(this)
                        .setMaxWidth(800)
                        .setMaxHeight(800)
                        .setQuality(85)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(cacheDir.absolutePath)
                        .compressToFile(File(storagelist[i]))
                    files2.add(compressedCover)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            runOnUiThread {

                uploadFileOnServer(files2)
                progressBar!!.visibility = View.GONE

            }
        }.start()
    }

    private fun uploadFileOnServer(files: ArrayList<File>) {
        val multipartUploadRequest: MultipartUploadRequest =
            MultipartUploadRequest(
                this, RetrofitClient.MainServer + "AOM"
            )
                .setBasicAuth("planet", "planet@#ioi$&*Tqhodktsnifk")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("X-API-KEY", "cw00ggcsw4co0g804gcggwo088g4kokgk88sso4s")
                .addParameter("OrdCode", orderCode!!)
                .addParameter("EmpId", responseUserLogin.EmpId!!)
                .setAutoDeleteFilesAfterSuccessfulUpload(false)
                .setUsesFixedLengthStreamingMode(true)
                .setMaxRetries(5)
                .setMethod("POST")
        try {
            var listing_logo: String

            for (i in files) {
                listing_logo = UUID.randomUUID().toString().replace("-".toRegex(), "")
                multipartUploadRequest.addFileToUpload(
                    i.path,
                    "Image[]",
                    "$listing_logo.jpg",
                    "UTF-8"
                )
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        multipartUploadRequest.subscribe(this, this, object : RequestObserverDelegate {
            override fun onCompleted(context: Context, uploadInfo: UploadInfo) {

            }

            override fun onCompletedWhileNotObserving() {
            }

            override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {

                progressBar!!.visibility = View.GONE

                when (exception) {
                    is UserCancelledUploadException -> {
                        Log.e("RECEIVER", "Error, user cancelled upload: $uploadInfo")
                    }

                    is UploadError -> {

                        MyUtils.createToast(
                            this@QrCodeWithProductActivity,
                            exception.serverResponse.bodyString
                        )
                        Log.e("RECEIVER", "Error, upload error: ${exception.serverResponse}")
                    }

                    else -> {
                        Log.e("RECEIVER", "Error: $uploadInfo", exception)
                    }
                }

            }

            override fun onProgress(context: Context, uploadInfo: UploadInfo) {
            }

            override fun onSuccess(
                context: Context,
                uploadInfo: UploadInfo,
                serverResponse: ServerResponse
            ) {

                progressBar!!.visibility = View.GONE

                Log.e("RECEIVER", "Error: "+serverResponse.bodyString)

                MyUtils.createToast(
                    this@QrCodeWithProductActivity,
                    serverResponse.bodyString
                )

            }

        })
        multipartUploadRequest.startUpload()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE && data != null) {

                val storagelist = ArrayList<String>()
                storagelist.addAll(Matisse.obtainPathResult(data))
                compressedImage(storagelist)

            }
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiInterface)
        ).get(OrdersViewModel::class.java)

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

            progressBar!!.visibility = View.GONE

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

                parent_of_lottie!!.visibility = View.VISIBLE
                lattie!!.playAnimation()

                distnumber.addAll(nestedProductAndPartsAdapter.getArrayList())
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
                noDataFound!!.visibility = View.GONE

                if(responseUserLogin.Role!!.equals("Fitter",true)){ // fitter jiska shippped status shipped hai,wahi sirf show karo

                    showModel.clear()

                    it.Info!!.forEach {

                         if(it.ShipStatus!!.equals("Shipped",true)){

                             showModel.add(it)

                         }

                     }

                } else {

                    showModel.clear()
                    showModel.addAll(items!!)

                }

                nestedProductAndPartsAdapter.notifyDataSetChanged()

                var i = 0 // button hide and show karna hai, jab product delivered ho jaye , fitters end se, jismai image and feedback dikhani hai

                it.Info!!.forEach {

                    if (responseUserLogin.Role.equals("Fitter") && it.DeliveryStatus.equals("Delivered")) {
                        i++
                    }
                }

                if (responseUserLogin.Role.equals("Fitter") && it.Info!!.size == i) {

                    mark_button!!.visibility = View.GONE
                    parent_of_feedback!!.visibility = View.VISIBLE

                } else if (responseUserLogin.Role.equals("Fitter")) {

                    mark_button!!.visibility=View.VISIBLE
                    parent_of_feedback!!.visibility=View.GONE
//
//                    mark_button!!.visibility = View.GONE
//                    parent_of_feedback!!.visibility = View.VISIBLE

                }


            }

            progressBar!!.visibility = View.GONE

        })

    }

    override fun onResume() {
        super.onResume()

        setupObservers()

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