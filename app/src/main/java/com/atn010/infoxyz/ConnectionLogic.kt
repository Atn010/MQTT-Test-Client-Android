package com.atn010.infoxyz

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by atn01 on 09/18/2017.
 */


class ConnectionLogic() : MqttCallback{

    var topicVerificationRequest = "verification/request/"+Data.clientID
    var topicVerificationResponse = "verification/response/"+Data.clientID

    var topicTransactionRequest ="transaction/request/"+Data.clientID
    var topicTransactionList = "transaction/list/"+Data.clientID
    //var topicUpdate = "transaction/update"+clientId <- use this in the future to append data

    var topicTransferRequest = "transfer/request/"+Data.clientID
    var topicTransferConfirm = "transfer/feedback/"+Data.clientID


    var broker = "tcp://192.168.56.104:1883"
    var payload = "";
    var qos = 1;
    var persistence =  MemoryPersistence();

    //constructor?
    val Client = MqttClient(broker, Data.clientID, persistence)
    val connOpts = MqttConnectOptions()




    fun transactionRequest() {
        // this request data from server
//        val Client = MqttClient(broker, clientId, persistence)
//        val connOpts = MqttConnectOptions()

        payload = "request"

        if(!Client.isConnected) {
            ConnectToServer()
        }

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(false);

        Client.subscribe(topicTransactionRequest);
        Client.subscribe(topicTransactionList)

        //Client.setCallback(this)
        Client.publish(topicTransactionRequest, message);

    }

    fun transactionListUpdate(){
        //change this to only request the very latest

//        val Client = MqttClient(broker, clientId, persistence)
//        val connOpts = MqttConnectOptions()

        if(!Client.isConnected) {
            ConnectToServer()
        }

        payload = "request"

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(true);

        Client.subscribe(topicTransactionRequest);
        Client.subscribe(topicTransactionList)

        //Client.setCallback(this)
        Client.publish(topicTransactionRequest, message);
    }

    fun transferRequest(target:String, amount:Long) {
//        val Client = MqttClient(broker, clientId, persistence)
//        val connOpts = MqttConnectOptions()
        // this request transfer
        var dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")

        if(!Client.isConnected) {
            ConnectToServer()
        }

        //var rawDateTime = Date().toString();
        var currentDateTime = dateFormat.format(Date())


        payload = currentDateTime+"-"+target+"-Rp."+amount

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        //message.setRetained(true);

        Client.subscribe(topicTransferRequest);
        Client.subscribe(topicTransferConfirm)
        //Client.setCallback(this)

        Client.publish(topicTransferRequest, message);
    }

    fun verificationRequest(username:String, password:String){
//        val Client = MqttClient(broker, clientId, persistence)
//        val connOpts = MqttConnectOptions()

        if(!Client.isConnected) {
            ConnectToServer()
        }

        payload = username+"[{-}]"+password

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(true);

        Client.subscribe(topicVerificationRequest);
        Client.subscribe(topicVerificationResponse)

        //Client.setCallback(this)
        Client.publish(topicTransactionRequest, message);
    }

    fun ConnectToServer() {
        connOpts.setCleanSession(false)
        connOpts.isAutomaticReconnect
        Client.connect(connOpts);
        Client.setCallback(this)
    }

    fun verificationResponse(status: Boolean): Boolean{

        return status
    }


    override fun connectionLost(cause: Throwable) {

 //       val Client = MqttClient(broker, clientId, persistence)
 //       val connOpts = MqttConnectOptions()
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
    }

    @Throws(Exception::class)
    override fun messageArrived(topic: String, message: MqttMessage) {

        if(topic == topicTransactionList){

            var messageText = message.toString()
            var rawList = ArrayList( messageText.split('|'))

            Data.listTransfer.addAll(rawList)
        }

        if(topic == topicTransferConfirm){
            var messageText = message.toString()
            Data.transferFlag = false;

            if(messageText == "confirmed"){
                transactionListUpdate()

            }
            if(messageText == "failed"){

            }
        }
        if(topic == topicVerificationResponse){
            var messageText = message.toString()

            if(messageText == "confirmed"){
                Client.unsubscribe(topicVerificationRequest)
                Client.unsubscribe(topicVerificationResponse)
                verificationResponse(true)


            }
            if(messageText == "failed"){
                verificationResponse(false)

            }

        }



    }




}