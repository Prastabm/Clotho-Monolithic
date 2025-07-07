package com.clotho.monolithic.product.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.io.OutputStream;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private String uploadUrl;

    @PostConstruct
    public void init() {
        this.uploadUrl = supabaseUrl + "/storage/v1/object";
    }

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String objectPath = bucketName + "/" + fileName;
            URL url = new URL(uploadUrl + "/" + objectPath);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
            conn.setRequestProperty("Content-Type", file.getContentType());
            conn.setRequestProperty("x-upsert", "true");

            conn.getOutputStream().write(file.getBytes());

            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                return supabaseUrl + "/storage/v1/object/public/" + objectPath;
            } else {
                // Read and log the error body
                InputStream errorStream = conn.getErrorStream();
                String errorMsg = new BufferedReader(new InputStreamReader(errorStream))
                        .lines().collect(Collectors.joining("\n"));
                log.error("Supabase upload failed ({}): {}", responseCode, errorMsg);
                return null;
            }
        } catch (Exception e) {
            log.error("Exception during Supabase upload", e);
            return null;
        }
    }

}
