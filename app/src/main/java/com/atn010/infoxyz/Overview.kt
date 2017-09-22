package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import java.util.*


class Overview : AppCompatActivity() {


    //var username: String = intent.getStringExtra("usernameKey")
    //var username = "username"
    val data = ConnectionLogic();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        val listview = findViewById<ListView>(R.id.transferList);


        //Thread.sleep(2000)

        if (Data.listTransfer.isEmpty()){
            data.transactionRequest()
            //data.listTransfer.add("Tanggal-Nama-Rp. Jumlah")
        }else {

            var displayList = java.util.ArrayList<String>(Data.listTransfer)
            Collections.reverse(displayList)

            val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList);

            listview.adapter = mAdapter;
        }

    }

    fun onRefresh(view: View){
        val listview = findViewById<ListView>(R.id.transferList);

        if (Data.listTransfer.isEmpty()){
            data.transactionRequest()
            //12/11/15 12:55-Rob-Rp. 50000|12/11/15 13:00-Jane-Rp. 20000
            //data.listTransfer.add("Tanggal-Nama-Rp. Jumlah")

            }else {
            var displayList = java.util.ArrayList<String>(Data.listTransfer)
            Collections.reverse(displayList)

            val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList);

            listview.adapter = mAdapter;
        }
    }

    fun onProfile (view:View){
        var intent = Intent(this,Profile::class.java);
        //intent.putExtra("usernameKey",username)
        startActivity(intent);
    }

    fun onTransfer(view: View){

        if(Data.transferFlag == false) {
            var intent = Intent(this, Transfer::class.java);
            //intent.putExtra("usernameKey",username)
            startActivity(intent);
        }
        if(Data.transferFlag == true){
            var transferPending = Toast.makeText(this, "Transfer Pending, Please Wait", Toast.LENGTH_SHORT);
            transferPending.show();
        }
    }


}
