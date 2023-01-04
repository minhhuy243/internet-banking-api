package com.internetbanking.controller;

import com.internetbanking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/info")
    public ResponseEntity<?> getInfo() {
        return ResponseEntity.ok(accountService.getInfo());
    }

    @GetMapping("")
    public ResponseEntity<?> getAll(@RequestParam Map<String, String> allParams,
                                    @PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(accountService.getAll(allParams, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        accountService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lock")
    public ResponseEntity<?> lock() {
        accountService.lock(null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lock/{id}")
    public ResponseEntity<?> lockById(@PathVariable("id") Long id) {
        accountService.lock(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlock() {
        accountService.unlock(null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unlock/{id}")
    public ResponseEntity<?> unlockById(@PathVariable("id") Long id) {
        accountService.unlock(id);
        return ResponseEntity.ok().build();
    }
}
