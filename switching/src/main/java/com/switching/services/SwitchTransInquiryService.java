package com.switching.services;

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

public class SwitchTransInquiryService {
    private static final Logger logger = LoggerFactory.getLogger(SwitchTransInquiryService.class);
    @Autowired
    private JdbcTemplate jdbc;
    public SwitchTransInquiryService(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }
    public String buildResponseMessage(String rekening, String responseCode,int amount, String beneficiary){
        byte[] result = new byte[0];
        try {
            // Load package from resources directory.
            InputStream is = getClass().getResourceAsStream("/fields.xml");
            GenericPackager packager = new GenericPackager(is);

            ISOMsg isoMsg = new ISOMsg();
            isoMsg.setPackager(packager);
            isoMsg.setMTI("0210");
            isoMsg.set (2,rekening);
            //trans inquiry processing code
            isoMsg.set(3, "399999");
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
//                isoMsg.set (62,sisaSaldo+"");
                isoMsg.set (103,beneficiary);
                //success
                isoMsg.set (39, responseCode);
            isoMsg.set (102,"9999");
            result= isoMsg.pack();
        } catch (ISOException e) {
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println (e.getMessage());
        }
        return new String(result);
    }
}
