package org;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class HealthThread implements Callable<String> {

    private CountDownLatch latch;
    String host;
    String path;

    public HealthThread(CountDownLatch latch,String host,String path){
        this.latch = latch;
        this.host=host;
        this.path=path;
    }

    public String call() throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        long timeElapsed=0l;
        try {
            // specify the host, protocol, and port
            HttpHost target = new HttpHost(host, 443, "https");

            // specify the get request
            HttpGet getRequest = new HttpGet(path);

            long startTime = System.currentTimeMillis();

            latch.countDown();
            System.out.println("Barrier opens for :: "+Thread.currentThread().getName()+" :: "+System.currentTimeMillis());
            HttpResponse httpResponse = httpclient.execute(target, getRequest);
            HttpEntity entity = httpResponse.getEntity();
            long endTime = System.currentTimeMillis();

            timeElapsed=(endTime - startTime);
            System.out.println("Time to respond : " + (endTime - startTime) + " milliSeconds ");
            System.out.println(httpResponse.getStatusLine());
            Header[] headers = httpResponse.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                System.out.println(headers[i]);
            }
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity));
                System.out.println("----------------------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return timeElapsed+"";
    }
}
