package com.example.springbatchflowcontrol.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @Id
    private String id;

    @Lob
    private String detail;
}
