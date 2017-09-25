package com.atn010.infoxyz

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList




/**
 * Created by atn01 on 09/18/2017.
 */


class ConnectionLogic() : MqttCallback{

    var topicMoney = "transaction/amount/"+Data.clientID

    var topicVerificationRequest = "verification/request/"+Data.clientID
    var topicVerificationResponse = "verification/response/"+Data.clientID

    var topicTransactionRequest ="transaction/request/"+Data.clientID
    var topicTransactionList = "transaction/list/"+Data.clientID
    //var topicUpdate = "transaction/update"+clientId <- use this in the future to append data

    var topicTransferRequest = "transfer/request/"+Data.clientID
    var topicTransferConfirm = "transfer/response/"+Data.clientID


    var broker = "tcp://192.168.56.104:1883"
    var payload = "";
    var qos = 1;
    var persistence =  MemoryPersistence();
    var transferCompare = ""
    var verificationCompare = ""

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

        Client.setCallback(this)
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
        message.setRetained(false);

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


        payload = currentDateTime+" - "+Data.clientID+" - "+target+" -Rp. "+amount
        transferCompare = payload;
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

        payload = username+" | "+password
        verificationCompare = payload;

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(false);

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
            Data.listTransfer.clear();

            var messageText = message.toString()
            var rawList = ArrayList( messageText.split('|'))


            var i = 0;
            for(item in rawList){
                var processedList = ArrayList(rawList.get(i).split(" - "));
                //var DateTimeList = ArrayList(rawList.get(i).split( " - "));

                var listObject = List

                listObject.dateTime = processedList.get(0);
                listObject.account = processedList.get(1);
                listObject.recipient = processedList.get(2);
                listObject.amount = processedList.get(3).toLong();

                processedList.clear();
                Data.listTransfer.add(listObject)
            }


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
                Data.verificationStatus = 2


            }
            if(messageText == "failed"){
                Data.verificationStatus = 1

            }
            Client.unsubscribe(topicVerificationRequest)
            Client.unsubscribe(topicVerificationResponse)

        }
        if(topic == topicMoney){
            var messageText = message.toString().toLong()
            Data.moneyAmount = messageText
        }



    }




}