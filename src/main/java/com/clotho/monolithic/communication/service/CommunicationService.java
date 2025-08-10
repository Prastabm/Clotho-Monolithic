package com.clotho.monolithic.communication.service;

import com.clotho.monolithic.communication.dto.CommunicationDTO;
import com.clotho.monolithic.communication.dto.ReplyDto;
import com.clotho.monolithic.communication.model.Communication;
import com.clotho.monolithic.communication.repository.CommunicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunicationService {

    private final CommunicationRepository communicationRepository;
    private final EmailService emailService;

    public void saveMessage(CommunicationDTO communicationDto) {
        Communication communication = Communication.builder()
                .customerName(communicationDto.getName())
                .customerEmail(communicationDto.getEmail())
                .message(communicationDto.getMessage())
                .receivedDate(LocalDateTime.now())
                .replied(false)
                .build();
        communicationRepository.save(communication);
    }

    public List<Communication> getAllMessages() {
        return communicationRepository.findAll();
    }

    @Transactional
    public void sendReply(Long messageId, ReplyDto replyDto) {
        Communication communication = communicationRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));

        String subject = "RE: Your inquiry to Clotho";
        emailService.sendEmail(communication.getCustomerEmail(), subject, replyDto.getReplyMessage());

        // Update the original message with the reply and mark it as replied
        communication.setReply(replyDto.getReplyMessage());
        communication.setReplied(true);
        communicationRepository.save(communication);
    }
}
