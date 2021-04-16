package com.virtual_market.planetshipmentapp.Activity

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.gcacace.signaturepad.views.SignaturePad
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import com.virtual_market.planetshipmentapp.MyUils.MyUtils
import com.virtual_market.planetshipmentapp.MyUils.PlanetShippingApplication
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.virtualmarket.api.RetrofitClient
import kotlinx.android.synthetic.main.transparent_progress_bar_layout.*
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.exceptions.UploadError
import net.gotev.uploadservice.exceptions.UserCancelledUploadException
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*


@Suppress("DEPRECATION")
class SignaturePadActivity : AppCompatActivity() {
    private var signature_pad: SignaturePad?=null
    private lateinit var responseUserLogin: ResponseUserLogin
    private val REQUEST_CODE_CHOOSE: Int=1001
    private var ordCode: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature_pad)

        ordCode = intent.getStringExtra("ordCode")
        responseUserLogin = (application as PlanetShippingApplication).responseUserLogin

        signature_pad = findViewById<SignaturePad>(R.id.signature_pad)
        val clear_text = findViewById<Button>(R.id.clear_text)
        val save = findViewById<Button>(R.id.save)

        save.setOnClickListener {

            askPermissionsForStorage()

        }

        clear_text.setOnClickListener {

            signature_pad!!.clear()

        }

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

    private fun uploadFileOnServer() {

        progress_bar.visibility=View.VISIBLE

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
                .setMaxRetries(5)
                .setMethod("POST")
        try {
            val listing_logo: String

                listing_logo = UUID.randomUUID().toString().replace("-".toRegex(), "")
                multipartUploadRequest.addFileToUpload(
                    bitmapToFile(this,signature_pad!!.signatureBitmap,"Signature")!!.path,
                    "Image[]",
                    "$listing_logo.jpg",
                    "UTF-8"
                )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        multipartUploadRequest.subscribe(this, this, object : RequestObserverDelegate {
            override fun onCompleted(context: Context, uploadInfo: UploadInfo) {

            }

            override fun onCompletedWhileNotObserving() {
            }

            override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {

                progress_bar!!.visibility = View.GONE

                when (exception) {
                    is UserCancelledUploadException -> {
                        Log.e("RECEIVER", "Error, user cancelled upload: $uploadInfo")
                    }

                    is UploadError -> {

                        MyUtils.createToast(
                            this@SignaturePadActivity,
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

                progress_bar_main!!.visibility = View.GONE

                Log.e("RECEIVER", "Error: " + serverResponse.bodyString)

                MyUtils.createToast(
                    this@SignaturePadActivity,
                    serverResponse.bodyString
                )

            }

        })
        multipartUploadRequest.startUpload()
    }

    fun bitmapToFile(
        context: Context,
        bitmap: Bitmap,
        fileNameToSave: String
    ): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + fileNameToSave
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

}