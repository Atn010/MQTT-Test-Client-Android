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

/**
 * This class display all the transaction information in a list and manage Request for an updated List
 * @author Antonius George Sunggeriwan
 *
 */
class Overview : AppCompatActivity() {

    val conLogic = ConnectionLogic()
    var pressedCount = 0

    /**
     * OnCreation of this class, load Transaction Data to list if available. Else do a transaction Request
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        val listview = findViewById<ListView>(R.id.transferList)
        val MoneyCurrently = findViewById<TextView>(R.id.MoneyCurrently)

        if (Data.listTransfer.isEmpty()) {

            //request data
            conLogic.transactionRequest()

        } else {

            //load from database
            updateTransactionList(listview, MoneyCurrently)
        }

    }

    /**
     * This Method updates the Transaction List and Amount of Money Information by
     * Replacing the data and overwriting the old list.
     *
     * @param listview The Transaction List
     * @param MoneyCurrently The Amount of Money Displayed
     */
    private fun updateTransactionList(listview: ListView, MoneyCurrently: TextView) {
        val displayList = ArrayList(Data.listTransfer)
        Collections.reverse(displayList)

        val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)

        listview.adapter = mAdapter

        val displayAmount = Data.moneyAmount
        MoneyCurrently.text = displayAmount.toString()
    }

    /**
     * This Method Refresh the list, until 3 times which will send a force update to the Transaction List
     */
    fun onRefresh(view: View) {
        val listview = findViewById<ListView>(R.id.transferList)
        val MoneyCurrently = findViewById<TextView>(R.id.MoneyCurrently)

        if (Data.listTransfer.isEmpty() || pressedCount >= 5) {
            pressedCount = 0
            conLogic.transactionRequest()

        } else {
            pressedCount++
            updateTransactionList(listview, MoneyCurrently)
        }
    }

    /**
     * This class Switch the user to Profile view
     */
    fun onProfile(view: View) {
        var intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    /**
     * This class Switch the user to Transfer view if Transfer Response is received
     */
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
