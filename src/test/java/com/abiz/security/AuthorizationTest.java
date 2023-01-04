package com.abiz.security;


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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    protected final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static AccountDto accountDto1;
    static AccountDto accountDto2;

    String adminAuth = new String(Base64.getEncoder().encode("admin|admin".getBytes()));
    String user1Auth = new String(Base64.getEncoder().encode("user1|user1".getBytes()));
    String user2Auth = new String(Base64.getEncoder().encode("user2|user2".getBytes()));
    String invalidAuth1 = new String(Base64.getEncoder().encode("admin:admin".getBytes()));
    String invalidAuth2 = new String(Base64.getEncoder().encode("admin|some-thing-else".getBytes()));
    String invalidAuth3 = new String(Base64.getEncoder().encode("not-user|admin".getBytes()));

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
    void createAccount_without_Authorization() {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .content(mapper.writeValueAsBytes(accountDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void createAccount_with_invalid_Authorization() {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .header(HttpHeaders.AUTHORIZATION, invalidAuth1)
                        .content(mapper.writeValueAsBytes(accountDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void createAccount_with_Authorization_and_user_rule() {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .content(mapper.writeValueAsBytes(accountDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    void createAccount_with_Authorization_and_admin_role() {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .header(HttpHeaders.AUTHORIZATION, adminAuth)
                        .content(mapper.writeValueAsBytes(accountDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAccounts_without_Authorization() {
        mockMvc.perform(MockMvcRequestBuilders.get("/account")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void getAccounts_with_Authorization_user() {
        mockMvc.perform(MockMvcRequestBuilders.get("/account")
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void blockAccount_without_Authorization() {
        mockMvc.perform(MockMvcRequestBuilders.put("/account/block/some-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.put("/account/unblock/some-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        AccountDto.Info account1 = accountService.open(accountDto1);

        mockMvc.perform(MockMvcRequestBuilders.put("/account/blockUserAccount/" + account1.getAccountNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.put("/account/unblockUserAccount/" + account1.getAccountNumber())
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    void blockAccount_with_user_Authorization() {
        AccountDto.Info account1 = accountService.open(accountDto1);

        mockMvc.perform(MockMvcRequestBuilders.put("/account/block/" + account1.getAccountNumber())
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Account blockedAccount = accountService.getByAccountNumber(account1.getAccountNumber());
        Assertions.assertTrue(blockedAccount.isBlocked());

        mockMvc.perform(MockMvcRequestBuilders.put("/account/unblock/" + account1.getAccountNumber())
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Account unblockedAccount = accountService.getByAccountNumber(account1.getAccountNumber());
        Assertions.assertFalse(unblockedAccount.isBlocked());

        mockMvc.perform(MockMvcRequestBuilders.put("/account/blockUserAccount/" + account1.getAccountNumber())
                        .header(HttpHeaders.AUTHORIZATION, adminAuth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        blockedAccount = accountService.getByAccountNumber(account1.getAccountNumber());
        Assertions.assertTrue(blockedAccount.isBlocked());
    }

    @Test
    @SneakyThrows
    void getTransactions_with_Authorization_user() {
        AccountDto.Info accountInfo = accountService.open(accountDto1);
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/" + accountInfo.getAccountNumber())
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getTransactions_without_Authorization() {
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/some-number")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        AccountDto.Info account2 = accountService.open(accountDto2);

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/"+account2.getAccountNumber())
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    void createTransactions_without_Authorization() {

        AccountDto.Info account1 = accountService.open(accountDto1);
        AccountDto.Info account2 = accountService.open(accountDto2);
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(500d)
                .description("test-desc")
                .operatorName("test-operator")
                .transferorAccountNumber(account1.getAccountNumber())
                .transfereeAccountNumber(account2.getAccountNumber())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user2Auth)
                        .content(mapper.writeValueAsBytes(transactionDto)))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(transactionDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void createTransactions_with_Authorization() {

        AccountDto.Info account1 = accountService.open(accountDto1);
        AccountDto.Info account2 = accountService.open(accountDto2);
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(250d)
                .description("test-desc")
                .operatorName("test-operator")
                .transferorAccountNumber(account1.getAccountNumber())
                .transfereeAccountNumber(account2.getAccountNumber())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .content(mapper.writeValueAsBytes(transactionDto)))
                .andExpect(status().isOk());
    }


}
