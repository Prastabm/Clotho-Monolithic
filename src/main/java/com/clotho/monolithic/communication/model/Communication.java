package com.clotho.monolithic.communication.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "communications")
public class Communication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime receivedDate;

    @Column(columnDefinition = "TEXT")
    private String reply;

    private boolean replied;
}
