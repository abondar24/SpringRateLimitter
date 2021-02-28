package org.abondar.spring.ratelimitter;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class RateLimitter {

    private final Logger logger = Logger.getLogger("logger");
    private final ConcurrentMap<String, RateObj> methodLimits = new ConcurrentHashMap<>();
    private final AtomicInteger requests = new AtomicInteger();

    private RateObj overallLimit;

    private long startBlock;

    private String controllerPackage;

    public RateLimitter (String controllerPackage){
        this.controllerPackage = controllerPackage;

        init();
    }

    private void init() {
        try {
            var ctrls = getControllers();
            for (Class<?> ctrl : ctrls) {
                initController(ctrl);
            }
        } catch (ClassNotFoundException ex) {
            logger.info("Controller not found");
        }

    }

    private List<Class<?>> getControllers() throws ClassNotFoundException{
        List<Class<?>> ctrlList = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(true);

        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

        for (BeanDefinition bd : scanner.findCandidateComponents(controllerPackage)){
            ctrlList.add(Class.forName(bd.getBeanClassName()));
        }

        return ctrlList;
    }

    private void initController(Class<?> ctrl){
        var rateAnnotation = ctrl.getAnnotation(RateLimit.class);
        if (rateAnnotation != null) {
            overallLimit = new RateObj(rateAnnotation.requests(), rateAnnotation.period());
        } else {
            overallLimit = new RateObj(1000,1L);
        }

        var startPath = "";
        var requestAnnotation = ctrl.getAnnotation(RequestMapping.class);
        if (requestAnnotation!=null && requestAnnotation.value().length!=0){
            startPath = requestAnnotation.value()[0];
        }

        var methods = ctrl.getMethods();
        for (Method method : methods) {

            var rateAnn = method.getAnnotation(RateLimit.class);
            if (rateAnn != null) {
                var methodRate = new RateObj(rateAnn.requests(), rateAnn.period());


                if (method.getAnnotation(GetMapping.class) != null) {
                    var path = startPath + method.getAnnotation(GetMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(PostMapping.class) != null) {
                    var path = startPath + method.getAnnotation(PostMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(PutMapping.class) != null) {
                    var path = startPath + method.getAnnotation(PutMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(DeleteMapping.class) != null) {
                    var path = startPath + method.getAnnotation(DeleteMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(PatchMapping.class) != null) {
                    var path = method.getAnnotation(PatchMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }

                if (method.getAnnotation(RequestMapping.class) != null) {
                    var path = startPath + method.getAnnotation(RequestMapping.class).path()[0];
                    methodLimits.put(path, methodRate);
                }
            }
        }
    }

    public void rateLimit(String requestURI) throws RateLimitException{
        requests.getAndIncrement();

        if (requests.get()==1){
            startBlock = System.currentTimeMillis();
        }

        RateObj activeLimit;
        if (methodLimits.containsKey(requestURI)) {
            activeLimit = methodLimits.get(requestURI);
        } else {
            activeLimit = overallLimit;
        }

        if (requests.get() > activeLimit.getLimit()) {
            if (System.currentTimeMillis()-startBlock>activeLimit.getPeriod()){
                requests.set(0);
            } else {
                throw new RateLimitException();
            }

        }
    }

}
