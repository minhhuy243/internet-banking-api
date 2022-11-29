package com.internetbanking.controller;

import com.internetbanking.entity.RecipientAccount;
import com.internetbanking.request.RecipientAccountCreateRequest;
import com.internetbanking.request.RecipientAccountUpdateRequest;
import com.internetbanking.service.RecipientAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/recipient-accounts")
@RequiredArgsConstructor
public class RecipientAccountController {

    private final RecipientAccountService recipientAccountService;

    @GetMapping("")
    public ResponseEntity<?> getByAccountId() {
        return ResponseEntity.ok(recipientAccountService.getByAccountId());
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody @Valid RecipientAccountCreateRequest request) {
        try {
            RecipientAccount recipientAccount = recipientAccountService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(recipientAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid RecipientAccountUpdateRequest request) {
        try {
            RecipientAccount recipientAccount = recipientAccountService.update(id, request);
            return ResponseEntity.ok(recipientAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            recipientAccountService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
