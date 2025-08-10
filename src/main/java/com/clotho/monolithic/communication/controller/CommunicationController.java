package com.clotho.monolithic.communication.controller;

import com.clotho.monolithic.communication.dto.CommunicationDTO;
import com.clotho.monolithic.communication.dto.ReplyDto;
import com.clotho.monolithic.communication.model.Communication;
import com.clotho.monolithic.communication.service.CommunicationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService communicationService;

    /**
     * Endpoint for users to submit a contact form.
     * This is public and does not require authentication.
     */
    @PostMapping("/send")
    public ResponseEntity<String> receiveMessage(@RequestBody CommunicationDTO communicationDto) {
        communicationService.saveMessage(communicationDto);
        return ResponseEntity.ok("Message received. We will get back to you shortly.");
    }

    /**
     * Endpoint for admins to fetch all messages.
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Communication>> getAllMessages() {
        return ResponseEntity.ok(communicationService.getAllMessages());
    }

    /**
     * Endpoint for admins to reply to a specific message.
     */
    @PostMapping("/reply/{messageId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> replyToMessage(@PathVariable Long messageId, @RequestBody ReplyDto replyDto) {
        try {
            communicationService.sendReply(messageId, replyDto);
            return ResponseEntity.ok("Reply sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send reply: " + e.getMessage());
        }
    }
}

// --- DTO Classes ---




