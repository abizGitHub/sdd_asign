package com.abiz.controller;

import com.abiz.model.dto.AccountDto;
import com.abiz.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<AccountDto.Info> create(@Valid @RequestBody AccountDto dto) {
        return new ResponseEntity(accountService.open(dto), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<String> getByUser(@AuthenticationPrincipal String username,
                                            @Nullable @RequestParam Integer page,
                                            @Nullable @RequestParam Integer size) {
        return new ResponseEntity(accountService.getList(username, page, size), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/block/{accountNumber}")
    public ResponseEntity blockAccount(@AuthenticationPrincipal String username,
                                       @PathVariable String accountNumber) {
        accountService.block(username,accountNumber);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/unblock/{accountNumber}")
    public ResponseEntity unblockAccount(@AuthenticationPrincipal String username,
                                         @PathVariable String accountNumber) {
        accountService.unblock(username,accountNumber);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/blockUserAccount/{accountNumber}")
    public ResponseEntity blockUserAccount(@PathVariable String accountNumber) {
        accountService.block(accountNumber);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/unblockUserAccount/{accountNumber}")
    public ResponseEntity<String> unblockUserAccount(@PathVariable String accountNumber) {
        accountService.unblock(accountNumber);
        return ResponseEntity.ok().build();
    }

}
