package com.example.springbatchflowcontrol.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ResourceLock {

    @Id
    private String resourceId;
    private String correlatedId;
    private String correlatedType;
    private Date createdTime;

}
