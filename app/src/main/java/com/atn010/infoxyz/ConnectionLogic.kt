package com.atn010.infoxyz

import com.atn010.infoxyz.Data.clientID
import com.atn010.infoxyz.Data.listTransfer
import com.atn010.infoxyz.Data.moneyAmount
import com.atn010.infoxyz.Data.transferFlag
import com.atn010.infoxyz.Data.verificationStatus

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import java.text.SimpleDateFormat
import java.util.*

import kotlin.collections.ArrayList


class ConnectionLogic : MqttCallback {

    var topicMoney = "transaction/money/" + clientID

    var topicVerificationRequest = "verification/request/" + clientID
    var topicVerificationResponse = "verification/response/" + clientID

    var topicTransactionRequest = "transaction/request/" + clientID
    var topicTransactionList = "transaction/list/" + clientID

    var topicTransferRequest = "transfer/request/" + clientID
    var topicTransferConfirm = "transfer/response/" + clientID


    var broker = "tcp://192.168.56.101:1883"
    var payload = ""
    var qos = 1
    var persistence = MemoryPersistence()


    val Client = MqttClient(broker, clientID, persistence)
    val connOpts = MqttConnectOptions()
    var latestVerificationDate = ""
    var latestTransferDate = ""

    /**
     * This method send a request for Transaction Information by
     *
     * checking if the Client i connected to the broker, and attempt to connect if it wasn't
     *
     * publish the message to the corresponding topic with the message: "request"
     */
    fun transactionRequest() {

        if (!Client.isConnected) {
            ConnectToServer()
        }
        if (Client.isConnected) {
            Client.publish(topicTransactionRequest, messageToServer("request"))
        }

    }

    /**
     * This method send a request for Transfer by
     *
     * checking if the Client i connected to the broker, and attempt to connect if it wasn't
     *
     * get the current date and time
     * publish the message to the corresponding topic with the message containing:
     * Date time~SenderAccount~ReceiverAccount~AmountOfMoney
     */
    fun transferRequest(target: String, amount: Long) {

        if (!Client.isConnected) {
            ConnectToServer()
        }
        if (Client.isConnected) {
            val currentDateTime = configureCurrentDate()

            Client.publish(topicTransferRequest, messageToServer(currentDateTime + "~" + clientID + "~" + target + "~" + amount))
        }
    }

    /**
     * This method send a request for verification by
     *
     * checking if the Client i connected to the broker, and attempt to connect if it wasn't
     *
     * get the current date and time
     * publish the message to the corresponding topic with the message containing:
     * Date time~username~password
     */
    fun verificationRequest(username: String, password: String) {
        if (!Client.isConnected) {
            ConnectToServer()
        }
        if (Client.isConnected) {
            val currentDateTime = configureCurrentDate()

            latestVerificationDate = currentDateTime

            Client.publish(topicVerificationRequest, messageToServer(currentDateTime + "~" + username + "~" + password))
        }
    }

    /**
     * This method set returns the current date and time in a format as string
     */
    private fun configureCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")
        val currentDateTime = dateFormat.format(Date())
        return currentDateTime
    }

    /**
     * This method configure the payload and return MqttMessage for delivery
     */
    fun messageToServer(payload: String): MqttMessage {
        val message = MqttMessage(payload.toByteArray())
        message.qos = 1
        message.isRetained = false

        return message
    }

    /**
     * This method connects to the Broker
     */
    fun ConnectToServer() {
        try {
            connOpts.isCleanSession = false
            connOpts.isAutomaticReconnect = true

            Client.connect(connOpts)
            Client.setCallback(this)

            setSubscription()

        } catch (mse: MqttSecurityException) {
            mse.printStackTrace()
        } catch (me: MqttException) {
            me.printStackTrace()
        }
    }

    /**
     * This method set the subscription for the Client
     */
    private fun setSubscription() {
        Client.subscribe(topicTransferConfirm)
        Client.subscribe(topicVerificationResponse)
        Client.subscribe(topicTransactionList)
        Client.subscribe(topicMoney)
    }

    override fun connectionLost(cause: Throwable) {
        print(cause)
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
    }

    @Throws(Exception::class)
    /**
     * This method receive incoming message and send them to the appropriate function
     *
     * When the message arrive
     * it is check whether or not the topic arrived at fits with the response
     * if the topic is a match, run the appropriate function to process the message
     */
    override fun messageArrived(topic: String, message: MqttMessage) {

        if (topic == topicTransactionList) {
            processNewTransactionList(message)
        }

        if (topic == topicTransferConfirm) {
            processTransferResponse(message)
        }
        if (topic == topicVerificationResponse) {
            processVerificationResponse(message)
        }

        if (topic == topicMoney) {
            processNewMoneyAmount(message)
        }
    }

    /**
     * @param message
     *
     * This method processed the Transfer Response by
     * Processing the message into the Date and the Status
     * Check if the Date is valid
     * and check if he Status is either confirm or failed
     *
     * if confirmed, request and update to the Transaction List
     */
    private fun processTransferResponse(message: MqttMessage) {
        val (processedDate, processedStatus) = processResponseMessage(message)

        transferFlag = false

        if (latestTransferDate == processedDate) {
            if (processedStatus == "confirmed") {
                transactionRequest()

            }
            if (processedStatus == "failed") {

            }
        }
    }

    /**
     * @param message
     * @return processedDate,processedStatus
     *
     * This method split the message into two distinct type
     * processedDate and processedStatus
     */
    private fun processResponseMessage(message: MqttMessage): Pair<String, String> {
        val messageText = message.toString()
        val processedList = ArrayList(messageText.split("~"))
        val processedDate = processedList.get(0)
        val processedStatus = processedList.get(1)
        return Pair(processedDate, processedStatus)
    }

    /**
     * @param message
     *
     * This method processed the Verification Response by
     * Processing the message into the Date and the Status
     * Check if the Date is valid
     * and check if he Status is either confirm or failed
     *
     * if failed, disconnects
     */
    private fun processVerificationResponse(message: MqttMessage) {
        val (processedDate, processedStatus) = processResponseMessage(message)

        if (latestVerificationDate == processedDate) {
            if (processedStatus == "confirmed") {
                verificationStatus = 2
            }
            if (processedStatus == "failed") {
                verificationStatus = 1
                Client.disconnect()
            }
        } else {
            verificationStatus = 0
        }
    }

    /**
     * This method retrive the amount of money by
     *
     * converting the message into string and thn parse it into long
     * and s storing the variable in the moneyAmount
     */
    private fun processNewMoneyAmount(message: MqttMessage) {
        val messageText = message.toString().toLong()
        moneyAmount = messageText
    }

    /**
     * This method processes the Message into a List by
     *
     * Clearing the old list
     * removing the first and last character. which contains : '[' and ']' respectively
     * split the Message to become a list
     * and put that list into the listTransfer
     */
    private fun processNewTransactionList(message: MqttMessage) {
        listTransfer.clear()

        val messageText = message.toString().substring(1, (message.toString().length) - 1)
        listTransfer = ArrayList(messageText.split(", "))
    }


}