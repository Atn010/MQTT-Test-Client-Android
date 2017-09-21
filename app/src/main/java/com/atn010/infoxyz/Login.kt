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

    fun signIn (view: View){

        var usernameText = findViewById<View>(R.id.Username) as EditText
        var passwordText = findViewById<View>(R.id.Password) as EditText

        var username = usernameText.text.toString();
        var password = passwordText.text.toString();

        if(username.length > 4 && password.length >4){

            //Thread.sleep(2000)

            var intent = Intent(this,Overview::class.java);
            //intent.putExtra("usernameKey",username)

            Data.setClient(username)
            //Data.clientID=username

            startActivity(intent);

        }else{
            var mySignin = Toast.makeText(this, "Invalid username/password", Toast.LENGTH_SHORT);
            mySignin.show();


        }


    }
}
