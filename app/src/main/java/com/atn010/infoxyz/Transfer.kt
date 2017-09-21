package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText

class Transfer : AppCompatActivity() {
    //var username: String = intent.getStringExtra("usernameKey")
    //var username = "username"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
    }

    fun onOverview (view: View){
        var intent = Intent(this,Profile::class.java);
        //intent.putExtra("usernameKey",username)
        startActivity(intent);
    }

    fun onSubmit(view: View){
        var recipientText = findViewById<View>(R.id.Recipient) as EditText
        var amountText = findViewById<View>(R.id.Amount) as EditText

        var recipient = recipientText.text.toString();
        var amount = amountText.text.toString().toDouble()

        var conLogic = ConnectionLogic();

        conLogic.transferRequest(recipient,amount)

    }
}
