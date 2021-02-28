package org.abondar.spring.ratelimitter;



import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SupportedAnnotationTypes(
        "org.abondar.spring.ratelimitter.RateLimit")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class RateProcessor extends AbstractProcessor {


    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {

        AtomicInteger counter = new AtomicInteger();
        annotations.forEach(annotation->{
            Set<? extends Element> annotatedElements
                    = roundEnv.getElementsAnnotatedWith(annotation);



            annotatedElements.forEach(ae->{
                var rl= ae.getAnnotation(RateLimit.class);
                var rate = new RateObj(rl.requests(),rl.period());

                if (rate.getLimit()<1){
                    processingEnv.getMessager()
                                   .printMessage(Diagnostic.Kind.ERROR, "Invalid limit value");
                }

                if (rate.getPeriod()<1){
                    processingEnv.getMessager()
                            .printMessage(Diagnostic.Kind.ERROR, "Invalid period value");
                }


            });


        });





        return false;
    }
}
