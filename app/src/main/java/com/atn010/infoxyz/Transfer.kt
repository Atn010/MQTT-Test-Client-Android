package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast

class Transfer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
    }

    fun onOverview(view: View) {
        var intent = Intent(this, Overview::class.java)
        startActivity(intent)
    }

    fun onProfile(view: View) {
        var intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    fun onSubmit(view: View) {
        var recipientText = findViewById<View>(R.id.Recipient) as EditText
        var amountText = findViewById<View>(R.id.Amount) as EditText

        var recipient = recipientText.text.toString()
        var amount = amountText.text.toString()

        if (recipient.isNotEmpty() && amount.isNotEmpty()) {
            var conLogic = ConnectionLogic()

            conLogic.transferRequest(recipient, amount.toLong())

            var transfering = Toast.makeText(this, "Transfer Request Sent", Toast.LENGTH_SHORT)
            transfering.show()

            var intent = Intent(this, Overview::class.java)

            startActivity(intent)
        }

    }
}
