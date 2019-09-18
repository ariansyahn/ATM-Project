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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BalanceService {
    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);
    @Autowired
    private JdbcTemplate jdbc;
    public BalanceService(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    public String checkSaldoQuery(String accNumber){
        String result="";
        try {
            String query = "select * from account where acc_number = '"+accNumber+"'";
            List<Map<String,Object>> accounts = jdbc.queryForList(query);

            for (Map<String,Object> account:accounts){
                for(Iterator<Map.Entry<String,Object>> itr = account.entrySet().iterator(); itr.hasNext();){
                    Map.Entry<String,Object> entry = itr.next();
                    if (entry.getKey().equalsIgnoreCase("balance")){
                        result = entry.getValue().toString();
                        logger.info("Success checking balance of {} with amount {}",
                                accNumber,result);
                    }
                }
            }
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println(e.getMessage());
        }
        return result;
    }


}