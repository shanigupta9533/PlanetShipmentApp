package com.virtual_market.planetshipmentapp.Activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.virtual_market.planetshipmentapp.Adapter.InstallationListModel
import com.virtual_market.planetshipmentapp.Adapter.InstallationsAdapter
import com.virtual_market.planetshipmentapp.BuildConfig
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.MyUils.GridSpacingItemDecoration
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.ActivityInstallationImagesBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_installation_images.*
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

class InstallationImagesActivity : AppCompatActivity() {

    private val REQUEST_CODE_CHOOSE=1010
    private var delivery_status: String?=null
    private var ordCode: String?=null
    private lateinit var activity:ActivityInstallationImagesBinding
    private lateinit var responseUserLogin: ResponseUserLogin
    private lateinit var installationsAdapter: InstallationsAdapter
    private var viewModel: OrdersViewModel? = null
    private lateinit var progressBar: RelativeLayout
    private var mySharedPreferences: MySharedPreferences? = null
    private lateinit var noDataFound: RelativeLayout
    private lateinit var refreshButton: RelativeLayout
    private lateinit var showModel: ArrayList<InstallationListModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=DataBindingUtil.setContentView(this,R.layout.activity_installation_images)

        mySharedPreferences = MySharedPreferences.getInstance(applicationContext)

        ordCode = intent.getStringExtra("ordCode")
        delivery_status = intent.getStringExtra("delivery_status")

        responseUserLogin =
            (application as PlanetShippingApplication).responseUserLogin

        activity.backButton.setOnClickListener {

            onBackPressed()

        }

        if(!TextUtils.isEmpty(delivery_status) && delivery_status.equals("Delivered") && (responseUserLogin.Role.equals("Fitter", true) || responseUserLogin.Role.equals("Helper"))){

            activity.imagesUpload.visibility=View.VISIBLE

        } else {

            activity.imagesUpload.visibility=View.GONE

        }

        noDataFound = findViewById(R.id.no_data_found)
        noDataFound.visibility = View.GONE

        progressBar = findViewById(R.id.progress_bar)

        val refreshButtonNoData = findViewById<RelativeLayout>(R.id.refresh_button_no_data)

        refreshButtonNoData.setOnClickListener {

            noDataFound.visibility = View.GONE

            no_internet_connection.visibility = View.GONE

            setupObservers()

        }

        refreshButton = findViewById(R.id.refresh_button)

        refreshButton.setOnClickListener {

            no_internet_connection.visibility = View.GONE

            setupObservers()

        }

        showModel = ArrayList()

        activity.pullToRefresh.setOnRefreshListener {

            setupObservers()

            Handler(Looper.myLooper()!!).postDelayed({

                activity.pullToRefresh.setRefreshing(false)

            }, 2500)

        }

        activity.imagesUpload.setOnClickListener {

             openStorage()

        }

        val gridLayoutManager = GridLayoutManager(this, 3)

        val spanCount = 1 // colums
        val spacing = 5 // spaces
        val includeEdge = true
        activity.recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )

        activity.recyclerView.layoutManager = gridLayoutManager
        installationsAdapter = InstallationsAdapter(this, showModel)
        activity.recyclerView.adapter = installationsAdapter

        installationsAdapter.setOnClickListener(object : InstallationsAdapter.OnClickListener{

            override fun onClick(image: String?, imageId: String?) {

               val intent=Intent(this@InstallationImagesActivity,ImagesBigActivity::class.java)
                intent.putExtra("image",image)
                intent.putExtra("image_id",imageId)
               startActivity(intent)

            }

        })

        setupViewModel()

    }

    override fun onResume() {
        super.onResume()

        if (mySharedPreferences!!.getBooleanKey(MySharedPreferences.isUpdateImages)) {

            setupObservers()

            mySharedPreferences!!.setBooleanKey(MySharedPreferences.isUpdateImages, false)


        }

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
        progressBar.visibility = View.VISIBLE

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
                progressBar.visibility = View.GONE

            }
        }.start()
    }

    private fun uploadFileOnServer(files: ArrayList<File>) {

        val uploadNotificationConfig = UploadNotificationConfig(
            notificationChannelId = UploadServiceConfig.defaultNotificationChannel!!,
            isRingToneEnabled = false,
            progress = UploadNotificationStatusConfig(
                title = "${Placeholder.Progress} Percent",
                message = "${Placeholder.TotalFiles} Files",
                clearOnAction = true,
                autoClear = true

            ),
            success = UploadNotificationStatusConfig(
                title = "success",
                message = "some success message",
                clearOnAction = true,
                autoClear = true
            ),
            error = UploadNotificationStatusConfig(
                title = "error",
                message = "Something Went Wrong",
                iconResourceID = R.drawable.ic_logo_brown
            ),
            cancelled = UploadNotificationStatusConfig(
                title = "cancelled",
                message = "some cancelled message",
                clearOnAction = true,
                autoClear = true
            )
        )

        val multipartUploadRequest: MultipartUploadRequest =
            MultipartUploadRequest(
                this, RetrofitClient.MainServer + "AOM"
            )
                .setBasicAuth("planet", "planet@#ioi$&*Tqhodktsnifk")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("X-API-KEY", "cw00ggcsw4co0g804gcggwo088g4kokgk88sso4s")
                .addParameter("OrdCode", ordCode!!)
                .addParameter("EmpId", responseUserLogin.EmpId!!)
                .setAutoDeleteFilesAfterSuccessfulUpload(false)
                .setUsesFixedLengthStreamingMode(true)
                .setNotificationConfig { context, uploadId -> uploadNotificationConfig }
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

                progressBar.visibility = View.GONE

                when (exception) {
                    is UserCancelledUploadException -> {
                        Log.e("RECEIVER", "Error, user cancelled upload: $uploadInfo")
                    }

                    is UploadError -> {

                        MyUtils.createToast(
                            this@InstallationImagesActivity,
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

                progressBar.visibility = View.GONE

                MyUtils.createToast(
                    this@InstallationImagesActivity,
                    "Image Upload Successfully"
                )

                setupObservers()

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

        setupObservers()

        viewModel!!.noInternet.observe(this, {
            if (it) {
                no_internet_connection.visibility = View.VISIBLE
                MyUtils.createToast(this, "No Internet Connection!")
            } else {

                no_internet_connection.visibility = View.GONE

            }
        })

        viewModel!!.errorMessage.observe(this, {

            MyUtils.createToast(this.applicationContext, it)

            no_internet_connection.visibility = View.VISIBLE

        })

        viewModel!!.loading.observe(this, {
            if (it) {
                progressBar.visibility = View.VISIBLE
                noDataFound.visibility = View.GONE
                no_internet_connection.visibility = View.GONE
            } else progressBar.visibility = View.GONE
        })

    }

    private fun setupObservers() {

        viewModel!!.getInstallationImages(ordCode!!)

        viewModel!!.installationImages.removeObservers(this)

        viewModel!!.installationImages.observe(this, {

            if (it.success.equals("failure")) {

                MyUtils.createToast(this.applicationContext, it.message)

                if (it.message.equals("No Data Available")) {

                    noDataFound.visibility = View.VISIBLE

                } else {

                    noDataFound.visibility = View.GONE

                }

                return@observe

            } else {

                noDataFound.visibility = View.GONE
                activity.pullToRefresh.setRefreshing(false)
                val images = it.Images

                showModel.clear()
                showModel.addAll(images!!)

                installationsAdapter.notifyDataSetChanged()


            }

        })

    }

}