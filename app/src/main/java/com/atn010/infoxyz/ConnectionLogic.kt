package com.atn010.infoxyz

import com.atn010.infoxyz.Data.clientID
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


    fun transactionRequest() {

        if (!Client.isConnected) {
            ConnectToServer()
        }
        if (Client.isConnected) {
            Client.publish(topicTransactionRequest, messageToServer("request"))
        }

    }

    fun transferRequest(target: String, amount: Long) {

        if (!Client.isConnected) {
            ConnectToServer()
        }
        if (Client.isConnected) {
            val currentDateTime = configureCurrentDate()

            Client.publish(topicTransferRequest, messageToServer(currentDateTime + "~" + clientID + "~" + target + "~" + amount))
        }
    }

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

    private fun configureCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")
        val currentDateTime = dateFormat.format(Date())
        return currentDateTime
    }

    fun messageToServer(payload: String): MqttMessage {
        val message = MqttMessage(payload.toByteArray())
        message.qos = 1
        message.isRetained = false

        return message
    }

    fun ConnectToServer() {
        try {
            connOpts.isCleanSession = false
            connOpts.isAutomaticReconnect = true

            Client.connect(connOpts)
            Client.setCallback(this)

            Client.subscribe(topicTransferConfirm)
            Client.subscribe(topicVerificationResponse)
            Client.subscribe(topicTransactionList)
            Client.subscribe(topicMoney)

        } catch (mse: MqttSecurityException) {
            mse.printStackTrace()
        } catch (me: MqttException) {
            me.printStackTrace()
        }
    }

    override fun connectionLost(cause: Throwable) {
        print(cause)
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
    }

    @Throws(Exception::class)
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

    private fun processTransferResponse(message: MqttMessage) {
        val (processedDate, processedStatus) = processResponseMessage(message)

        Data.transferFlag = false

        if (latestTransferDate == processedDate) {
            if (processedStatus == "confirmed") {
                transactionRequest()

            }
            if (processedStatus == "failed") {

            }
        }
    }

    private fun processResponseMessage(message: MqttMessage): Pair<String, String> {
        val messageText = message.toString()
        val processedList = ArrayList(messageText.split("~"))
        val processedDate = processedList.get(0)
        val processedStatus = processedList.get(1)
        return Pair(processedDate, processedStatus)
    }

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

    private fun processNewMoneyAmount(message: MqttMessage) {
        val messageText = message.toString().toLong()
        Data.moneyAmount = messageText
    }

    /**
     * This method processes the Message into a List
     */
    private fun processNewTransactionList(message: MqttMessage) {
        Data.listTransfer.clear()

        val messageText = message.toString().substring(1, (message.toString().length) - 1)
        Data.listTransfer = ArrayList(messageText.split(", "))
    }


}