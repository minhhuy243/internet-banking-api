package com.internetbanking.controller;

import com.internetbanking.request.DebtReminderRequest;
import com.internetbanking.service.DebtReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debt-reminders")
@RequiredArgsConstructor
public class DebtReminderController {

    private final DebtReminderService debtReminderService;

    @GetMapping
    public ResponseEntity<?> getDebtReminderList(@RequestParam(value = "createdByMyself", required = false) Boolean createdByMyself,
                                                 @PageableDefault(value = 20, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(debtReminderService.getDebtReminderList(createdByMyself, pageable));
    }

    @GetMapping("/{debtReminderId}")
    public ResponseEntity<?> getById(@PathVariable("debtReminderId") Long debtReminderId) {
        return ResponseEntity.ok(debtReminderService.getById(debtReminderId));
    }

    @PostMapping
    public ResponseEntity<?> getDebtReminderList(@RequestBody DebtReminderRequest request) {
        return ResponseEntity.ok(debtReminderService.create(request));
    }

    @PostMapping("/cancellation/{debtReminderId}")
    public ResponseEntity<?> cancelDebtReminder(@PathVariable("debtReminderId") Long debtReminderId,
                                                @RequestBody DebtReminderRequest request) {
        debtReminderService.cancelDebtReminder(debtReminderId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment/{debtReminderId}")
    public ResponseEntity<?> paymentDebtReminder(@PathVariable("debtReminderId") Long debtReminderId) {
        debtReminderService.payment(debtReminderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment/validate/{otpValue}")
    public ResponseEntity<?> validatePayment(@PathVariable("otpValue") Integer otpValue) {
        Object result = debtReminderService.validatePayment(otpValue);
        return ResponseEntity.ok(result);
    }
}
