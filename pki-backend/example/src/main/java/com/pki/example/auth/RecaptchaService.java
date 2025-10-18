package com.pki.example.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecaptchaService {
    private final String secretKey = "6Leh5u0rAAAAAMr0b2ARwaVRarGWN9u9Iu6kZSpu"; // iz Google reCAPTCHA konzole
    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verify(String recaptchaToken) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", recaptchaToken);

        RecaptchaResponse response = restTemplate.postForObject(VERIFY_URL, params, RecaptchaResponse.class);
        return response != null && response.isSuccess();
    }

    static class RecaptchaResponse {
        @Setter
        @Getter
        private boolean success;
        private List<String> errorCodes;
    }
}
