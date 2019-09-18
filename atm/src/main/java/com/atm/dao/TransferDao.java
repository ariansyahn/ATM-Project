package com.atm.dao;

import com.atm.controllers.HttpController;
import com.atm.controllers.iso.ISOController;
import com.atm.services.SwitchTransInquiryService;
import com.atm.services.TransInquiryService;
import com.atm.services.TransferService;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.DataOutputStream;
import java.net.Socket;

public class TransferDao {
    private static final Logger logger = LoggerFactory.getLogger(TransferDao.class);
    private static ISOController isoController = new ISOController();
    private static HttpController httpController = new HttpController();
    @Autowired
    private JdbcTemplate jdbc;

    private Socket socket;

    public TransferDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String transferInquiry(String accountNumber, String pinNumber, int amount,String beneficiaryNumber,
                                  String server,String port) {
        Dao dao = new Dao(jdbc);
        TransInquiryService transInquiryService = new TransInquiryService (jdbc);
        String getResponse="";
        try{
            if (dao.isAccountExist(beneficiaryNumber)){
                getResponse= transInquiryService.buildResponseMessage (accountNumber,"76",0,"Null",server,port);
                logger.info("Beneficiary bank number does not exist");
            }else {
                if(dao.checkAuthentication (accountNumber, pinNumber)){
                    String getResult = transInquiryService.transInquiryQuery(accountNumber,amount,beneficiaryNumber);
                    String[] arrOfStr = getResult.split(",");
                    getResponse= transInquiryService.buildResponseMessage (accountNumber,arrOfStr[0],Integer.parseInt(arrOfStr[1]),arrOfStr[2],
                            server,port);
                }else{
                    getResponse = transInquiryService.buildResponseMessage (accountNumber,"05",0, "Null",
                            server,port);
                }
            }
            socket = new Socket(server, Integer.parseInt(port));
            if (socket.isConnected()){
                System.out.println("Connect to socket");
            }
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(getResponse);
            dout.flush();
            dout.close();
            socket.close();
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }

    public String transfer(String accountNumber,int amount,String beneficiaryNumber,String server,String port) {
        TransferService transferService = new TransferService (jdbc);
        String getResponse="";
        try{
            String getResult = transferService.transferQuery(accountNumber,amount,beneficiaryNumber);
            String[] arrayStr = getResult.split(",");
            getResponse= transferService.buildResponseMessage (accountNumber,arrayStr[0],Integer.parseInt(arrayStr[1]),beneficiaryNumber,
                    server,port);
            socket = new Socket(server, Integer.parseInt(port));
            if (socket.isConnected()){
                System.out.println("Connect to socket");
            }
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(getResponse);
            dout.flush();
            dout.close();
            socket.close();
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }

    public String switchTransferInquiry(String message,String accountNumber, String pinNumber,
                                        String server,String port) {
        Dao dao = new Dao(jdbc);
        SwitchTransInquiryService switchTransInquiryService = new SwitchTransInquiryService (jdbc);
        String getResponse="";
        try{
            if(dao.checkAuthentication (accountNumber, pinNumber)){
                String url="switchingtransinquiry";
                getResponse = httpController.sendHttpRequestSwitch(message,url);
//                ISOMsg isoMsg = isoController.parseISOMessage(result);
//                String getResult = switchTransInquiryService.switchTransInquiryQuery(
//                        accountNumber,amount,beneficiaryNumber);
//                String[] arrOfStr = getResult.split(",");
//                getResponse= switchTransInquiryService.buildResponseMessage (
//                        accountNumber,arrOfStr[0],Integer.parseInt(arrOfStr[1]),arrOfStr[2]);
            }else{
                getResponse = switchTransInquiryService.buildResponseMessage (
                        accountNumber,"05",0, "Null");
            }
            socket = new Socket(server, Integer.parseInt(port));
            if (socket.isConnected()){
                System.out.println("Connect to socket with Port : "+Integer.parseInt(port));
            }
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(getResponse);
            dout.flush();
            dout.close();
            socket.close();
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }

    public String switchTransfer(String message,String server,String port) {
//        Dao dao = new Dao(jdbc);
        TransferService transferService = new TransferService (jdbc);
        String getResponse="";
        try{
//            if(dao.checkAuthentication (accountNumber, pinNumber)){
                String url="switchingtransfer";
                getResponse = httpController.sendHttpRequestSwitch(message,url);
                ISOMsg isoMsg = isoController.parseISOMessage(getResponse);
                if (isoMsg.getString(39).equalsIgnoreCase("00")){
                    System.out.println("Success");
                    String getResult = transferService.switchTransferQuery(isoMsg.getString(2),
                            Integer.parseInt(isoMsg.getString(4)),isoMsg.getString(103));
                    String[] arrOfStr = getResult.split(",");
                    getResponse= transferService.buildResponseMessage (
                            isoMsg.getString(2),arrOfStr[0],Integer.parseInt(arrOfStr[1]),isoMsg.getString(103),server,port);
                }
            socket = new Socket(server, Integer.parseInt(port));
            if (socket.isConnected()){
                System.out.println("Connect to socket");
            }
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(getResponse);
            dout.flush();
            dout.close();
            socket.close();
//                String getResult = switchTransInquiryService.switchTransInquiryQuery(
//                        accountNumber,amount,beneficiaryNumber);

//            }else{
//                getResponse = switchTransInquiryService.buildResponseMessage (
//                        accountNumber,"05",0, "Null");
//            }
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }
}
