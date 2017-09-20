package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView


class Overview : AppCompatActivity() {


    //var username: String = intent.getStringExtra("usernameKey")
    var username = "username"
    val data = Data(username);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        val listview = findViewById<ListView>(R.id.transferList);




        //Thread.sleep(2000)

        if (data.listTransfer.isEmpty()){
            data.transactionRequest()
            //data.listTransfer.add("Tanggal-Nama-Rp. Jumlah")
        }else {

            val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data.listTransfer);
            listview.adapter = mAdapter;
        }

    }

    fun onRefresh(view: View){
        val listview = findViewById<ListView>(R.id.transferList);

        if (data.listTransfer.isEmpty()){
            data.transactionRequest()
            //data.listTransfer.add("Tanggal-Nama-Rp. Jumlah")
            //data.listTransfer.add("Tanggal-Nama-Rp. Jumlah")

            }else {

            val mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data.listTransfer);
            listview.adapter = mAdapter;
        }
    }

    fun onProfile (view:View){
        var intent = Intent(this,Profile::class.java);
        //intent.putExtra("usernameKey",username)
        startActivity(intent);
    }

    fun onTransfer(view: View){
        var intent = Intent(this,Transfer::class.java);
        //intent.putExtra("usernameKey",username)
        startActivity(intent);
    }


}
