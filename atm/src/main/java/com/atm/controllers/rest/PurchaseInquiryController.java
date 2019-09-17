package com.atm.controllers.rest;
import com.atm.controllers.iso.ISOController;
import com.atm.dao.PurchaseDao;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class PurchaseInquiryController {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseInquiryController.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private ISOController isoController = new ISOController();
    private ISOMsg isoMsg = new ISOMsg();
    @RequestMapping(method = RequestMethod.POST, value = "/purchaseinquiry")
    @ResponseBody
    public String purchaseInquiry(@RequestBody String message){
        String response="";
        try{
//            System.out.println(message);
            System.out.println("Receive Purchase Inquiry from Client : ");
            isoMsg = isoController.parseISOMessage(message);
            String accNumber = isoMsg.getString(2);
            String pinNumber = isoMsg.getString(52);
            String phoneNumber = isoMsg.getString(62);
            String server = isoMsg.getString(54).substring(0,9);
            String port = isoMsg.getString(54).substring(9);
            int amount = Integer.parseInt(isoMsg.getString(4));
            PurchaseDao purchaseDao = new PurchaseDao(jdbcTemplate);
            response = purchaseDao.purchaseInquiry(accNumber,pinNumber,amount,phoneNumber,server,port);
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println(e.getMessage());
        }
//        System.out.println(response);
        return response;
    }


}
