package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.Sipd;
import com.multipolar.uiteller.backend.service.MiddlewareGatewayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/cetakulang")
public class CetakUlangSipdController {
    @Value("${sipd.server.url}")
    private String sipdUrl;

    private RestTemplate restTemplate;
    private static Logger logger = LoggerFactory.getLogger(MiddlewareGatewayServiceImpl.class);

    @PostMapping("/sipd")
    public Sipd CetakUlangSipdController(@RequestBody String id) throws IOException {
        System.out.println("MASUK");
//        Sipd response = getResponse(id);
        String urls = sipdUrl+"?billing="+id;
        System.out.println("URL "+urls);
        String[] listId = id.split(";");
        String auth = getBasicAuthenticationHeader("tellerAdmin", "tell3R123");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", auth);
        logger.info("AUTH : "+auth);
        logger.info("HEADERS : "+headers);

        Map<String, String> map = new HashMap<>();
        HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
        ResponseEntity<Sipd> response = restTemplate.exchange(urls, HttpMethod.GET, entity, Sipd.class);
        return response.getBody();
    }
    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + DatatypeConverter.printBase64Binary(valueToEncode.getBytes());
    }
    private String buildJsonDetailRequestBodySipd(String s, String id) throws IOException {
//        Rule rule = getRule();
        System.out.println("JSONNNNN"+ UriComponentsBuilder.fromUriString(s).buildAndExpand(id).toUriString());
        return UriComponentsBuilder.fromUriString(s).buildAndExpand(id).toUriString();
    }
    public Sipd getResponse(String id) throws IOException {
        String urls = sipdUrl+"?billing="+id;
        System.out.println("URL "+urls);
        String[] listId = id.split(";");
        String auth = getBasicAuthenticationHeader("tellerAdmin", "tell3R123");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", auth);
        logger.info("AUTH : "+auth);
        logger.info("HEADERS : "+headers);

        Map<String, String> map = new HashMap<>();
        HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map, headers);
        ResponseEntity<Sipd> response = restTemplate.exchange(urls, HttpMethod.GET, entity, Sipd.class);
        return response.getBody();
    }

}

