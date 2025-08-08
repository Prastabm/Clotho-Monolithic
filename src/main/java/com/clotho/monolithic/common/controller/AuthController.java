package com.clotho.monolithic.common.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ListUsersPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${firebase.web.api.key}")
    private String firebaseWebApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/test-api-key")
    public ResponseEntity<?> testApiKey() {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=" + firebaseWebApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("idToken", "dummy-token"); // This will fail but show if API key works

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return ResponseEntity.ok("API key works");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("API key is invalid or Firebase Auth not enabled");
            }
            return ResponseEntity.ok("API key works (got different error: " + e.getMessage() + ")");
        }
    }
    @GetMapping("/user-count")
    public ResponseEntity<?> getUserCount() {
        try {
            ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
            int count = 0;
            while (page != null) {
                for (ExportedUserRecord user : page.getValues()) {
                    count++;
                }
                page = page.getNextPage();
            }
            return ResponseEntity.ok(Map.of("userCount", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user count: " + e.getMessage()));
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        // Validate input
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        // Check if API key is configured
        if (firebaseWebApiKey == null || firebaseWebApiKey.trim().isEmpty()) {
            System.err.println("Firebase Web API key is not configured");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Firebase Web API key not configured"));
        }

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + firebaseWebApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Empty for development

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("returnSecureToken", true);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Parse the response to extract relevant information
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("idToken", jsonResponse.get("idToken").asText());
            responseBody.put("email", jsonResponse.get("email").asText());
            responseBody.put("localId", jsonResponse.get("localId").asText());
            responseBody.put("refreshToken", jsonResponse.get("refreshToken").asText());

            return ResponseEntity.ok(responseBody);

        } catch (HttpClientErrorException e) {
            System.err.println("Signup failed with status: " + e.getStatusCode());
            System.err.println("Response: " + e.getResponseBodyAsString());

            try {
                JsonNode errorResponse = objectMapper.readTree(e.getResponseBodyAsString());
                JsonNode error = errorResponse.get("error");
                String errorMessage = error.get("message").asText();

                // Handle specific Firebase error codes
                String userFriendlyMessage = switch (errorMessage) {
                    case "EMAIL_EXISTS" -> "Email already exists";
                    case "INVALID_EMAIL" -> "Invalid email format";
                    case "WEAK_PASSWORD" -> "Password should be at least 6 characters";
                    case "OPERATION_NOT_ALLOWED" -> "Email/password accounts are not enabled";
                    default -> "Signup failed: " + errorMessage;
                };

                return ResponseEntity.status(e.getStatusCode())
                        .body(Map.of("error", userFriendlyMessage));

            } catch (Exception parseException) {
                return ResponseEntity.status(e.getStatusCode())
                        .body(Map.of("error", "Signup failed"));
            }

        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error occurred"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseWebApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("returnSecureToken", true);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("idToken", jsonResponse.get("idToken").asText());
            responseBody.put("email", jsonResponse.get("email").asText());
            responseBody.put("localId", jsonResponse.get("localId").asText());
            responseBody.put("refreshToken", jsonResponse.get("refreshToken").asText());

            return ResponseEntity.ok(responseBody);

        } catch (HttpClientErrorException e) {
            System.err.println("Login failed with status: " + e.getStatusCode());
            System.err.println("Response: " + e.getResponseBodyAsString());

            try {
                JsonNode errorResponse = objectMapper.readTree(e.getResponseBodyAsString());
                JsonNode error = errorResponse.get("error");
                String errorMessage = error.get("message").asText();

                String userFriendlyMessage = switch (errorMessage) {
                    case "EMAIL_NOT_FOUND" -> "Email not found";
                    case "INVALID_PASSWORD" -> "Invalid password";
                    case "USER_DISABLED" -> "User account has been disabled";
                    default -> "Login failed: " + errorMessage;
                };

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", userFriendlyMessage));

            } catch (Exception parseException) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials"));
            }

        } catch (Exception ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error occurred"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst().map(Object::toString).orElse("UNKNOWN");

        return ResponseEntity.ok(Map.of("email", email, "role", role));
    }
}