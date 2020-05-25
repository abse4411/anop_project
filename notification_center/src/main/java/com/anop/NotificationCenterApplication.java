package com.anop;
import com.anop.service.RemoteService;
import feign.FeignException;
import org.mybatis.spring.annotation.MapperScan;
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
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.resource.HttpResource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.net.HttpRetryException;
import java.security.Principal;
import java.util.*;

/**
 * 通知微服务启动类
 *
 * @author Xue_Feng
 */
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.anop.mapper")
@RestController
public class NotificationCenterApplication {
    private static final Logger logger= LoggerFactory.getLogger(NotificationCenterApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(NotificationCenterApplication.class, args);
    }

    @PostMapping("/hello1")
    public String hello(){
        return "Hello world!-NotificationCenterApplication";
    }

    @GetMapping("/user")
    public Message user(Principal principal) {
        return new Message(principal.getName());
    }

    @PostMapping("/test")
    public Message test(Principal principal) {
        return new Message(principal.getName());
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
        for (int i = 0; i < cc.length; i++) {
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

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }


    class Message {
        private String id = UUID.randomUUID().toString();
        private String content;

        public Message(String content) {
            this.content = content;
        }

        public Message() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Autowired
    RemoteService remoteService;

    @GetMapping("/hello3")
    public Object hello3() {
        String result = null;
        result = remoteService.hello("你好");
        return "Hello world!-NotificationCenterApplication" + result;
    }

    @GetMapping("/hello4")
    public Object hello4() {
        String result = null;
        result = remoteService.hello(null);
        return "Hello world!-NotificationCenterApplication" + result;
    }
}

