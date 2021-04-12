package com.virtual_market.planetshipmentapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.github.gcacace.signaturepad.views.SignaturePad
import com.virtual_market.planetshipmentapp.R

class SignaturePadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature_pad)

        val signature_pad = findViewById<SignaturePad>(R.id.signature_pad)
        val clear_text = findViewById<Button>(R.id.clear_text)
        val save = findViewById<Button>(R.id.save)

        clear_text.setOnClickListener {

            signature_pad.clear()

        }

    }
}