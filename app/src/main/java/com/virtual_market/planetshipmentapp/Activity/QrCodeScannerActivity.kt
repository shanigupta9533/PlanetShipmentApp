package com.virtual_market.planetshipmentapp.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.budiyev.android.codescanner.*
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.virtual_market.planetshipmentapp.R
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine

class QrCodeScannerActivity : AppCompatActivity() {

    private val REQUEST_CODE_CHOOSE_LOGO: Int=1001
    private var file_chooser: LottieAnimationView?=null
    private var parent_of_lottie: LinearLayout?=null
    private var scan_again: RelativeLayout?=null
    private var lattie: LottieAnimationView?=null
    private var scanner: CodeScanner?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        parent_of_lottie = findViewById<LinearLayout>(R.id.parent_of_lottie)
        scan_again = findViewById(R.id.scan_again)
        lattie = findViewById(R.id.animationView)
        file_chooser = findViewById(R.id.file_chooser)

        file_chooser!!.setOnClickListener {

              askPermissionForQrCode()

        }

        scan_again!!.setOnClickListener {

            if(scanner!=null){

                parent_of_lottie!!.visibility=View.GONE
                scanner!!.startPreview()

            }

        }

        if (!checkPermission())
            askPermissions()
        else
            openQrCode()

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

                parent_of_lottie!!.visibility= View.VISIBLE
                lattie!!.playAnimation()

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

    private fun askPermissionForQrCode() {

        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
        val rationale = "Please provide Storage permission so that you can Upload Images"
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
                    openStorageForLogo()
                }

                override fun onDenied(
                    context: Context,
                    deniedPermissions: java.util.ArrayList<String>
                ) {
                    // permission denied, block the feature.
                }
            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



    }

    private fun openStorageForLogo() {
        Matisse.from(this)
            .choose(MimeType.ofImage(), false)
            .countable(true)
            .maxSelectable(1)
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
            .forResult(REQUEST_CODE_CHOOSE_LOGO)
    }

}