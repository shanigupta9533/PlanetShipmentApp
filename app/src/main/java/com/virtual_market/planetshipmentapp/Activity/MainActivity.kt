@file:Suppress("DEPRECATION")

package com.virtual_market.planetshipmentapp.Activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.virtual_market.planetshipmentapp.Fragment.HomeFragment
import com.virtual_market.planetshipmentapp.Fragment.ListedProductFragment
import com.virtual_market.planetshipmentapp.Fragment.NewProduct
import com.virtual_market.planetshipmentapp.Fragment.SettingFragment
import com.virtual_market.planetshipmentapp.MyUils.MySharedPreferences
import com.virtual_market.planetshipmentapp.R
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var pd: ProgressDialog?=null
    private var mySharedPreferences: MySharedPreferences?=null
    private var itemIndexGlobal: Int=0
    private val REQUEST_ENABLE_GPS = 516
    private var spaceNavigationView: SpaceNavigationView? = null
    private var tags: String? = null
    private var Mylongitude = 0.0
    private val REQUEST_CHECK_SETTINGS = 214
    private var Mylatitude = 0.0
    private var mLocationCallBack: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationClient: FusedLocationProviderClient? = null
    private val address = arrayOf("home", "setting")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spaceNavigationView = findViewById(R.id.bottom_space)
        spaceNavigationView!!.showIconOnly()
        spaceNavigationView!!.initWithSaveInstanceState(savedInstanceState)
        spaceNavigationView!!.addSpaceItem(SpaceItem("", R.drawable.ic_product_icon))
        spaceNavigationView!!.addSpaceItem(SpaceItem("", R.drawable.ic_settings_icon))

         mySharedPreferences = MySharedPreferences.getInstance(applicationContext)
        mLocationCallBack = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult.lastLocation
                Mylatitude = lastLocation.latitude
                Mylongitude = lastLocation.longitude

                if(Mylatitude>0 && Mylongitude>0) {

                    mySharedPreferences!!.setStringKey(
                        MySharedPreferences.myLatitude,
                        Mylatitude.toString()
                    )
                    mySharedPreferences!!.setStringKey(
                        MySharedPreferences.myLongitude,
                        Mylongitude.toString()
                    )

                    if (pd != null)
                        pd!!.dismiss()

                    mLocationClient!!.removeLocationUpdates(mLocationCallBack!!)

                }

            }
        }


        mLocationClient = LocationServices.getFusedLocationProviderClient(this)

        supportFragmentManager.beginTransaction().add(R.id.frameLayout, HomeFragment(), "home").commit()

        spaceNavigationView!!.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {

                startActivity(Intent(applicationContext, AllProductActivity::class.java))

            }

            override fun onItemClick(itemIndex: Int, itemName: String?) {

                loadFragment(itemIndex)
                itemIndexGlobal = itemIndex


            }


            override fun onItemReselected(itemIndex: Int, itemName: String?) {

                loadFragment(itemIndex)
                itemIndexGlobal = itemIndex

            }
        })

//        getCurrentLocation()

    }

    private fun loadFragment(itemIndex: Int){

        var selectedFragment: Fragment? = null;
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        when (itemIndex) {

            0 -> {

                tags = "home"

                if (fm.findFragmentByTag(tags) != null) {
                    hideFragment()
                    Objects.requireNonNull(
                        supportFragmentManager.findFragmentByTag(tags)
                    )?.let {
                        ft.show(it).commit()
                    }
                }
                selectedFragment = HomeFragment()

            }
            1 -> {

                tags = "setting"

                if (fm.findFragmentByTag(tags) != null) {
                    hideFragment()
                    Objects.requireNonNull(
                        supportFragmentManager.findFragmentByTag(tags)
                    )?.let {
                        ft.show(it).commit()
                    }
                }
                selectedFragment = SettingFragment()

            }

        }

        if (selectedFragment != null && !TextUtils.isEmpty(tags) && supportFragmentManager.findFragmentByTag(
                tags
            ) == null) {
            hideFragment()
            ft.add(R.id.frameLayout, selectedFragment, tags).addToBackStack(null).commit()
        }

    }

    override fun onBackPressed() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount

        if (backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            spaceNavigationView!!.changeCurrentItem(0)
            val fm: FragmentManager = supportFragmentManager
            for (i in 0 until fm.backStackEntryCount) {
                fm.popBackStack()
            }
        }
    }

    private fun showFragment() {
        if (supportFragmentManager.findFragmentById(R.id.frameLayout) != null) Objects.requireNonNull(
            supportFragmentManager.findFragmentById(
                R.id.frameLayout
            )
        )?.let {
            supportFragmentManager.beginTransaction().show(it).commit()
        }
    }

    private fun hideFragment() {
        for (addres in address) {
            if (supportFragmentManager.findFragmentByTag(addres) != null) Objects.requireNonNull(
                supportFragmentManager.findFragmentByTag(
                    addres
                )
            )?.let {
                supportFragmentManager.beginTransaction().hide(it).commit()
            }

        }
    }

    private fun openDialogOfSettingGps() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        builder.setAlwaysShow(true)
        mLocationSettingsRequest = builder.build()
        mSettingsClient = LocationServices.getSettingsClient(this)
        mSettingsClient?.checkLocationSettings(mLocationSettingsRequest)?.addOnSuccessListener { doLocationWork() }?.addOnFailureListener { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val rae: ResolvableApiException = e as ResolvableApiException
                        rae.startResolutionForResult(
                            this,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sie: SendIntentException) {
                        Log.e("GPS", "Unable to execute request.")
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.e(
                        "GPS",
                        "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                    )
                }
            }?.addOnCanceledListener { Log.e("GPS", "checkLocationSettings -> onCanceled") }
    }


    fun permissionIsGranted(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun openGpsEnableSetting() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, REQUEST_ENABLE_GPS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                RESULT_OK -> doLocationWork()
                RESULT_CANCELED -> {
                    Log.e("GPS", "User denied to access location")
                    openGpsEnableSetting()
                }
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGpsEnabled) {
                openGpsEnableSetting()
            } else {
                doLocationWork()
            }
        }
    }

    private fun getCurrentLocation() {
        if (permissionIsGranted()) {
            openDialogOfSettingGps()
        } else {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            val rationale = "Please provide location permission so that you can ..."
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
                        openDialogOfSettingGps()
                    }

                    override fun onDenied(context: Context, deniedPermissions: ArrayList<String>) {
                        // permission denied, block the feature.
                    }
                })
        }
    }

    private fun doLocationWork() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if (locationRequest == null) {
            locationRequest = LocationRequest.create()
            locationRequest?.interval = 1000 // two minute interval
            locationRequest?.fastestInterval = 1000
            locationRequest?.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
            mLocationClient?.requestLocationUpdates(locationRequest!!, mLocationCallBack!!, null)

            if(TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.myLongitude)) && TextUtils.isEmpty(mySharedPreferences!!.getStringkey(MySharedPreferences.myLatitude))) {

                pd = ProgressDialog(this@MainActivity)
                pd?.setMessage("Fetching Your Location..")
                pd?.setCanceledOnTouchOutside(false)
                pd?.show()

            }

        }
    }
}