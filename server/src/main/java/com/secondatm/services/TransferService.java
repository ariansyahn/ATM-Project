package com.secondatm.services;

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

public class TransferService {
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
    @Autowired
    private JdbcTemplate jdbc;
    public TransferService(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }
    public String buildResponseMessage(String accNumber, String pinNumber,
                                       int amount,String responseCode,String beneficiary){
        byte[] result = new byte[0];
        try {
            // Load package from resources directory.
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");
            isoMsg.set (2,accNumber);
            //processing code
            isoMsg.set(3, "409999");
            isoMsg.set(4, String.valueOf(amount));
            isoMsg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
            isoMsg.set(11, "999999");
            isoMsg.set (12, new SimpleDateFormat ("hhmmss").format(new Date ()));
            isoMsg.set (13, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set (15, new SimpleDateFormat ("MMdd").format(new Date ()));
            isoMsg.set(18,"9999");
            isoMsg.set(32,"99999999999");
            isoMsg.set(33,"99999999999");
            isoMsg.set(37,"RETRIEVAL123");
            isoMsg.set(41, "12340001");
            isoMsg.set (42,"000000000000000");
            isoMsg.set(43, "1234000123123400012312340001231234000123");
            isoMsg.set(49, "840");
            isoMsg.set(52,pinNumber);
            if(responseCode.equalsIgnoreCase("00")){
//                isoMsg.set (62,sisaSaldo+"");
                isoMsg.set (103,beneficiary);
                isoMsg.set (39, "00");
            }else {
//                isoMsg.set (62,sisaSaldo+"");
                isoMsg.set (103,beneficiary);
                isoMsg.set (39, "12");
            }
            isoMsg.set (102,"9999");

            result= isoMsg.pack();
        } catch (ISOException e) {
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println (e.getMessage());
        }
        return new String(result);
    }

    public String transferQuery(int amount, String beneficiaryNumber){
        String result="";
        try {
            String query = "update secondary_account set balance = balance + "+amount+" where acc_number = '"+beneficiaryNumber+"'";
//            System.out.println(query);
            int execute = jdbc.update(query);
            if (execute==1){
                System.out.println("Success");
                result = "00";
                logger.info("Success transfer to {}", beneficiaryNumber);
            }
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println(e.getMessage());
        }
        return result;
    }
}
