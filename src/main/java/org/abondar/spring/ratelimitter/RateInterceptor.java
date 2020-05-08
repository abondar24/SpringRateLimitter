package org.abondar.spring.ratelimitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RateInterceptor implements HandlerInterceptor {


    private final RateLimitter rateLimitter;

    @Autowired
    public RateInterceptor(RateLimitter rateLimitter) {
        this.rateLimitter = rateLimitter;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        rateLimitter.rateLimit(request.getRequestURI());
        return true;
    }


}
