package com.switching.controllers;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpController {
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
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return result;
    }
}
