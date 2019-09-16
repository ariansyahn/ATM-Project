package com.switching.dao;

import com.switching.controllers.HttpController;
import com.switching.controllers.iso.ISOController;
import com.switching.services.SwitchTransInquiryService;
import com.switching.services.TransInquiryService;
import com.switching.services.TransferService;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class TransferDao {
    private static final Logger logger = LoggerFactory.getLogger(TransferDao.class);
    private static ISOController isoController = new ISOController();
    private static HttpController httpController = new HttpController();
    private static ISOMsg isoMsg = new ISOMsg();
    @Autowired
    private JdbcTemplate jdbc;

    public TransferDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }


    public String switchTransferInquiry(String accountNumber, int amount, String beneficiaryNumber) {
//        Dao dao = new Dao(jdbc);
        SwitchTransInquiryService switchTransInquiryService = new SwitchTransInquiryService (jdbc);
        String getResponse="";
        try{
//            if(dao.checkAuthentication (accountNumber, pinNumber)){
                //potongan
                amount = amount+6500;
//                String getResult = switchTransInquiryService.switchTransInquiryQuery(
//                        accountNumber,amount,beneficiaryNumber);
//                String[] arrOfStr = getResult.split(",");
                getResponse = switchTransInquiryService.buildResponseMessage (
                        accountNumber,"00",amount,beneficiaryNumber);
                isoController.parseISOMessage(getResponse);
                String url="switchingtransinquiry";
                getResponse = httpController.sendHttpRequest(getResponse,url);
                isoController.parseISOMessage(getResponse);
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

    public String switchTransfer(String message) {
//        Dao dao = new Dao(jdbc);
        TransferService transferService = new TransferService (jdbc);
        String getResponse="";
        try{
//            if(dao.checkAuthentication (accountNumber, pinNumber)){
            //potongan
//            amount = amount+6500;
//                String getResult = transferService.switchTransInquiryQuery(
//                        accountNumber,amount,beneficiaryNumber);
//                String[] arrOfStr = getResult.split(",");
//            getResponse = switchTransInquiryService.buildResponseMessage (
//                    accountNumber,"00",amount,beneficiaryNumber);
//            isoController.parseISOMessage(getResponse);
            String url="switchingtransfer";
            getResponse = httpController.sendHttpRequest(message,url);
            isoMsg = isoController.parseISOMessage(getResponse);
            if (isoMsg.getString(39).equalsIgnoreCase("00")){
                int amount = Integer.parseInt(isoMsg.getString(4));
                //potongan
                amount = amount+6500;
                getResponse = transferService.buildResponseMessage(isoMsg.getString(2),isoMsg.getString(39),
                        amount,isoMsg.getString(103));
                System.out.println("Beneficiary : "+isoMsg.getString(103));
            }

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
