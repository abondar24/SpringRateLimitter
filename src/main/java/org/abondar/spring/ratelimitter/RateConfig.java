package org.abondar.spring.ratelimitter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class RateConfig extends WebMvcConfigurationSupport {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var rateInterceptor = new RateInterceptor(rateLimitter());
        registry.addInterceptor(rateInterceptor);

    }

    @Value("${controller.package:''}")
    private String controllerPackage;

    @Bean
    public RateLimitter rateLimitter(){
        return new RateLimitter(controllerPackage);
    }

}
