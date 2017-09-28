package com.atn010.infoxyz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast

class Login : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun signIn(view: View) {

        var usernameText = findViewById<View>(R.id.Username) as EditText
        var passwordText = findViewById<View>(R.id.Password) as EditText

        var username = usernameText.text.toString();
        var password = passwordText.text.toString();

        var data = Data;



        if (username.length > 4 && password.length > 4) {

            //Thread.sleep(2000)
            data.clientID = username
            var conLogic = ConnectionLogic();
            conLogic.verificationRequest(username, password)

            var timeOut = 0;
            while (timeOut < 30) {
                if (data.verificationStatus == 0) {
                    Thread.sleep(1000)
                } else if (data.verificationStatus == 2) {

                    //Thread.sleep(2000)
                    var intent = Intent(this, Overview::class.java);
                    //intent.putExtra("usernameKey",username)

                    data.clientID = username
                    //Data.clientID=username

                    startActivity(intent);
                } else if (data.verificationStatus == 1) {
                    var myAttempt = Toast.makeText(this, "Invalid username/password", Toast.LENGTH_SHORT);
                    myAttempt.show();
                    conLogic.Client.close()
                    conLogic.Client.disconnect()
                }
                timeOut++;
            }
            if(timeOut>= 30){
                var myTimeout = Toast.makeText(this, "Connection attempt Timeout due to No Connection to Server", Toast.LENGTH_SHORT);
                myTimeout.show();
            }


        } else {
            var mySignin = Toast.makeText(this, "Invalid username/password length", Toast.LENGTH_SHORT);
            mySignin.show();


        }


    }
}
