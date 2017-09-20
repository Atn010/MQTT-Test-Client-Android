package com.atn010.infoxyz

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by atn01 on 09/18/2017.
 */


class Data(var clientId: String) : MqttCallback {

    var listTransfer = ArrayList<String>();

    var topicVerificationRequest = "verification/request/"+clientId
    var topicVerificationResponse = "verification/response/"+clientId

    var topicTransactionRequest ="transaction/request/"+clientId
    var topicTransactionList = "transaction/list/"+clientId
    //var topicUpdate = "transaction/update"+clientId <- use this in the future to append data

    var topicTransferRequest ="transaction/request/"+clientId
    var topicTransferConfirm = "transfer/feedback/"+clientId


    var broker = "tcp://192.168.56.104:1883"
    var payload = "";
    var qos = 2;
    var persistence =  MemoryPersistence();

    //constructor?
    val Client = MqttClient(broker, clientId, persistence)
    val connOpts = MqttConnectOptions()
    

    fun transactionRequest() {
        // this request data from server

        payload = "request"

        val Client = MqttClient(broker, clientId, persistence)
        val connOpts = MqttConnectOptions()

        //if(!Client.isConnected()) {
            connOpts.setCleanSession(false)
            Client.connect();
        //}
        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(false);

        Client.subscribe(topicTransactionRequest);
        Client.subscribe(topicTransactionList)

        Client.setCallback(this)
        Client.publish(topicTransactionRequest, message);

    }

    fun transactionListUpdate(){
        //change this to only request the very latest

        //if(!Client.isConnected()) {

            connOpts.setCleanSession(false)
            Client.connect();
        //}
        payload = "request"

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(true);

        Client.subscribe(topicTransactionRequest);
        Client.subscribe(topicTransactionList)

        Client.setCallback(this)
        Client.publish(topicTransactionRequest, message);
    }

    fun transferRequest(target:String, amount:Double) {
        // this request transfer
        var dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")

        if(!Client.isConnected()) {

            connOpts.setCleanSession(false)
            Client.connect();

        }
        
        //var rawDateTime = Date().toString();
        var currentDateTime = dateFormat.format(Date())


        payload = currentDateTime+"-"+target+"-Rp."+amount

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(true);

        Client.subscribe(topicTransferRequest);
        Client.subscribe(topicTransferConfirm)
        Client.setCallback(this)

        Client.publish(topicTransferRequest, message);
    }

    fun verificationRequest(){
        if(!Client.isConnected()) {

            connOpts.setCleanSession(false)
            Client.connect();
        }
        payload = "request"

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(true);

        Client.subscribe(topicTransactionRequest);
        Client.subscribe(topicTransactionList)

        Client.setCallback(this)
        Client.publish(topicTransactionRequest, message);
    }

    fun verificationResponse(status: Boolean): Boolean{

        return status
    }


    override fun connectionLost(cause: Throwable) {
        Client.reconnect()
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
    }

    @Throws(Exception::class)

    override fun messageArrived(topic: String, message: MqttMessage) {

        if(topic == topicTransactionList){

            var messageText = message.payload.toString()
            var rawList = messageText.split('|')
            listTransfer = ArrayList(rawList)
        }
        if(topic == topicTransferConfirm){
            var messageText = message.payload.toString()

            if(messageText == "confirmed"){
                transactionListUpdate()

            }
            if(messageText == "failed"){

            }
        }
        if(topic == topicVerificationResponse){
            var messageText = message.payload.toString()

            if(messageText == "confirmed"){
                verificationResponse(true)

            }
            if(messageText == "failed"){
                verificationResponse(false)

            }
        }



    }




}