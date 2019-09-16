package com.atm.controllers.messaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.atm.controllers.HttpController;
import com.atm.controllers.iso.ISOController;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.jpos.iso.ISOMsg;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver
{
    private static HttpController httpController = new HttpController();
    private ISOController isoController = new ISOController();
    private ISOMsg isoMsg = new ISOMsg();
    @JmsListener(destination = "bank")
    public void listener(String message) {
        isoMsg = isoController.parseISOMessage(message);
        if (isoMsg.getString(3).equalsIgnoreCase("300000")){
            httpController.sendHttpRequest(message,"check");
        } else if (isoMsg.getString(3).equalsIgnoreCase("010000")){
            httpController.sendHttpRequest(message,"withdraw");
        }else if (isoMsg.getString(3).equalsIgnoreCase("180000")){
            httpController.sendHttpRequest(message,"payment");
        }else if (isoMsg.getString(3).equalsIgnoreCase("380000")){
            httpController.sendHttpRequest(message,"paymentinquiry");
        }else if (isoMsg.getString(3).equalsIgnoreCase("180099")){
            httpController.sendHttpRequest(message,"purchase");
        }else if (isoMsg.getString(3).equalsIgnoreCase("380099")){
            httpController.sendHttpRequest(message,"purchaseinquiry");
        }else if (isoMsg.getString(3).equalsIgnoreCase("400099")){
            httpController.sendHttpRequest(message,"switchingtransfer");
        }else if (isoMsg.getString(3).equalsIgnoreCase("390099")){
            httpController.sendHttpRequest(message,"switchingtransinquiry");
        }else if (isoMsg.getString(3).equalsIgnoreCase("400000")){
            httpController.sendHttpRequest(message,"transfer");
        }else if (isoMsg.getString(3).equalsIgnoreCase("390000")){
            httpController.sendHttpRequest(message,"transinquiry");
        }
//        System.out.println("Received Message: " + message);

    }
//    private ConnectionFactory factory = null;
//
//    private Connection connection = null;
//
//    private Session session = null;
//
//    private Destination destination = null;
//
//    private MessageConsumer messageConsumer;
//
//    public MessageReceiver()
//    {
//    }
//
//    public String receiveMessage() throws JMSException {
//        TextMessage text=null;
//        try
//        {
//            factory = new ActiveMQConnectionFactory( ActiveMQConnection.DEFAULT_BROKER_URL );
//            connection = factory.createConnection();
//            connection.start();
//            session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
//            destination = session.createQueue( "bank" );
//            messageConsumer = session.createConsumer( destination );
//            Message message = messageConsumer.receive();
//            if ( message instanceof TextMessage )
//            {
//                text = ( TextMessage ) message;
//                System.out.println( "Received Message is: " + text.getText() );
//            }
//        }
//        catch ( JMSException e )
//        {
//            e.printStackTrace();
//        }
//        assert text != null;
//        return text.getText();
//    }
//
//    public static void main( String[] args ) throws JMSException {
//        ReceiverConsumer receiver = new ReceiverConsumer();
//        String str = receiver.receiveMessage();
//        ISOController isoController = new ISOController();
//        isoController.parseISOMessage(str);
//    }
}
