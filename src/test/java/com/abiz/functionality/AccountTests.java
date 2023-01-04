package com.abiz.functionality;


import com.abiz.model.AccountType;
import com.abiz.model.dto.AccountDto;
import com.abiz.service.AccountService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class AccountTests {

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
    void openAccount() {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                        .header(HttpHeaders.AUTHORIZATION, adminAuth)
                        .content(mapper.writeValueAsBytes(accountDto1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").isNotEmpty())
                .andExpect(jsonPath("$.accountHolder").value("user1"))
                .andExpect(jsonPath("$.accountType").value(AccountType.SAVING))
                .andExpect(jsonPath("$.balance").value(1000));
    }


    @Test
    @SneakyThrows
    void getAccounts() {
        accountService.open(accountDto1);
        accountService.open(accountDto1);
        accountService.open(accountDto1);
        accountService.open(accountDto1);

        mockMvc.perform(MockMvcRequestBuilders.get("/account")
                        .header(HttpHeaders.AUTHORIZATION, user1Auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0]['accountHolder']").value("user1"))
                .andExpect(jsonPath("$[1]['accountHolder']").value("user1"))
                .andExpect(jsonPath("$[2]['accountHolder']").value("user1"))
                .andExpect(jsonPath("$[3]['accountHolder']").value("user1"));
    }

}
