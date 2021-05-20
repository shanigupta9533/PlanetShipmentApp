package com.virtual_market.planetshipmentapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.virtual_market.planetshipmentapp.Adapter.HeadingOfQuestionsAdapter
import com.virtual_market.planetshipmentapp.Fragment.SignaturePanelFragment
import com.virtual_market.planetshipmentapp.Modal.FeedbackUserList
import com.virtual_market.planetshipmentapp.Modal.QuestionsModel
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.ViewModel.OrdersViewModel
import com.virtual_market.planetshipmentapp.ViewModel.ViewModelFactory
import com.virtual_market.planetshipmentapp.databinding.ActivityFeedbackBinding
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.activity_feedback.view.*
import kotlinx.android.synthetic.main.activity_qr_code_with_product.*
import kotlinx.android.synthetic.main.feedback_include_six.view.*
import kotlinx.android.synthetic.main.feedback_include_six.view.signature_pad
import kotlinx.android.synthetic.main.fragment_setting.view.*
import kotlinx.android.synthetic.main.fragment_signature_panel.view.*
import kotlinx.android.synthetic.main.no_internet_connection.*
import kotlinx.android.synthetic.main.no_internet_connection.view.*
import kotlinx.android.synthetic.main.transparent_progress_bar_layout.*
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class FeedbackActivity : AppCompatActivity(),DatePickerDialog.OnDateSetListener {

    private var customerCode: String?=null
    private var fitters: String?=null
    private var empId: String?=null
    private var ordCode: String?=null
    private var showModel: ArrayList<QuestionsModel>? = null
    private lateinit var headingOfQuestionsAdapter: HeadingOfQuestionsAdapter
    private lateinit var viewModel: OrdersViewModel
    private lateinit var activity: ActivityFeedbackBinding
    private var signatureBitmapGlobal: Bitmap? = null
    private lateinit var includedLayout: View
    private var noDataFound: RelativeLayout? = null
    private var noInternetConnection: RelativeLayout? = null
    private var refreshButton: RelativeLayout? = null
    private var progressBar: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = DataBindingUtil.setContentView(this, R.layout.activity_feedback)

        noDataFound = findViewById(R.id.no_data_found)
        noDataFound!!.visibility = View.GONE

        if(intent!=null){

            ordCode = intent.getStringExtra("ordCode")
            empId = intent.getStringExtra("empId")
            fitters = intent.getStringExtra("fitters")
            customerCode = intent.getStringExtra("customerCode")

        }

        noInternetConnection = findViewById(R.id.no_internet_connection)

        progressBar = findViewById(R.id.progress_bar)

        refreshButton = findViewById(R.id.refresh_button)

        includedLayout = activity.included
        includedLayout.signature_pad
            .setOnClickListener {

                val signaturePanelFragment = SignaturePanelFragment()
                signaturePanelFragment.isCancelable = false
                signaturePanelFragment.show(supportFragmentManager, "signaturePanelFragment")

                signaturePanelFragment.setOnClickListener(object :
                    SignaturePanelFragment.OnClickListener {
                    override fun onclick(signatureBitmap: Bitmap) {

                        includedLayout.signature_pad
                            .setImageBitmap(signatureBitmap)

                        signatureBitmapGlobal = signatureBitmap

                    }
                })

            }

        showModel = ArrayList()

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        activity.recyclerView.layoutManager = linearLayoutManager
        headingOfQuestionsAdapter = HeadingOfQuestionsAdapter(this, showModel!!)
        activity.recyclerView.adapter = headingOfQuestionsAdapter

        activity.submitFeedback.setOnClickListener {

            askPermissionsForStorage()

        }

        activity.included.date.setOnClickListener {

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

        setupViewModel()

    }

    private fun askPermissionsForStorage() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                    uploadFileOnServer()
                }

                override fun onDenied(
                    context: Context,
                    deniedPermissions: java.util.ArrayList<String>
                ) {
                    // permission denied, block the feature.
                }
            })
    }

    private fun bitmapToFile(bitmap: Bitmap): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "Signature"
            )
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata: ByteArray = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    private fun uploadFileOnServer() {

        progressBar!!.visibility = View.VISIBLE

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
                this, RetrofitClient.MainServer + "AFD"
            )
                .setBasicAuth("planet", "planet@#ioi$&*Tqhodktsnifk")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("X-API-KEY", "cw00ggcsw4co0g804gcggwo088g4kokgk88sso4s")
                .addParameter("OrdCode", ordCode!!)
                .addParameter("FiterId", fitters!!)
                .addParameter("CustomerCode",customerCode!!)
                .addParameter("Date",activity.included.date.text.toString())
                .addParameter("OptionIds",headingOfQuestionsAdapter.answerIdModel()!!)
                .addParameter("Feedback",activity.included.customer_feedback.text.toString())
                .addParameter("CustomerName",activity.included.customer_name.text.toString())
                .addParameter("Mobile",activity.included.mobileNumber.text.toString())
                .addParameter("TransporterName",activity.included.transport_name.text.toString())
                .addParameter("VehicleNumber",activity.included.vehicle_number.text.toString())
                .setAutoDeleteFilesAfterSuccessfulUpload(false)
                .setNotificationConfig { context, uploadId -> uploadNotificationConfig }
                .setUsesFixedLengthStreamingMode(true)
                .setMaxRetries(5)
                .setMethod("POST")
        try {
            if (signatureBitmapGlobal != null) {

                val listing_logo: String

                listing_logo = UUID.randomUUID().toString().replace("-".toRegex(), "")
                multipartUploadRequest.addFileToUpload(
                    bitmapToFile( signatureBitmapGlobal!!)!!.path,
                    "Signature",
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
                            applicationContext,
                            exception.serverResponse.bodyString
                        )
                        Log.e("RECEIVER", "Error, upload error: ${exception.serverResponse.bodyString}")
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

                Handler(Looper.myLooper()!!).postDelayed({

                    onBackPressed()

                }, 800)

                MyUtils.createToast(
                    applicationContext,
                    serverResponse.bodyString
                )

            }

        })
        multipartUploadRequest.startUpload()
    }

    private fun setDataOnFeedback(feedbackUserList: FeedbackUserList){

        activity.included.customer_feedback.setText(feedbackUserList.Feedback)
        activity.included.customer_name.setText(feedbackUserList.CustomerName)
        activity.included.mobileNumber.setText(feedbackUserList.Mobile)
        activity.included.transport_name.setText(feedbackUserList.TranspoterName)
        activity.included.vehicle_number.setText(feedbackUserList.VehicleNumber)
        activity.included.feedback_notice_text.visibility=View.VISIBLE
        activity.submitFeedback.visibility=View.GONE
        activity.included.date.text = feedbackUserList.Date

        headingOfQuestionsAdapter.feedbackModels(feedbackUserList)
        headingOfQuestionsAdapter.notifyDataSetChanged()

        Glide.with(this).load(feedbackUserList.Signature).into(activity.included.signature_pad)

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

        viewModel.errorMessage.observe(this, {

            MyUtils.createToast(this.applicationContext, it)
            noInternetConnection!!.visibility = View.VISIBLE

        })

        viewModel.noInternet.observe(this, {
            if (it) {
                noInternetConnection!!.visibility = View.VISIBLE
                MyUtils.createToast(this, "No Internet Connection!")
            } else {
                noInternetConnection!!.visibility = View.VISIBLE
                MyUtils.createToast(this, "No Internet Connection bhai!")
            }
        })

        viewModel.loading.observe(this, {
            if (it) {
                progressBar!!.visibility = View.VISIBLE
                noDataFound!!.visibility = View.GONE
                noInternetConnection!!.visibility = View.GONE
            } else
                progressBar!!.visibility=View.GONE
        })

    }

    @SuppressLint("SetTextI18n")
    private fun setupObservers() {

        viewModel.getQuestions()

        viewModel.allQuestions.removeObservers(this)

        viewModel.allQuestions.observe(this, {

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

                val items = it.Questions
                showModel!!.clear()
                showModel!!.addAll(items!!)

                activity.included.feedbackTextOnInclude.text =
                    "${(showModel!!.size + 1)}.  Customer \n Feedback"

                headingOfQuestionsAdapter.notifyDataSetChanged()

                noDataFound!!.visibility = View.GONE

                getUploadedDataFeedbackOrderCode()

            }

        })

    }

    private fun getUploadedDataFeedbackOrderCode() {

        viewModel.getFeedbackDetails(ordCode!!)

        viewModel.feedbackDetails.removeObservers(this)

        viewModel.feedbackDetails.observe(this, {

            if (it.success.equals("failure")) {

                return@observe

            } else if (it?.Feeedback != null) {

                setDataOnFeedback(it.Feeedback?.get(0)!!)

            }

        })

    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        activity.included.date.text = "$year-${month + 1}-$dayOfMonth"

    }
}