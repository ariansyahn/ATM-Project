package com.atm.services;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    private JdbcTemplate jdbc;
    public PaymentService(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }
    public String buildResponseMessage(String rekening,String responseCode,int sisaSaldo,
                                       String forwardingInstitutionCode, String virtualAccount,
                                       String server,String port){
        byte[] result = new byte[0];
        try {
            // Load package from resources directory.
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");
            isoMsg.set (2,rekening);
            isoMsg.set(3, "999999");
            isoMsg.set(4, "000000000000");
            isoMsg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
            isoMsg.set(11, "999999");
            isoMsg.set (12, new SimpleDateFormat ("hhmmss").format(new Date ()));
            isoMsg.set (13, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set (15, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set(18,"9999");
            isoMsg.set(32,"99999999999");
            isoMsg.set(33,forwardingInstitutionCode);
            isoMsg.set(37,"RETRIEVAL123");
            isoMsg.set(41, "12340001");
            isoMsg.set (42,"000000000000000");
            isoMsg.set(43, "1234000123123400012312340001231234000123");
            isoMsg.set(49, "840");
            isoMsg.set(54,server+port);
            if(responseCode.equalsIgnoreCase("00")){
                isoMsg.set (62,sisaSaldo+"");
                isoMsg.set (102,virtualAccount);
                isoMsg.set (39, "00");
            }else if (responseCode.equalsIgnoreCase("12")){
                isoMsg.set (62,sisaSaldo+"");
                isoMsg.set (102,virtualAccount);
                isoMsg.set (39, "12");
            }
//            ISOController decodeIso = new ISOController ();
//            decodeIso.printISOMessage(isoMsg);
            result= isoMsg.pack();
//            System.out.println ("ISO Message from Client: "+ new String(result));
        } catch (ISOException e) {
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println (e.getMessage());
        }
        return new String(result);
    }

    public String paymentQuery(String accNumber, int amount, String virtualAccount){
        String result="";
        try {
            BalanceService balanceService = new BalanceService(jdbc);
            String query = "update account set balance = balance-"+amount+" where acc_number = '"+accNumber+"'";
            int execute = jdbc.update(query);
            if (execute==1){
                System.out.println("Success");
                //update saldo virtual account
                query = "update account set balance = balance+"+amount+" where acc_number = '"+virtualAccount+"'";
                execute = jdbc.update(query);
                if (execute==1){
                    System.out.println("Success");
                }
                //sisa saldo si pengirim
                result = "00,"+balanceService.checkSaldoQuery(accNumber);
                logger.info("Success payment with amount {} to {} ", amount,virtualAccount);
            }else {
                result = "12,"+balanceService.checkSaldoQuery(accNumber);
                logger.info("Fail payment with amount {} to {} ", amount,virtualAccount);
            }
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println(e.getMessage());
        }


//        System.out.println(result);
        return result;
    }
}