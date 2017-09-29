package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class Profile : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun onOverview() {
        val intent = Intent(this, Overview::class.java)
        startActivity(intent)
    }

    fun onTransfer() {

        if (Data.transferFlag == false) {
            val intent = Intent(this, Transfer::class.java)

            startActivity(intent)
        }
        if (Data.transferFlag == true) {
            val transferPending = Toast.makeText(this, "Transfer Pending, Please Wait", Toast.LENGTH_SHORT)
            transferPending.show()
        }
    }
}
