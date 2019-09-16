package com.atm.controllers.iso;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ISOPurchaseController {
    private static final Logger logger = LoggerFactory.getLogger(ISOPurchaseController.class);
    private InputStream is = getClass().getResourceAsStream("/fields.xml");
    private static GenericPackager packager;
    //    private ISOController isoController = new ISOController();
    private ISOMsg isoMsg;
    public String buildISO(String accNumber,String pin, int amount, String phoneNumber,String server){
        byte[] result= new byte[1];
        try {
            packager = new GenericPackager(is);
            isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0200");
            isoMsg.set(2,accNumber);
            //payment processing code
            isoMsg.set(3, "180099");
            isoMsg.set(4, String.valueOf(amount));
            isoMsg.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
            isoMsg.set(11, "999999");
            isoMsg.set(12,new SimpleDateFormat("hhmmss").format(new Date()));
            isoMsg.set(13,new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(15,new SimpleDateFormat("MMdd").format(new Date()));
            isoMsg.set(18,"9999");
            isoMsg.set(32,"99999999999");
            //DANA TOP UP CODE
            isoMsg.set(37,"RETRIEVAL123");
            isoMsg.set(41, "12340001");
            isoMsg.set(42, "123456789123456");
            isoMsg.set(43, "1234000123123400012312340001231234000123");
            isoMsg.set(49, "840");
            isoMsg.set(52,pin);
            isoMsg.set(54,server);
            //phone number
            isoMsg.set(62,phoneNumber);
            //issuer bank code
            isoMsg.set(100,"2222");
            isoMsg.set(102,"9999");
            result = isoMsg.pack();
//            System.out.println(new String(result));
            logger.info("Built ISO Message for Purchase");
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println(e.getMessage());
        }
        return new String(result);
    }
}
