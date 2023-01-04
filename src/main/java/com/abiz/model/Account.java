package com.abiz.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private double balance;
    @Column
    private String accountHolder;
    @Enumerated(EnumType.ORDINAL)
    @Column
    private AccountType accountType;
    @Column
    private Date createDate;
    @Column(unique = true)
    private String accountNumber;
    @Column
    private boolean blocked;
}

