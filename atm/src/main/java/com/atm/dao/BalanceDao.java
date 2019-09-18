package com.atm.dao;

import com.atm.services.BalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.DataOutputStream;
import java.net.Socket;

public class BalanceDao {
    private static final Logger logger = LoggerFactory.getLogger(BalanceDao.class);
    @Autowired
    private JdbcTemplate jdbc;

    private Socket socket;

    public BalanceDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String balanceCheck(String accountNumber, String pinNumber, String server, String port) {
        Dao dao = new Dao(jdbc);
        BalanceService balanceService = new BalanceService (jdbc);
        String getResponse="";
        try{
            if(dao.checkAuthentication (accountNumber, pinNumber)){
                String getResult = balanceService.checkSaldoQuery(accountNumber);
                getResponse= balanceService.buildResponseMessage (accountNumber,"00",Integer.parseInt (getResult),server,port);
            }else{
                getResponse = balanceService.buildResponseMessage (accountNumber,"05", 0,server,port);
            }
            socket = new Socket(server, Integer.parseInt(port));
            if (socket.isConnected()){
                System.out.println("Connect to socket with Port : "+Integer.parseInt(port));
            }
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            dout.writeUTF(getResponse);
            dout.flush();
            dout.close();
            socket.close();
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println ("Error: "+e.getMessage ());
        }
        return getResponse;
    }
}
