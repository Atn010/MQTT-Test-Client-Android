package com.atn010.infoxyz

import java.util.*

/**
 * This class store the data used for the lifetime of the application.
 * @author Antonius George Sunggeriwan <atn010g@gmail.com>
 *
 */
object Data {
    var clientID = ""
    var moneyAmount: Long = 0
    var listTransfer = ArrayList<String>()
    var transferFlag = false
    var verificationStatus = 0

}