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
    //var topicUpdate = "transaction/update"+clientId <- use this in the future to append conLogic

    var topicTransferRequest = "transfer/request/"+Data.clientID
    var topicTransferConfirm = "transfer/response/"+Data.clientID


    var broker = "tcp://192.168.56.104:1883"
    var payload = "";
    var qos = 1;
    var persistence =  MemoryPersistence();

    //constructor?
    val Client = MqttClient(broker, Data.clientID, persistence)
    val connOpts = MqttConnectOptions()
    var latestVerificationDate = ""
    var latestTransferDate = ""




    fun transactionRequest() {
        // this request conLogic from server
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


        if(!Client.isConnected) {
            ConnectToServer()
        }

        //var rawDateTime = Date().toString();
        var dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")
        var currentDateTime = dateFormat.format(Date())


        payload = currentDateTime+"~"+Data.clientID+"~"+target+"~"+amount

        latestTransferDate = currentDateTime;


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
        var dateFormat = SimpleDateFormat("dd/MM/yy hh:mm")
        var currentDateTime = dateFormat.format(Date())

        payload =currentDateTime+"~"+username+"~"+password

        val message = MqttMessage(payload.toByteArray())
        message.setQos(qos)
        message.setRetained(false);

        latestVerificationDate =currentDateTime;

        Client.subscribe(topicVerificationRequest);
        Client.subscribe(topicVerificationResponse)

        //Client.setCallback(this)
        Client.publish(topicVerificationRequest, message);
    }

    fun ConnectToServer() {

        connOpts.setCleanSession(false)
        connOpts.isAutomaticReconnect
        Client.connect(connOpts);
        Client.setCallback(this)
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
                var processedList = ArrayList(rawList.get(i).split("~"));
                //var DateTimeList = ArrayList(rawList.get(i).split( "~"));

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
            var processedList = ArrayList(messageText.split("~"));
            Data.transferFlag = false;

            var processedDate = processedList.get(0);
            var processedStatus = processedList.get(1);

            if(latestTransferDate == processedDate) {
                if (processedStatus == "confirmed") {
                    transactionListUpdate()

                }
                if (processedStatus == "failed") {

                }
            }
        }
        if(topic == topicVerificationResponse){
            var messageText = message.toString()
            var processedList = ArrayList(messageText.split("~"));

            var processedDate = processedList.get(0);
            var processedStatus = processedList.get(1);

            if(latestVerificationDate == processedDate) {
                if (processedStatus == "confirmed") {
                    Data.verificationStatus = 2


                }
                if (processedStatus == "failed") {
                    Data.verificationStatus = 1
                    Client.close()
                    Client.disconnect()

                }
            }else{
                Data.verificationStatus = 0
            }

        }
        if(topic == topicMoney){
            var messageText = message.toString().toLong()
            Data.moneyAmount = messageText
        }



    }




}