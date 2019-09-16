package com.secondatm.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Dao {
    private static final Logger logger = LoggerFactory.getLogger(Dao.class);
    @Autowired
    private JdbcTemplate jdbc;

    public Dao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    boolean checkAuthentication(String accountNumber, String pinNumber){
        boolean status = false;
        try {
            String sql = "select * from account where acc_number='" + accountNumber + "'";
            List<Map<String, Object>> accounts = jdbc.queryForList(sql);
            if (!accounts.isEmpty()) {
                for (Map<String, Object> account : accounts) {
                    for (Iterator<Map.Entry<String, Object>> itr = account.entrySet().iterator(); itr.hasNext(); ) {
                        Map.Entry<String, Object> entry = itr.next();
                        if (entry.getKey().equalsIgnoreCase("pin")) {
                            String pincheck = entry.getValue().toString();
                            if (Integer.parseInt(pinNumber) == Integer.parseInt(pincheck)) {
                                status = true;
                                logger.info("Authentication succeed");
                            } else {
                                status = false;
                                logger.info("Authentication failed");
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return status;
    }

    boolean isAccountExist(String accountNumber){
        boolean status = false;
        try {
            String sql = "select * from secondary_account where acc_number='" + accountNumber + "'";
            List<Map<String, Object>> accounts = jdbc.queryForList(sql);
            if (!accounts.isEmpty()) {
                for (Map<String, Object> account : accounts) {
                    for (Iterator<Map.Entry<String, Object>> itr = account.entrySet().iterator(); itr.hasNext(); ) {
                        Map.Entry<String, Object> entry = itr.next();
                        if (entry.getKey().equalsIgnoreCase("acc_number")) {
                            String accountCheck = entry.getValue().toString();
                            if (accountCheck.equalsIgnoreCase(accountNumber)) {
                                status = true;
                            } else {
                                status = false;
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return !status;
    }
}