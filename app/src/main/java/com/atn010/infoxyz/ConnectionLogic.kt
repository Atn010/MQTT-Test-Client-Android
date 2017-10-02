package com.atn010.infoxyz

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ConnectionLogic : MqttCallback {

    var topicMoney = "transaction/money/" + Data.clientID

    var topicVerificationRequest = "verification/request/" + Data.clientID
    var topicVerificationResponse = "verification/response/" + Data.clientID

    var topicTransactionRequest = "transaction/request/" + Data.clientID
    var topicTransactionList = "transaction/list/" + Data.clientID

    var topicTransferRequest = "transfer/request/" + Data.clientID
    var topicTransferConfirm = "transfer/response/" + Data.clientID


    var broker = "tcp://192.168.56.101:1883"
    var payload = ""
    var qos = 1
    var persistence = MemoryPersistence()


    val Client = MqttClient(broker, Data.clientID, persistence)
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

    fun transactionListUpdate() {

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

            var dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")
            var currentDateTime = dateFormat.format(Date())

            Client.publish(topicTransferRequest, messageToServer(currentDateTime + "~" + Data.clientID + "~" + target + "~" + amount))
        }
    }

    fun verificationRequest(username: String, password: String) {


        if (!Client.isConnected) {
            ConnectToServer()
        }
        if (Client.isConnected) {
            var dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")
            var currentDateTime = dateFormat.format(Date())

            latestVerificationDate = currentDateTime

            Client.publish(topicVerificationRequest, messageToServer(currentDateTime + "~" + username + "~" + password))
        }
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
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
    }

    @Throws(Exception::class)
    override fun messageArrived(topic: String, message: MqttMessage) {

        if (topic == topicTransactionList) {
            Data.listTransfer.clear()

            var messageText = message.toString().substring(1, (message.toString().length) - 1)
            Data.listTransfer = ArrayList(messageText.split(", "))
        }

        if (topic == topicTransferConfirm) {
            var messageText = message.toString()
            var processedList = ArrayList(messageText.split("~"))
            Data.transferFlag = false

            var processedDate = processedList.get(0)
            var processedStatus = processedList.get(1)

            if (latestTransferDate == processedDate) {
                if (processedStatus == "confirmed") {
                    transactionRequest()

                }
                if (processedStatus == "failed") {

                }
            }
        }
        if (topic == topicVerificationResponse) {
            var messageText = message.toString()
            var processedList = ArrayList(messageText.split("~"))

            var processedDate = processedList.get(0)
            var processedStatus = processedList.get(1)

            if (latestVerificationDate == processedDate) {
                if (processedStatus == "confirmed") {
                    Data.verificationStatus = 2

                }
                if (processedStatus == "failed") {
                    Data.verificationStatus = 1
                    Client.disconnect()
                    Client.close()
                }
            } else {
                Data.verificationStatus = 0
            }

        }

        if (topic == topicMoney) {
            var messageText = message.toString().toLong()
            Data.moneyAmount = messageText
        }


    }


}