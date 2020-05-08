package org.abondar.spring.ratelimitter;

public class RateLimitException extends Exception{


    public RateLimitException(){
        super("Exceeded the allowed number of requests");
    }
}
