package com.atm.dao;

import com.atm.services.PaymentInquiryService;
import com.atm.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.DataOutputStream;
import java.net.Socket;

public class PaymentDao {
    private static final Logger logger = LoggerFactory.getLogger(PaymentDao.class);
    @Autowired
    private JdbcTemplate jdbc;

    private Socket socket;

    public PaymentDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String paymentInquiry(String accountNumber, String pinNumber, int amount,
                                 String forwardingInstitutionCode, String virtualAccount,
                                 String server,String port) {
        PaymentInquiryService paymentInquiryService = new PaymentInquiryService (jdbc);
        Dao dao = new Dao(jdbc);
        String getResponse="";
        try{
            if(dao.checkAuthentication (accountNumber, pinNumber)){
                if (dao.isAccountExist(virtualAccount)){
                    getResponse= paymentInquiryService.buildResponseMessage (accountNumber,"76",0,
                            forwardingInstitutionCode,"Null",server,port);
                    logger.info("Virtual Account does not exist");
                }else {
                    String getResult = paymentInquiryService.paymentInquiryQuery(accountNumber,amount,virtualAccount);
                    String[] arrOfStr = getResult.split(",");
                    getResponse= paymentInquiryService.buildResponseMessage (accountNumber,arrOfStr[0],
                            Integer.parseInt(arrOfStr[1]),forwardingInstitutionCode,arrOfStr[2],
                            server,port);
                }
            }else{
                getResponse = paymentInquiryService.buildResponseMessage (accountNumber,"05",
                        0,forwardingInstitutionCode, "Null",server,port);
                logger.info("Authentication Failed");
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

    public String payment(String accountNumber, int amount,
                          String forwardingInstitutionCode,String virtualAccount,
                          String server,String port) {
        PaymentService paymentService = new PaymentService (jdbc);
        String getResponse="";
        try{
            String getResult = paymentService.paymentQuery(accountNumber,amount,virtualAccount);
            String[] arrayStr = getResult.split(",");
            getResponse= paymentService.buildResponseMessage (accountNumber,arrayStr[0],
                    Integer.parseInt(arrayStr[1]),forwardingInstitutionCode,virtualAccount,server,port);
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
            System.out.println ("Error: " + e.getMessage ());
        }
        return getResponse;
    }
}
