package com.secondatm.dao;

import com.secondatm.controllers.HttpController;
import com.secondatm.controllers.iso.ISOController;
import com.secondatm.services.TransInquiryService;
import com.secondatm.services.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class TransferDao {
    private static final Logger logger = LoggerFactory.getLogger(TransferDao.class);
    private static ISOController isoController = new ISOController();
    private static HttpController httpController = new HttpController();
    @Autowired
    private JdbcTemplate jdbc;

    public TransferDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String transferInquiry(String accountNumber,int amount,String beneficiaryNumber) {
        Dao dao = new Dao(jdbc);
        TransInquiryService transInquiryService = new TransInquiryService (jdbc);
        String getResponse="";
        System.out.println(beneficiaryNumber);
        try{
            if (dao.isAccountExist(beneficiaryNumber)){
                getResponse= transInquiryService.buildResponseMessage (accountNumber,"76",0,
                        0,"Null");
                logger.info("Beneficiary bank number does not exist");
            }else {
                String getResult = transInquiryService.transInquiryQuery(accountNumber,amount,beneficiaryNumber);
                String[] arrOfStr = getResult.split(",");
//                System.out.println(arrOfStr[0]);
//                System.out.println(arrOfStr[1]);
//                System.out.println(arrOfStr[2]);
                getResponse= transInquiryService.buildResponseMessage (accountNumber,arrOfStr[0],amount,
                        Integer.parseInt(arrOfStr[1]),arrOfStr[2]);
            }
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }

    public String transfer(String accNumber, String pinNumber,int amount,String beneficiaryNumber) {
        TransferService transferService = new TransferService (jdbc);
        String getResponse="";
//        System.out.println("bujang");
        try{
            String getResult = transferService.transferQuery(amount,beneficiaryNumber);
            getResponse= transferService.buildResponseMessage (
                    accNumber,pinNumber,amount,getResult,beneficiaryNumber);
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }

}
