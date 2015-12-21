package ru.cdf.zoo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import ru.cdf.zoo.client.ZooClient;
import ru.cdf.zoo.client.ZooClientImpl;
import ru.cdf.zoo.listener.ListenerType;

/**
 * Created by dmitry on 20.12.2015.
 */
public class ZooClientBuilder {
    private String url;
    private String host;
    private String port;
    private RetryPolicy retryPolicy= new ExponentialBackoffRetry(100, 1);
    private ListenerType listenerType=ListenerType.RealTime;
    private Integer listenerTime=100;

    public ZooClientBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public ZooClientBuilder setHost(String host) {
        this.host = host;
        return this;
    }

    public ZooClientBuilder setPort(String port) {
        this.port = port;
        return this;
    }

    public ZooClientBuilder setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    public ZooClientBuilder setListenerType(ListenerType listenerType) {
        this.listenerType = listenerType;
        return this;
    }

    public ZooClientBuilder setListenerTime(Integer listenerTime) {
        this.listenerTime = listenerTime;
        return this;
    }
    public ZooClient build(){
        if(url==null){
            if(port==null){
                port="2181";
            }
            if(host==null){
                host="localhost";
            }
            url=host+":"+port;
        }
        return new ZooClientImpl(url,retryPolicy,listenerType,listenerTime);
    }

}
