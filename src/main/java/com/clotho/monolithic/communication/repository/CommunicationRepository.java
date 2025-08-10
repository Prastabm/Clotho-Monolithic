package com.clotho.monolithic.communication.repository;

import com.clotho.monolithic.communication.model.Communication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunicationRepository extends JpaRepository<Communication, Long> {
}
