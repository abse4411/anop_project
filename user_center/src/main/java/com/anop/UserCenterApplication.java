package com.anop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@RestController
public class UserCenterApplication {
    private static final Logger logger= LoggerFactory.getLogger(UserCenterApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }

    @GetMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/")
    public ResponseEntity<String> home(HttpServletRequest request) {
        final String romteUrl="http://AUTH-SERVICE/test";
        HttpHeaders header = new HttpHeaders();
        List<String> cookies = new ArrayList<>();
        Cookie[] cc = request.getCookies();
        HashMap<String, Object> body = new HashMap<>();
        for (int i = 0; i < cc.length; i++)
        {
            cookies.add(cc[i].getName() + "=" + cc[i].getValue());
            System.out.println(cc[i].getName() + "=" + cc[i].getValue());
            if(cc[i].getName().equals("XSRF-TOKEN")){
                List<String> csrfToken = new ArrayList<>(1);
                csrfToken.add(cc[i].getValue());
                header.put("X-XSRF-TOKEN",csrfToken);
                System.out.println("X-XSRF-TOKEN Found");
            }
        }
        //需要携带Cookie信息，用于Session共享
        header.put(HttpHeaders.COOKIE, cookies);
        ResponseEntity<String> entity=null;
        HttpEntity httpEntity = new HttpEntity(body, header);
        try{
            entity = restTemplate.exchange(romteUrl, HttpMethod.POST, httpEntity, String.class);
        }catch (HttpClientErrorException e){
            entity=new ResponseEntity<>(e.getResponseBodyAsString(), e.getResponseHeaders(),e.getStatusCode());
            e.printStackTrace();
            logger.warn(e.getMessage());
        }
        return entity;
    }

    @PostMapping("/hello1")
    public String hello(){
        return "Hello world!-NotificationCenterApplication";
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

}
