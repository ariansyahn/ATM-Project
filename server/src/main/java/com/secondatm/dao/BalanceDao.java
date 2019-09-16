package com.secondatm.dao;

import com.secondatm.services.BalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class BalanceDao {
    private static final Logger logger = LoggerFactory.getLogger(BalanceDao.class);
    @Autowired
    private JdbcTemplate jdbc;

    public BalanceDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String balanceCheck(String accountNumber, String pinNumber) {
        Dao dao = new Dao(jdbc);
        BalanceService balanceService = new BalanceService (jdbc);
        String getResponse="";
        try{
            if(dao.checkAuthentication (accountNumber, pinNumber)){
                String getResult = balanceService.checkSaldoQuery(accountNumber);
                getResponse= balanceService.buildResponseMessage (accountNumber,"00",Integer.parseInt (getResult));
            }else{
                getResponse = balanceService.buildResponseMessage (accountNumber,"05", 0);
            }
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }
}
