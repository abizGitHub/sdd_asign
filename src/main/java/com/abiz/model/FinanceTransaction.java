package com.abiz.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class FinanceTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Account transferorAccount;
    @ManyToOne
    private Account transfereeAccount;
    @Column
    private Double amount;
    @Column
    private Date transferDate;
    @Column
    private OperationType operationType;
    @Column
    private String operatorName;
    @Column
    private String description;
}
