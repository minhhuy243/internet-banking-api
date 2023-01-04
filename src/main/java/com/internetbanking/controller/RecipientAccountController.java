package com.internetbanking.controller;

import com.internetbanking.entity.RecipientAccount;
import com.internetbanking.request.RecipientAccountRequest;
import com.internetbanking.service.RecipientAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<?> getByAccountId(@PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(recipientAccountService.getByAccountId(pageable));
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody RecipientAccountRequest request) {
        Object result = recipientAccountService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid RecipientAccountRequest request) {
        Object result = recipientAccountService.update(id, request);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        recipientAccountService.delete(id);
        return ResponseEntity.ok().build();
    }
}
