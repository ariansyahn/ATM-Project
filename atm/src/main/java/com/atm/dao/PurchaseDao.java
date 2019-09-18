package com.atm.dao;

import com.atm.services.PaymentInquiryService;
import com.atm.services.PaymentService;
import com.atm.services.PurchaseInquiryService;
import com.atm.services.PurchaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.DataOutputStream;
import java.net.Socket;

public class PurchaseDao {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseDao.class);
    @Autowired
    private JdbcTemplate jdbc;

    private Socket socket;

    public PurchaseDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String purchaseInquiry(String accountNumber, String pinNumber,
                                  int amount, String phoneNumber,String server,String port) {
        PurchaseInquiryService purchaseInquiryService = new PurchaseInquiryService (jdbc);
        Dao dao = new Dao(jdbc);
        String getResponse="";
        try{
            if(dao.checkAuthentication (accountNumber, pinNumber)){
                String getResult = purchaseInquiryService.purchaseInquiryQuery(
                        accountNumber,amount);
                String[] arrOfStr = getResult.split(",");
                getResponse= purchaseInquiryService.buildResponseMessage (accountNumber,arrOfStr[0],
                        Integer.parseInt(arrOfStr[1]),phoneNumber,server,port);
            }else{
                getResponse = purchaseInquiryService.buildResponseMessage (accountNumber,"05",
                        0, "Null",server,port);
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
        } catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }

    public String purchase(String accountNumber, int amount, String phoneNumber,String server,String port) {
        PurchaseService purchaseService = new PurchaseService(jdbc);
        String getResponse="";
        try{
            String getResult = purchaseService.purchaseQuery(accountNumber,amount,phoneNumber);
            String[] arrayStr = getResult.split(",");
            getResponse= purchaseService.buildResponseMessage (accountNumber,arrayStr[0],
                    Integer.parseInt(arrayStr[1]),phoneNumber,server,port);
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
            System.out.println ("Error: " + e.getMessage ());
        }
        return getResponse;
    }
}
