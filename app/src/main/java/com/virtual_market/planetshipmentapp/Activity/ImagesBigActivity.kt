package com.virtual_market.planetshipmentapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.virtual_market.planetshipmentapp.R

class ImagesBigActivity : AppCompatActivity() {

    private var imageView: PhotoView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_big)

        val image = intent.getStringExtra("image")

        imageView=findViewById(R.id.imageView)

        Glide.with(this).load(image).placeholder(R.drawable.ic_logo_brown).error(R.drawable.ic_logo_brown).into(imageView!!)

    }
}