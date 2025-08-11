package com.clotho.monolithic.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    // Inject the path. If not found, default to an empty string to prevent a crash.
    @Value("${FIREBASE_SECRET_PATH:}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public void initialize() {
        try {
            // Add a check to see if the path was actually provided.
            if (serviceAccountKeyPath == null || serviceAccountKeyPath.isEmpty()) {
                System.err.println("WARNING: FIREBASE_SECRET_PATH environment variable not set. Firebase Admin SDK will not be initialized.");
                return; // Exit gracefully instead of crashing the application.
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase from path: " + serviceAccountKeyPath + " - " + e.getMessage());
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
