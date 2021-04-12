package com.virtual_market.planetshipmentapp.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.virtual_market.planetshipmentapp.R
import com.virtual_market.planetshipmentapp.databinding.ActivityShowClientAddressBinding

class ShowClientAddressActivity : AppCompatActivity() {

    private lateinit var activity: ActivityShowClientAddressBinding

    private var lat:Double=27.20604115714989
    private var aLongitude:Double=82.65132533978193
    private var lat2:Double=26.845904196610437
    private var aLongitude2:Double=80.93580723686009

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity=DataBindingUtil.setContentView(this, R.layout.activity_show_client_address)

        activity.showMapsAddress.setOnClickListener {

            submittedmaps()

        }

        activity.contactUs.setOnClickListener {

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:0123456789")
            startActivity(intent)

        }

        activity.markDeliverd.setOnClickListener {

            startActivity(Intent(this,SignaturePadActivity::class.java))

        }

    }

    fun submittedmaps() {

        val intent = Intent(Intent.ACTION_VIEW)

        val uri =
            "http://maps.google.com/maps?saddr=$lat,$aLongitude&daddr=$lat2,$aLongitude2"

        intent.data = Uri.parse(uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }

    }

}