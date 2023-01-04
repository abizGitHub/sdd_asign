package com.abiz.controller;

import com.abiz.model.dto.TransactionDto;
import com.abiz.service.FinanceTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private FinanceTransactionService service;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<TransactionDto.Info>> get(@AuthenticationPrincipal String username,
                                                         @NotNull @PathVariable String accountNumber,
                                                         @Nullable @RequestParam Integer page,
                                                         @Nullable @RequestParam Integer size) {
        return new ResponseEntity<>(service.getList(username, accountNumber, page, size), HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{accountHolder}/{accountNumber}")
    public ResponseEntity<List<TransactionDto.Info>> getUsersTransaction(@NotNull @PathVariable String accountHolder,
                                                                         @NotNull @PathVariable String accountNumber,
                                                                         @Nullable @RequestParam Integer page,
                                                                         @Nullable @RequestParam Integer size) {
        return new ResponseEntity<>(service.getList(accountHolder, accountNumber, page, size), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TransactionDto.Info> transfer(@AuthenticationPrincipal String username,
                                                        @Valid @RequestBody TransactionDto dto) {
        TransactionDto.Info info = service.transfer(username, dto);
        return new ResponseEntity(info, HttpStatus.OK);
    }

}
