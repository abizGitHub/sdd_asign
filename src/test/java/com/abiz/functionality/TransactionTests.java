package com.abiz.functionality;


import com.abiz.model.Account;
import com.abiz.model.AccountType;
import com.abiz.model.dto.AccountDto;
import com.abiz.model.dto.TransactionDto;
import com.abiz.service.AccountService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Base64;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class TransactionTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    protected final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static AccountDto accountDto1;
    static AccountDto accountDto2;

    String user1Auth = new String(Base64.getEncoder().encode("user1|user1".getBytes()));
    String user2Auth = new String(Base64.getEncoder().encode("user2|user2".getBytes()));

    @BeforeAll
    static void init() {
        accountDto1 = AccountDto.builder()
                .accountHolder("user1")
                .accountType(AccountType.SAVING)
                .balance(1000)
                .build();
        accountDto2 = AccountDto.builder()
                .accountHolder("user2")
                .accountType(AccountType.SAVING)
                .balance(2000)
                .build();
    }

    @Test
    @SneakyThrows
    void getTransactions() {
        AccountDto.Info info = accountService.open(accountDto1);

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/" + info.getAccountNumber())
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]['amount']").value(1000));

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/" + info.getAccountNumber())
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .queryParam("size", "10")
                        .queryParam("page", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @SneakyThrows
    void transfer() {
        AccountDto.Info account1 = accountService.open(accountDto1);
        AccountDto.Info account2 = accountService.open(accountDto2);

        TransactionDto transaction1 = TransactionDto.builder()
                .amount(250d)
                .description("user 1 -> 2")
                .operatorName("test-operator")
                .transferorAccountNumber(account1.getAccountNumber())
                .transfereeAccountNumber(account2.getAccountNumber())
                .build();

        TransactionDto transaction2 = TransactionDto.builder()
                .amount(50d)
                .description("user 1 -> 2")
                .operatorName("test-operator")
                .transferorAccountNumber(account1.getAccountNumber())
                .transfereeAccountNumber(account2.getAccountNumber())
                .build();

        TransactionDto transaction3 = TransactionDto.builder()
                .amount(10d)
                .description("user 2 -> 1")
                .operatorName("test-operator")
                .transferorAccountNumber(account2.getAccountNumber())
                .transfereeAccountNumber(account1.getAccountNumber())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .content(mapper.writeValueAsBytes(transaction1)))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .content(mapper.writeValueAsBytes(transaction2)))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user2Auth)
                        .content(mapper.writeValueAsBytes(transaction3)))
                .andExpect(status().isOk());

        Account account1AfterTransfer = accountService.getByAccountNumber(account1.getAccountNumber());
        Account account2AfterTransfer = accountService.getByAccountNumber(account2.getAccountNumber());

        Assertions.assertEquals(
                account1.getBalance()
                        - 250
                        - 50
                        + 10
                , account1AfterTransfer.getBalance());

        Assertions.assertEquals(
                account2.getBalance()
                        + 250
                        + 50
                        - 10
                , account2AfterTransfer.getBalance());
    }


    @Test
    @SneakyThrows
    void transfer_more_than_balance_or_blocked() {
        AccountDto.Info account1 = accountService.open(accountDto1);
        AccountDto.Info account2 = accountService.open(accountDto2);

        TransactionDto transaction1 = TransactionDto.builder()
                .amount(account1.getBalance() + 0.1)
                .description("user 1 -> 2")
                .operatorName("test-operator")
                .transferorAccountNumber(account1.getAccountNumber())
                .transfereeAccountNumber(account2.getAccountNumber())
                .build();

        accountService.block(account1.getAccountNumber());

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .content(mapper.writeValueAsBytes(transaction1)))
                .andExpect(status().isNotAcceptable());

        accountService.unblock(account1.getAccountNumber());

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .content(mapper.writeValueAsBytes(transaction1)))
                .andExpect(status().isNotAcceptable());

        transaction1.setAmount(account1.getBalance() - 0.1);

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .content(mapper.writeValueAsBytes(transaction1)))
                .andExpect(status().isOk());

        accountService.block(account2.getAccountNumber());

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .content(mapper.writeValueAsBytes(transaction1)))
                .andExpect(status().isNotAcceptable());
    }


}
