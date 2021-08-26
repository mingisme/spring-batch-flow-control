package com.example.springbatchflowcontrol.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferItem {

    private String from;
    private String to;
    private BigDecimal amount;

}
