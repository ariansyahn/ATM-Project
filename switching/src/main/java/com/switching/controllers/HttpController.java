package com.switching.controllers;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpController {
    private static final Logger logger = LoggerFactory.getLogger(HttpController.class);

    public String sendHttpRequest(String message,String url){
        String result="";
        try{
            CloseableHttpClient client = HttpClients.createSystem();
            HttpPost httpPost = new HttpPost("http://localhost:8082/"+url);
            StringEntity entity = new StringEntity(message);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept","text/plain");
            httpPost.setHeader("Content-type","text/plain");
            CloseableHttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
            client.close();
            logger.info("Sending Http Request to http://localhost:8082/{}",url);
        }catch (Exception e){
            logger.error("Error : {} in {} method",e.getMessage(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
            System.out.println(e.getMessage());
        }
        return result;
    }
}
