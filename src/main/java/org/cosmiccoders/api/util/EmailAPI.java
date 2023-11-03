package org.cosmiccoders.api.util;

import org.cosmiccoders.api.security.SecurityConstants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component

public class EmailAPI {

    @Async
    public void send(String email, String username, String link) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Map<String, String> requestBody = Map.of(
                "email", email,
                "username", username,
                "link", link);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                SecurityConstants.FRONTEND_INTERNAL_URL + "/api/email",
                request,
                String.class
        );

        if (response.getStatusCode().isError()) {
            System.out.println(response);
        }
    }
}
