## Spring rate limitter

Small rate limitting library for rest controllers.

Idea: you can block your endpoints after some requests for specified period of time

# Usage
1. Add a depdendency to your maven project

```yaml
         <dependency>
            <groupId>io.github.abondar24</groupId>
            <artifactId>SpringRateLimitter</artifactId>
            <version>0.0.2-SNAPSHOT</version>
         </dependency>

```
2. Import RateConfig to your config or SpringBootApplication

```yaml
@Import({RateConfig.class})
``` 

3. Add property controller.package to your applicaiton.yml or application.properties
```yaml
controller:
  package: package-name
```

4. Add an exception handler for RateLimitException. 

5. Annotate controller or separate methods. 
Number of requests and period im milliseconds can be specified. 
Defalut values are 1000 requests and 1ms

# Build
```yaml
mvn clean install
```

# Versions

- 0.0.1 - basic rate limitter
- 0.0.2 - added check for rate values
