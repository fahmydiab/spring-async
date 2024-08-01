package org.example.async_demo.service;

import org.example.async_demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class LookupService {

    private static final Logger logger = LoggerFactory.getLogger(LookupService.class);
    private static final String GITHUB_USER_URL = "https://api.github.com/users/%s";
    private final RestTemplate restTemplate;

    public LookupService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<User> findUser(String user) throws InterruptedException {
        logger.info("Looking up "+ user);
        String url = String.format(GITHUB_USER_URL, user);
        try {
            ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);
            User results = response.getBody();
            Thread.sleep(400L);
            return CompletableFuture.completedFuture(results);
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("User not found: " + user, e);
            // Return null or throw a custom exception as needed
            return CompletableFuture.completedFuture(null);
        } catch (HttpClientErrorException e) {
            logger.error("Error looking up user: " + user, e);
            // Handle other HTTP errors here if necessary
            return CompletableFuture.completedFuture(null);
        }
    }
}
