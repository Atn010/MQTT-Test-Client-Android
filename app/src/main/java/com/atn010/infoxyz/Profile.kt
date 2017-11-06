package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

/**
 * This is an unsed class, planned to display some information, but was cut instead and left barren
 * @author Antonius George Sunggeriwan
 */
class Profile : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
    }
    /**
     * This class Switch the user to Overview view
     */
    fun onOverview(view: View) {
        val intent = Intent(this, Overview::class.java)
        startActivity(intent)

    }
    /**
     * This class Switch the user to Transfer view
     */
    fun onTransfer(view: View) {

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
