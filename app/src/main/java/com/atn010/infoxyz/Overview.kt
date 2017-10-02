package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import java.util.*


class Overview : AppCompatActivity() {

    val conLogic = ConnectionLogic()
    var pressedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        val listview = findViewById<ListView>(R.id.transferList)
        var MoneyCurrently = findViewById<TextView>(R.id.MoneyCurrently)

        if (Data.listTransfer.isEmpty()) {

            //request data
            conLogic.transactionRequest()

        } else {

            //load from database
            var displayList = java.util.ArrayList(Data.listTransfer)
            Collections.reverse(displayList)

            val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)

            listview.adapter = mAdapter

            var displayAmount = Data.moneyAmount
            MoneyCurrently.text = displayAmount.toString()
        }

    }

    fun onRefresh(view: View) {
        val listview = findViewById<ListView>(R.id.transferList)
        var MoneyCurrently = findViewById<TextView>(R.id.MoneyCurrently)

        if (Data.listTransfer.isEmpty() || pressedCount == 3) {
            //refresh is empty or forced
            pressedCount = 0
            conLogic.transactionRequest()

        } else {
            //refresh from database

            pressedCount++
            var displayList = java.util.ArrayList(Data.listTransfer)
            Collections.reverse(displayList)

            val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)

            listview.adapter = mAdapter

            var displayAmount = Data.moneyAmount
            MoneyCurrently.text = displayAmount.toString()
        }
    }

    fun onProfile(view: View) {
        var intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    fun onTransfer(view: View) {
        //check if transfer is available

        //available
        if (Data.transferFlag == false) {
            var intent = Intent(this, Transfer::class.java)
            startActivity(intent)
        }

        //not available
        if (Data.transferFlag == true) {
            var transferPending = Toast.makeText(this, "Transfer Pending, Please Wait", Toast.LENGTH_SHORT)
            transferPending.show()
        }
    }


}
