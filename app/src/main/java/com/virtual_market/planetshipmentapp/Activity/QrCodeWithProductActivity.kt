package com.virtual_market.planetshipmentapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.util.isNotEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.budiyev.android.codescanner.*
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.snackbar.Snackbar
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.virtual_market.planetshipmentapp.Adapter.NestedProductAndPartsAdapter
import com.virtual_market.planetshipmentapp.Fragment.ConfirmationDialogFragment
import com.virtual_market.planetshipmentapp.Fragment.ProgressBarDialogFragment
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.Modal.SerialProductListModel
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
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
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_qr_code_with_product.*
import kotlinx.android.synthetic.main.activity_show_client_address.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import net.gotev.uploadservice.UploadServiceConfig
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.exceptions.UploadError
import net.gotev.uploadservice.exceptions.UserCancelledUploadException
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.placeholders.Placeholder
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class QrCodeWithProductActivity : AppCompatActivity() {

    private var progressBarDialogFragment: ProgressBarDialogFragment? = null
    private lateinit var parent_of_view: View
    private var fitter: Boolean = false
    private lateinit var mySharedPreferences: MySharedPreferences
    private var items1 = ArrayList<SerialProductListModel>()
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
    private var isMarkedClicked: Boolean = false
    private var feedbackText: RelativeLayout? = null
    private var image_upload: RelativeLayout? = null
    private var button_of_parent: RelativeLayout? = null
    private var parent_of_feedback: LinearLayout? = null
    private lateinit var showModel: ArrayList<SerialProductListModel>
    private var lattie: LottieAnimationView? = null
    var cameraView: SurfaceView? = null
    var barcode: BarcodeDetector? = null
    var scan_button_text: TextView? = null
    var holder: SurfaceHolder? = null
    private lateinit var detector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    @SuppressLint("SetTextI18n") override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_with_product)

        detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()
        detector.setProcessor(processor)

        mySharedPreferences = MySharedPreferences.getInstance(applicationContext)

        responseUserLogin = (applicationContext as PlanetShippingApplication).responseUserLogin

        if (intent != null) {

            orderCode = intent.getStringExtra("orderCode")
            spoCode = intent.getStringExtra("spoCode")

        }

        noDataFound = findViewById(R.id.no_data_found)
        parent_of_view = findViewById(R.id.parent_of_top)
        noDataFound!!.visibility = View.GONE

        progressBar = findViewById(R.id.progress_bar)
        scan_button_text = findViewById(R.id.scan_button_text)
        text_as_button = findViewById(R.id.text_as_button)
        mark_button = findViewById(R.id.mark_button)
        feedbackText = findViewById(R.id.feedbackText)
        image_upload = findViewById(R.id.image_upload)
        parent_of_feedback = findViewById(R.id.parent_of_feedback)
        button_of_parent = findViewById(R.id.button_of_parent) //        cameraView = findViewById(R.id.cameraView)

        image_upload!!.setOnClickListener {

            askPermissionsForStorage()

        }

        if (!TextUtils.isEmpty(responseUserLogin.Role) && responseUserLogin.Role!!.equals("Stores", true)) {

            button_of_parent!!.visibility = View.VISIBLE
            text_as_button!!.text = "Mark Shipped"

        } else if (!TextUtils.isEmpty(responseUserLogin.Role) && (responseUserLogin.Role.equals("Fitter", true) || responseUserLogin.Role.equals("Helper"))) {

            button_of_parent!!.visibility = View.VISIBLE
            text_as_button!!.text = "Mark Delivered"

        } else {

            button_of_parent!!.visibility = View.GONE

        }

        feedbackText!!.setOnClickListener {

            var fitters: String? = null
            var CustomerCode: String? = null

            items1.forEach {
                fitters = it.fitters
                if (!TextUtils.isEmpty(fitters)) return@forEach

            }

            items1.forEach {

                CustomerCode = it.CustomerCode
                if (!TextUtils.isEmpty(it.CustomerCode)) return@forEach

            }

            val intent = Intent(this, FeedbackActivity::class.java)
            intent.putExtra("ordCode", orderCode)
            intent.putExtra("empId", responseUserLogin.EmpId)
            intent.putExtra("customerCode", CustomerCode)
            intent.putExtra("fitters", fitters)
            startActivity(intent)

        }

        mark_button!!.setOnClickListener {

            val now: Calendar = Calendar.getInstance()
            val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)
            val month = now.get(Calendar.MONTH)
            val year = now.get(Calendar.YEAR)

            var hashmap: HashMap<String, String>?

            if (responseUserLogin.Role.equals("Fitter", true) || responseUserLogin.Role.equals("Helper")) {

                val chekedProduct = isChekedProduct()

                if (!chekedProduct) {

                    MyUtils.createToast(this, "Select Product For Delivered")

                    return@setOnClickListener
                }

                val arrayList = nestedProductAndPartsAdapter.getSerialId()

                if (arrayList.isEmpty()) {

                    MyUtils.createToast(this, "Select A Product")
                    return@setOnClickListener

                }

                val confirmationDialogFragment = ConfirmationDialogFragment()
                confirmationDialogFragment.show(supportFragmentManager, "confirmationDialogFragment")

                confirmationDialogFragment.setOnClickListener(object : ConfirmationDialogFragment.OnClickListener {
                    override fun onClick() {

                        val join = TextUtils.join(",", arrayList)

                        MyUtils.clearAllMap()
                        MyUtils.setHashmap("SerialIds", join)
                        MyUtils.setHashmap("DeliveryStatus", "Delivered")
                        hashmap = MyUtils.setHashmap("DeliveryDate", "${year}-${month + 1}-${dayOfMonth}")

                        fitter = true;

                        sendDocumentOnServer(hashmap)
                    }

                })

            } else if (responseUserLogin.Role.equals("Stores")) {

                val chekedProduct = isChekedProduct() // checked a product all subproduct chcked or not

                if (!chekedProduct) {

                    MyUtils.createToast(this, "Select Product For Shipped")

                    return@setOnClickListener
                }

                val arrayList1 = nestedProductAndPartsAdapter.getSerialId()

                if (arrayList1.isEmpty()) {

                    MyUtils.createToast(this, "Select A Product")
                    return@setOnClickListener

                }

                val join = TextUtils.join(",", arrayList1)
                MyUtils.clearAllMap()
                MyUtils.setHashmap("SerialIds", join)
                MyUtils.setHashmap("ShipStatus", "Shipped")
                hashmap = MyUtils.setHashmap("ShipDate", "${year}-${month + 1}-${dayOfMonth}")

                Log.i("TAG", "onCreate: ${year}-${month + 1}-${dayOfMonth}")

                fitter = false;

                sendDocumentOnServer(hashmap)

            }

        }

        refreshButton = findViewById(R.id.refresh_button)

        showModel = ArrayList()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        nestedProductAndPartsAdapter = NestedProductAndPartsAdapter(this, showModel)
        recyclerView.adapter = nestedProductAndPartsAdapter

        nestedProductAndPartsAdapter.setOnClickListener(object : NestedProductAndPartsAdapter.OnClickListener {

            override fun onClick(isChecked: Boolean, position: Int) {

                if (isChecked) {
                    recyclerView.smoothScrollToPosition(position)
                }

            }

            override fun onHideDialog() {

                Handler(Looper.myLooper()!!).postDelayed({

                    progressBarDialogFragment?.dismiss()
                    progressBarDialogFragment = null

                }, 1000)

            }

            override fun onShowDialog() {

                if (progressBarDialogFragment == null) {

                    progressBarDialogFragment = ProgressBarDialogFragment()
                    progressBarDialogFragment?.isCancelable = false
                    val bundle = Bundle()
                    bundle.putString("keywords", "Wait a second...")
                    progressBarDialogFragment?.arguments = bundle
                    progressBarDialogFragment?.show(supportFragmentManager, "progressBarDialogFragment")

                }

            }

        })

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

        if (!checkPermission()) askPermissions()
        else openQrCode()

        //order dispatch observer  //dispatchOrdersByParts
        orderDispatchByPartsObserver()

    }

    private val processor = object : Detector.Processor<Barcode> {
        override fun release() {
        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
            if (detections != null && detections.detectedItems.isNotEmpty()) {
                val barcode = detections?.detectedItems
                if (barcode?.size() ?: 0 > 0) { // show barcode content value
                    Toast.makeText(this@QrCodeWithProductActivity, barcode?.valueAt(0)?.displayValue ?: "", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isChekedProduct(): Boolean {
        val checkedArrayList = nestedProductAndPartsAdapter.getCheckedArrayList()

        if (checkedArrayList.isEmpty()) return false

        checkedArrayList.forEach {
            if (!it.isProduct || !it.isSubProduct) {
                return false
            }
        }

        return true
    }

    private fun sendDocumentOnServer(hashmap: HashMap<String, String>?) {

        viewModel.sendDocumentOnServer(hashmap!!)

    }

    private fun askPermissionsForStorage() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        val rationale = "Please provide Storage And Camera permission so that you can Upload Images"
        val options = Permissions.Options().setRationaleDialogTitle("Info").setSettingsDialogTitle("Warning")
        Permissions.check(this /*context*/, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                openStorage()
            }

            override fun onDenied(context: Context, deniedPermissions: java.util.ArrayList<String>) { // permission denied, block the feature.
            }
        })
    }

    private fun openStorage() {
        Matisse.from(this).choose(MimeType.ofImage(), true).countable(true).maxSelectable(10).capture(true).captureStrategy(CaptureStrategy(true, BuildConfig.APPLICATION_ID + ".provider")).gridExpectedSize(resources.getDimensionPixelSize(R.dimen._100ssp)).restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).thumbnailScale(0.85f).imageEngine(GlideEngine()).showSingleMediaType(true).originalEnable(true).maxOriginalSize(10).autoHideToolbarOnSingleTap(true).forResult(REQUEST_CODE_CHOOSE)
    }

    private fun compressedImage(storagelist: ArrayList<String>) {

        val files2 = ArrayList<File>()
        progressBar!!.visibility = View.VISIBLE

        Thread {

            for (i in storagelist.indices) {
                try {
                    val compressedCover = Compressor(this).setMaxWidth(800).setMaxHeight(800).setQuality(85).setCompressFormat(Bitmap.CompressFormat.JPEG).setDestinationDirectoryPath(cacheDir.absolutePath).compressToFile(File(storagelist[i]))
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

        progressBar!!.visibility = View.VISIBLE

        val uploadNotificationConfig = UploadNotificationConfig(notificationChannelId = UploadServiceConfig.defaultNotificationChannel!!, isRingToneEnabled = false, progress = UploadNotificationStatusConfig(title = "${Placeholder.Progress} Percent", message = "${Placeholder.TotalFiles} Files", clearOnAction = true, autoClear = true

        ), success = UploadNotificationStatusConfig(title = "success", message = "some success message", clearOnAction = true, autoClear = true), error = UploadNotificationStatusConfig(title = "error", message = "Something Went Wrong", iconResourceID = R.drawable.ic_logo_brown), cancelled = UploadNotificationStatusConfig(title = "cancelled", message = "some cancelled message", clearOnAction = true, autoClear = true))

        val multipartUploadRequest: MultipartUploadRequest = MultipartUploadRequest(this, RetrofitClient.MainServer + "AOM").setBasicAuth("planet", "planet@#ioi$&*Tqhodktsnifk").addHeader("Content-Type", "application/json").addHeader("Accept", "application/json").addHeader("X-API-KEY", "cw00ggcsw4co0g804gcggwo088g4kokgk88sso4s").addParameter("OrdCode", orderCode!!).addParameter("EmpId", responseUserLogin.EmpId!!).setAutoDeleteFilesAfterSuccessfulUpload(false).setUsesFixedLengthStreamingMode(true).setNotificationConfig { context, uploadId -> uploadNotificationConfig }.setMaxRetries(5).setMethod("POST")
        try {
            var listing_logo: String

            for (i in files) {
                listing_logo = UUID.randomUUID().toString().replace("-".toRegex(), "")
                multipartUploadRequest.addFileToUpload(i.path, "Image[]", "$listing_logo.jpg", "UTF-8")
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

                        MyUtils.createToast(this@QrCodeWithProductActivity, exception.serverResponse.bodyString)
                        Log.e("RECEIVER", "Error, upload error: ${exception.serverResponse}")
                    }

                    else -> {
                        Log.e("RECEIVER", "Error: $uploadInfo", exception)
                    }
                }

            }

            override fun onProgress(context: Context, uploadInfo: UploadInfo) {


            }

            override fun onSuccess(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse) {

                progressBar!!.visibility = View.GONE

                Log.e("RECEIVER", "Error: " + serverResponse.bodyString)

                MyUtils.createToast(this@QrCodeWithProductActivity, "Image Upload Successfully")

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
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitClient.apiInterface)).get(OrdersViewModel::class.java)

        refreshButton!!.setOnClickListener {

            setupObservers()

        }


        setUpDocumentObserver()


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

    private fun setUpDocumentObserver() {

        viewModel.documentOnServer.observe(this, {

            if (fitter && it.success.toString().equals("success", true)) { // after success open signature pad

                val intent = Intent(this, SignaturePadActivity::class.java)
                intent.putExtra("ordCode", orderCode)
                isMarkedClicked = true
                setupObservers()
                startActivity(intent)

            } else if (it.success.toString().equals("success", true)) {

                isMarkedClicked = true
                setupObservers()

            }

            MyUtils.createToast(applicationContext, it.message)

            progressBar!!.visibility = View.GONE

        })

    }

    private fun askPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
        )
        val rationale = "Please provide Camera permission so that you can Scan QrCode"
        val options = Permissions.Options().setRationaleDialogTitle("Info").setSettingsDialogTitle("Warning")
        Permissions.check(this /*context*/, permissions, rationale, options, object : PermissionHandler() {
            override fun onGranted() {
                openQrCode()
            }

            override fun onDenied(context: Context, deniedPermissions: java.util.ArrayList<String>) { // permission denied, block the feature.
            }
        })
    }

    //    private fun openQrCode() {
    //
    //        holder = cameraView!!.holder
    //        barcode = BarcodeDetector.Builder(this)
    //            .setBarcodeFormats(Barcode.CODE_128)
    //            .build()
    //        if (!barcode!!.isOperational) {
    //            Toast.makeText(
    //                applicationContext,
    //                "Sorry, Couldn't setup the detector",
    //                Toast.LENGTH_LONG
    //            ).show()
    //        }
    //        cameraSource = CameraSource.Builder(this, barcode)
    //            .setFacing(CameraSource.CAMERA_FACING_BACK)
    //            .setRequestedFps(200f)
    //            .setAutoFocusEnabled(true)
    //            .setRequestedPreviewSize(1924, 100)
    //            .build()
    //        cameraView!!.holder.addCallback(object : SurfaceHolder.Callback {
    //            @SuppressLint("MissingPermission")
    //            override fun surfaceCreated(holder: SurfaceHolder) {
    //                try {
    //                    cameraSource!!.start(cameraView!!.holder)
    //                } catch (e: IOException) {
    //                    e.printStackTrace()
    //                }
    //            }
    //
    //            override fun surfaceChanged(
    //                holder: SurfaceHolder,
    //                format: Int,
    //                width: Int,
    //                height: Int
    //            ) {
    //            }
    //
    //            override fun surfaceDestroyed(holder: SurfaceHolder) {}
    //        })
    //        barcode!!.setProcessor(object : Detector.Processor<Barcode?> {
    //            override fun release() {
    //            }
    //
    //            override fun receiveDetections(detections: Detector.Detections<Barcode?>) {
    //                val barcodes: SparseArray<Barcode?>? = detections.detectedItems
    //                if (barcodes!!.size() > 0) {
    //                    runOnUiThread {
    //                        Toast.makeText(
    //                            this@QrCodeWithProductActivity,
    //                            "Scan result: ${barcodes.valueAt(0)!!.displayValue}",
    //                            Toast.LENGTH_LONG
    //                        ).show()
    //
    //                        parent_of_lottie!!.visibility = View.VISIBLE
    //                        lattie!!.playAnimation()
    //
    //                        distnumber.addAll(nestedProductAndPartsAdapter.getArrayList())
    //                        distnumber.add(barcodes.valueAt(0)!!.displayValue)
    //                        nestedProductAndPartsAdapter.setProductId(distnumber)
    //                        nestedProductAndPartsAdapter.setScanningStart(
    //                            true,
    //                            barcodes.valueAt(0)!!.displayValue
    //                        );
    //                        nestedProductAndPartsAdapter.notifyDataSetChanged()
    //
    //                        val mp: MediaPlayer =
    //                            MediaPlayer.create(applicationContext, R.raw.sucess_sound)
    //
    //                        mp.start()
    //
    //                    }
    //                }
    //            }
    //
    //        })
    //
    //    }

    private val surfaceCallBack = object : SurfaceHolder.Callback {

        override fun surfaceCreated(holder: SurfaceHolder) {
            if (ActivityCompat.checkSelfPermission(this@QrCodeWithProductActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) { // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            cameraSource.start(holder)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            cameraSource.stop()
        }

    }

    private fun openQrCode() {

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        scanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        scanner!!.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        scanner!!.formats = CodeScanner.TWO_DIMENSIONAL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        scanner!!.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        scanner!!.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        scanner!!.isAutoFocusEnabled = false // Whether to enable auto focus or not
        scanner!!.isFlashEnabled = false // Whether to enable flash or not
        scanner!!.isTouchFocusEnabled = true

        scanner!!.startPreview()

        // Callbacks
        scanner!!.decodeCallback = DecodeCallback {

            if (nestedProductAndPartsAdapter == null || nestedProductAndPartsAdapter.getArrayList() == null) return@DecodeCallback

            runOnUiThread {

                val serialNumberIsPresentOrNOT: Boolean = serialNumberIsPreseNTOrNOT(showModel, it.text)

                if (!serialNumberIsPresentOrNOT) {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Alert!")
                    builder.setMessage("This product is not available in this list.")

                    builder.setPositiveButton(Html.fromHtml("<b>OK<b>")) { _, _ ->
                        scanner!!.startPreview()
                    }

                    builder.show()

                    return@runOnUiThread
                }

                val filter = nestedProductAndPartsAdapter.getArrayList().filter { scanning ->
                    scanning.equals(it.text, true)
                }

                if (filter.isNotEmpty()) {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Alert!")
                    builder.setMessage("This is already scanned product.")

                    builder.setPositiveButton(Html.fromHtml("<b>OK<b>")) { _, _ ->
                        scanner!!.startPreview()
                    }

                    builder.show()


                    return@runOnUiThread

                }

                scan_button_text!!.text = "Next Scan"
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                parent_of_lottie!!.visibility = View.VISIBLE
                lattie!!.playAnimation()

                progressBarDialogFragment = ProgressBarDialogFragment()
                progressBarDialogFragment?.isCancelable = false
                val bundle = Bundle()
                bundle.putString("keywords", "Wait a second...")
                progressBarDialogFragment?.arguments = bundle
                progressBarDialogFragment?.show(supportFragmentManager, "progressBarDialogFragment")

                distnumber.clear()
                distnumber.addAll(nestedProductAndPartsAdapter.getArrayList())
                distnumber.add(it.text)
                nestedProductAndPartsAdapter.setProductId(distnumber)
                nestedProductAndPartsAdapter.setScanningStart(true, it.text);
                nestedProductAndPartsAdapter.notifyDataSetChanged()

                recyclerView.postDelayed({
                    recyclerView.scrollToPosition(0)
                    recyclerView.smoothScrollToPosition(showModel.size - 1)
                }, 500)

                val mp: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.sucess_sound)
                mp.start()

            }
        }
        scanner!!.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun serialNumberIsPreseNTOrNOT(showModel: ArrayList<SerialProductListModel>, text: String): Boolean {

        showModel.forEach {

            if (it.Details != null) {
                it.Details!!.forEach { serialModels ->
                    if (serialModels.SerialNumber.equals(text, true)) {
                        return true
                    }
                }
            }

        }

        return false

    }

    fun onSNACK(view: View, s: String) { //Snackbar(view)
        val snackbar = Snackbar.make(view, "" + s, Snackbar.LENGTH_LONG)
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(Color.BLACK)
        val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        textView.textSize = 12f
        snackbar.show()
    }

    private fun orderDispatchByPartsObserver() {

        viewModel.orderDispatchByParts.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(this.applicationContext, it.message)

                if (it.message.equals("No Data Available")) {
                    noDataFound?.visibility = View.VISIBLE
                } else {
                    noDataFound?.visibility = View.GONE
                }

                progressBar?.visibility = View.GONE

                return@observe

            } else {

                noDataFound!!.visibility = View.GONE

                val items = it.Info

                items1.clear()
                items1.addAll(items!!)

                noDataFound?.visibility = View.GONE

                checkedAllProductIsShippedOrNot(items) // check karo saare product shipped hai stores side se , toh UO api call karni hai open ko

                showOnlyShippedProduct(items) // fitter jiska shippped status shipped hai,wahi sirf show karo

                buttonHideAndShowOfFeedback(items) // button hide and show karna hai, jab product delivered ho jaye , fitters end se, jismai image and feedback dikhani hai

            }

            progressBar!!.visibility = View.GONE

        })

    }

    private fun setupObservers() {

        viewModel.dispatchOrdersByParts(orderCode!!)

    }

    @SuppressLint("SetTextI18n")
    private fun checkedAllProductIsShippedOrNot(items: java.util.ArrayList<SerialProductListModel>?) {

        var i = 0

        items?.forEach {

            if (responseUserLogin.Role.equals("Stores") && it.ShipStatus.equals("Shipped")) {
                i++
            }
        }

        if (responseUserLogin.Role.equals("Stores") && isMarkedClicked) {
            callUpdateUoApi("Shipped")
        }

        if (responseUserLogin.Role.equals("Stores") && items?.size == i) text_as_button?.text = "Already Shipped"

    }

    private fun callUpdateUoApi(s: String) {

        MyUtils.clearAllMap()
        MyUtils.setHashmap("OrdStatus", s)
        MyUtils.setHashmap("Remarks", s)
        val hashmap = MyUtils.setHashmap("OrdCode", orderCode)

        mySharedPreferences.setStringKey(MySharedPreferences.shippedOrDelivered, s)

        viewModel.callUpdateOrdersFromScanPage(hashmap)
        viewModel.updateOrderModel.removeObservers(this)
        viewModel.updateOrderModel.observe(this, {

            MyUtils.createToast(this, it.message)
            progressBar?.visibility = View.GONE
            isMarkedClicked = false

        })

        progressBar?.visibility = View.GONE

    }

    private fun buttonHideAndShowOfFeedback(items: java.util.ArrayList<SerialProductListModel>?) {

        var i = 0

        items!!.forEach {

            if ((responseUserLogin.Role.equals("Fitter", true) || responseUserLogin.Role.equals("Helper")) && it.DeliveryStatus.equals("Delivered")) {
                i++
            }
        }

        if ((responseUserLogin.Role.equals("Fitter", true) || responseUserLogin.Role.equals("Helper")) && items.size == i) {

            mark_button!!.visibility = View.GONE
            parent_of_feedback!!.visibility = View.VISIBLE

        } else if ((responseUserLogin.Role.equals("Fitter", true) || responseUserLogin.Role.equals("Helper"))) {

            mark_button!!.visibility = View.VISIBLE
            parent_of_feedback!!.visibility = View.GONE

        }

        if ((responseUserLogin.Role.equals("Fitter", true) || responseUserLogin.Role.equals("Helper")) && items.size == i && isMarkedClicked) {

            callUpdateUoApi("Delivered")

        }


    }

    private fun showOnlyShippedProduct(items: java.util.ArrayList<SerialProductListModel>?) {

        if ((responseUserLogin.Role.equals("Fitter", true) || responseUserLogin.Role.equals("Helper"))) { // fitter jiska shippped status shipped hai,wahi sirf show karo

            showModel.clear()

            items!!.forEach {

                if (it.ShipStatus!!.equals("Shipped", true)) {

                    showModel.add(it)

                }

            }

        } else {

            showModel.clear()
            showModel.addAll(items!!)

        }

        nestedProductAndPartsAdapter.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()

        setupObservers()

        if (checkPermission()) scanner!!.startPreview()
        else askPermissions()

    }

    override fun onPause() {
        super.onPause()
        if (checkPermission()) scanner!!.releaseResources()

    }


    private fun checkPermission(): Boolean {

        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    }

}