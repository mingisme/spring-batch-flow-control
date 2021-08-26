package com.example.springbatchflowcontrol.db;

import com.example.springbatchflowcontrol.db.model.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRequestRepository extends JpaRepository<TransferRequest, String> {
}
