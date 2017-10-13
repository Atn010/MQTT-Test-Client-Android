package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

/**
 * This class manages the user Login attempt
 * @author Antonius George Sunggeriwan
 *
 */
class Login : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button.setOnClickListener {
            signIn()
        }
    }

    /**
     * This class extract the relevant information field and perform validation and execute a function
     */
    fun signIn() {

        val usernameText = findViewById<View>(R.id.Username) as EditText
        val passwordText = findViewById<View>(R.id.Password) as EditText

        val username = usernameText.text.toString()
        val password = passwordText.text.toString()

        val data = Data

        if (username.length > 4 && password.length > 4) {
            verificationRequest(data, username, password)

        } else {
            val mySignin = Toast.makeText(this, "Invalid username/password length", Toast.LENGTH_SHORT)
            mySignin.show()
        }
    }

    /**
     * This class configure the information and send information. Then waits for the response
     *
     * @param data The declared Data class
     * @param username The User's Username
     * @param password The User's Password
     */
    private fun verificationRequest(data: Data, username: String, password: String) {
        data.clientID = username
        val conLogic = ConnectionLogic()
        conLogic.verificationRequest(username, password)

        var timeOut = verificationAttempt(data, username, conLogic)
        if (timeOut >= 30) {
            val myTimeout = Toast.makeText(this, "Connection attempt Timeout due to No Connection to Server", Toast.LENGTH_SHORT)
            myTimeout.show()
        }
    }

    /**
     * This class is waits for 30 secconds while it checks for an appropriate response from the Broker
     *
     * @param data The declared Data class
     * @param username The User's Username
     * @param conLogic The declared ConnectionLogic class
     *
     * @return timeOut This returns the number of secconds passed
     */
    private fun verificationAttempt(data: Data, username: String, conLogic: ConnectionLogic): Int {
        var timeOut = 0
        while (timeOut < 30) {
            if (data.verificationStatus == 0) {
                Thread.sleep(1000)
            } else if (data.verificationStatus == 2) {

                val intent = Intent(this, Overview::class.java)

                data.clientID = username
                startActivity(intent)
            } else if (data.verificationStatus == 1) {
                var myAttempt = Toast.makeText(this, "Invalid username/password", Toast.LENGTH_SHORT)
                myAttempt.show()

                conLogic.Client.disconnect()
            }
            timeOut++
        }
        return timeOut
    }
}
