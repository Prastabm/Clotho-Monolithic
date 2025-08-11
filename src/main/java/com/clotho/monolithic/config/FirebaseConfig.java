package com.clotho.monolithic.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream; // <-- IMPORTANT: Use FileInputStream
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    // Inject the file path from the environment variable you just created
    @Value("${FIREBASE_SECRET_PATH}")
    private String serviceAccountKeyPath;

    @PostConstruct
    public void initialize() {
        try {
            // Check if Firebase app is already initialized
            if (FirebaseApp.getApps().isEmpty()) {

                // Read the file from the absolute path provided by the environment variable
                FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized successfully");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            // It's good practice to wrap the original exception
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
}
