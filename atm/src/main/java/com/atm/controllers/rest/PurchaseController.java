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
public class PurchaseController {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private ISOController isoController = new ISOController();
    private ISOMsg isoMsg = new ISOMsg();
    @RequestMapping(method = RequestMethod.POST, value = "/purchase")
    @ResponseBody
    public String purchaseInquiry(@RequestBody String message){
        String response="";
        try{
            System.out.println(message);
            isoMsg = isoController.parseISOMessage(message);
            String accNumber = isoMsg.getString(2);
            String phoneNumber = isoMsg.getString(62);
            String server = isoMsg.getString(54).substring(0,9);
            String port = isoMsg.getString(54).substring(9);
            int amount = Integer.parseInt(isoMsg.getString(4));
            PurchaseDao purchaseDao = new PurchaseDao(jdbcTemplate);
            response = purchaseDao.purchase(accNumber,amount,phoneNumber,server,port);
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println(e.getMessage());
        }
        System.out.println(response);
        return response;
    }


}
