package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

class Profile : AppCompatActivity() {
    //var username: String = intent.getStringExtra("usernameKey")
    //var username = "username"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }

    fun onOverview(view: View) {
        var intent = Intent(this, Overview::class.java);
        //intent.putExtra("usernameKey",username)
        startActivity(intent);
    }

    fun onTransfer(view: View) {

        if (Data.transferFlag == false) {
            var intent = Intent(this, Transfer::class.java);
            //intent.putExtra("usernameKey",username)
            startActivity(intent);
        }
        if (Data.transferFlag == true) {
            var transferPending = Toast.makeText(this, "Transfer Pending, Please Wait", Toast.LENGTH_SHORT);
            transferPending.show();
        }
    }
}
