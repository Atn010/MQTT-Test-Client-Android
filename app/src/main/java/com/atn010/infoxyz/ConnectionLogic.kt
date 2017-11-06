package com.atn010.infoxyz

import com.atn010.infoxyz.Data.clientID
import com.atn010.infoxyz.Data.listTransfer
import com.atn010.infoxyz.Data.moneyAmount
import com.atn010.infoxyz.Data.verificationStatus
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * This class manages the connection between Client and Broker for both Sending and Receiving messages.
 * @author Antonius George Sunggeriwan
 *
 */
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
     * checking if the Client i connected to the broker, and attempt to connect if it wasn't connected already.
     * and publish the message to the corresponding topic with the message: "request".
     *
     * @sample topicTransactionRequest transaction/request/example
     * @sample payload "request"
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
     * checking if the Client i connected to the broker, and attempt to connect if it wasn't connected already.
     *
     * get the current date and time and
     * publish the message to the corresponding topic with the message containing:
     * Date time~SenderAccount~ReceiverAccount~AmountOfMoney.
     *
     * @param target the Recipient of the Transfer request
     * @param amount the Amount of money  wished to be Transfered
     *
     * @sample topicTransferRequest Transfer/request/example
     * @sample payload 31/12/17 23:59~exampleSender~exampleRecipient~25000
     */
    fun transferRequest(target: String, amount: Long) {

        if (!Client.isConnected) {
            ConnectToServer()
        }

        if (Client.isConnected) {
            val currentDateTime = configureCurrentDate()
            Data.transferFlag = true;
            Client.publish(topicTransferRequest, messageToServer(currentDateTime + "~" + clientID + "~" + target + "~" + amount))
        }
    }

    /**
     * This method send a request for verification by
     * checking if the Client i connected to the broker, and attempt to connect if it wasn't connected already.
     *
     * get the current date and time and
     * publish the message to the corresponding topic with the message containing:
     * Date time~username~password.
     *
     * @param username the username of the requester
     * @param password the password of the requester
     *
     * @sample topicVerificationRequest Verification/request/example
     * @sample payload 31/12/17 23:59~exampleUsername~examplePassword
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
     * This method set returns the current date and time in a format as string.
     * @return Date time in a "dd/MM/yy hh:mm" format: 31/12/17 23:59
     */
    private fun configureCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")
        val currentDateTime = dateFormat.format(Date())
        return currentDateTime
    }

    /**
     * This method configure the payload and return MqttMessage for delivery.
     * @param payload The String of Message wished to be sent to the server
     * @return message MQTTMessage, a Byte Array
     *
     * @sample payload "example"
     */
    fun messageToServer(payload: String): MqttMessage {
        val message = MqttMessage(payload.toByteArray())
        message.qos = 1
        message.isRetained = false

        return message
    }

    /**
     * This method connects the Client to the Broker
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
     * This method receive incoming message and send them to the appropriate function.
     *
     * When the message arrive, the Method is check whether or not the topic arrived at fits with the response
     * if the topic is a match, run the appropriate function to process the message.
     *
     * @param topic The Arriving topic
     * @param message The Arriving Message
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
     * This method processed the Transfer Response by
     * Processing the message into the Date and the Status.
     * Check if the Date is valid
     * and check if he Status is either confirm or failed.
     *
     * if confirmed, request and update to the Transaction List.
     *
     * @param message  The Message from the topic
     */
    private fun processTransferResponse(message: MqttMessage) {
        val (processedDate, processedStatus) = processResponseMessage(message)



        if (latestTransferDate == processedDate) {
            Data.transferFlag = false
            if (processedStatus == "confirmed") {
                transactionRequest()

            }
            if (processedStatus == "failed") {

            }
        }
    }

    /**
     * This method split the message into two distinct type:
     * processedDate and processedStatus.
     *
     * @param message The Message from the Topic
     * @return processedDate The result of the Date from Message
     * @return processedStatus The result of the  Payload from Message
     */
    private fun processResponseMessage(message: MqttMessage): Pair<String, String> {
        val messageText = message.toString()
        val processedList = ArrayList(messageText.split("~"))
        val processedDate = processedList.get(0)
        val processedStatus = processedList.get(1)
        return Pair(processedDate, processedStatus)
    }

    /**
     * This method processed the Verification Response by
     * Processing the message into the Date and the Status.
     * Check if the Date is valid
     * and check if he Status is either confirm or failed.
     *
     * @param message the Message from the Topic
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
     * converting the message into string and thn parse it into long
     * and s storing the variable in the moneyAmount.
     *
     * @param message the Message from the Topic
     */
    private fun processNewMoneyAmount(message: MqttMessage) {
        val messageText = message.toString().toLong()
        moneyAmount = messageText
    }

    /**
     * This method processes the Message into a List by
     *
     * Clearing the old list removing the first and last character.
     * which contains : '[' and ']' respectively and
     * split the Message to become a list
     * and put that list into the listTransfer
     *
     * @param message the Message from the Topic
     */
    private fun processNewTransactionList(message: MqttMessage) {
        listTransfer.clear()

        val messageText = message.toString().substring(1, (message.toString().length) - 1)
        listTransfer = ArrayList(messageText.split(", "))
    }


}