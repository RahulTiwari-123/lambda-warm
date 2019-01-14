package org;

import com.amazonaws.services.lambda.runtime.Context;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Map;
import java.util.concurrent.*;

public class Main {


    int threadCount;

    String containerId;
    public static void main(String args[]){
        Main main = new Main();
        main.handleRequest(null,null);
    }

    public String handleRequest(Map<String,Object> input, Context context) {

        String threadCountS = System.getenv("threadCount");
        String host = System.getenv("hostName");
        String path = System.getenv("path");

        if(host == null || host.equalsIgnoreCase("") )
            host="51xuiuiouk.execute-api.us-west-2.amazonaws.com";

        if(path == null || path.equalsIgnoreCase(""))
            path="/test/ping";


        if(threadCountS == null || threadCountS.equals("")){
            threadCount=5;
        }else {
            threadCount = Integer.parseInt(threadCountS);
        }

        final CountDownLatch latch = new CountDownLatch(threadCount);
        //containerId=context.getAwsRequestId();
        if(context!=null)
          System.out.println("Container Id : "+context.getAwsRequestId());
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        DefaultHttpClient httpclient = new DefaultHttpClient();

        for(int i=0;i<threadCount;i++) {
        HealthThread h = new HealthThread(latch,host,path);
            executorService.submit(h);
        }


        try {
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();


        //gate.await();


        return "Success";
    }





}
