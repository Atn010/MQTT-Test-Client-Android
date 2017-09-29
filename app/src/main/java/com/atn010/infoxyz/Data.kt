package com.atn010.infoxyz

import java.util.*

/**
 * Created by atn01 on 09/20/2017.
 */

object Data {
    var clientID = ""
    var moneyAmount: Long = 0
    var listTransfer = ArrayList<String>()
    var transferFlag = false
    var verificationStatus = 0


    fun setClient(username: String) {
        clientID = username
    }


}