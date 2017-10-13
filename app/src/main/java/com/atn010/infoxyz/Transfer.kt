package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast

/**
 * This class manages the Transfer Page which include the function to send a TransferRequest
 * @author Antonius George Sunggeriwan
 */
class Transfer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
    }

    /**
     * This class Switch the user to Overview view
     */
    fun onOverview(view: View) {
        var intent = Intent(this, Overview::class.java)
        startActivity(intent)
    }

    /**
     * This class Switch the user to Profile view
     */
    fun onProfile(view: View) {
        var intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    /**
     * This class extract the relevant information field and perform validation and execute a function
     */
    fun onSubmit(view: View) {
        var recipientText = findViewById<View>(R.id.Recipient) as EditText
        var amountText = findViewById<View>(R.id.Amount) as EditText

        var recipient = recipientText.text.toString()
        var amount = amountText.text.toString()

        if (recipient.isNotEmpty() && amount.isNotEmpty()) {
            submitTransferRequest(recipient, amount)
        }else{
            var error = Toast.makeText(this, "Recipient field and Amount field must not be empty", Toast.LENGTH_SHORT)
            error.show()
        }

    }

    /**
     * This method initiate a common class and send the relevant information to ConnectionLogic.
     * This class also switch the User's View to Overview.
     *
     * @param recipient The id of the Recipient
     * @param amount The amount of money wished to be transfered
     */
    private fun submitTransferRequest(recipient: String, amount: String) {
        var conLogic = ConnectionLogic()

        conLogic.transferRequest(recipient, amount.toLong())

        var transfering = Toast.makeText(this, "Transfer Request Sent", Toast.LENGTH_SHORT)
        transfering.show()

        var intent = Intent(this, Overview::class.java)

        startActivity(intent)
    }
}
