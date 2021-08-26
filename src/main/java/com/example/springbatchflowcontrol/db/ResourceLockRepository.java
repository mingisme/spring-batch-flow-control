package com.example.springbatchflowcontrol.db;

import com.example.springbatchflowcontrol.db.model.ResourceLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceLockRepository extends JpaRepository<ResourceLock, String> {

    int countByResourceIdAndCorrelatedId(String resourceId, String correlatedId);

}
