package com.atm.dao;

import com.atm.services.WithdrawService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.DataOutputStream;
import java.net.Socket;

public class CashWithdrawalDao {
    private static final Logger logger = LoggerFactory.getLogger(CashWithdrawalDao.class);
    @Autowired
    private JdbcTemplate jdbc;
    private Socket socket;
    public CashWithdrawalDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }
    public String cashWithdrawal(String accountNumber, String pinNumber, int amount,String server,String port) {
        Dao dao = new Dao(jdbc);
        WithdrawService withdrawService = new WithdrawService (jdbc);
        String getResponse="";
        try{
            if(dao.checkAuthentication(accountNumber, pinNumber)){
                String getResult = withdrawService.withdrawQuery(accountNumber,amount);
                String[] arrayStr = getResult.split(",");
                if (arrayStr[0].equalsIgnoreCase("51")){
                    getResponse= withdrawService.buildResponseMessage (accountNumber,arrayStr[0],Integer.parseInt (arrayStr[1]),
                            server,port);
                }else {
                    getResponse= withdrawService.buildResponseMessage (accountNumber,arrayStr[0],Integer.parseInt (arrayStr[1]),
                            server,port);
                }
            }else{
                getResponse = withdrawService.buildResponseMessage (accountNumber,"05", 0,server,port);
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
