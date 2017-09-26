package com.atn010.infoxyz

import java.util.*

/**
 * Created by atn01 on 09/20/2017.
 */

object List {
    var dateTime: String = ""
    var account: String = ""
    var recipient: String = ""
    var amount: Long = 0
}
object Data{
    var clientID = ""
    var moneyAmount: Long = 0
    var listTransfer = ArrayList<List>();
    var transferFlag = false;
    var verificationStatus = 0;


    public final fun setClient(username: String){
        clientID = username
    }



}