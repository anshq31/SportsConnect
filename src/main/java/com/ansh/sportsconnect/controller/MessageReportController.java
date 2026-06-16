package com.ansh.sportsconnect.controller;

import com.ansh.sportsconnect.dto.MessageReportDto;
import com.ansh.sportsconnect.dto.MessageReportRequest;
import com.ansh.sportsconnect.service.MessageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageReportController {

    @Autowired
    private MessageReportService messageReportService;

    @PostMapping("/{messageId}/report")
    public ResponseEntity<?> reportMessage(
            @PathVariable Long messageId,
            @RequestBody MessageReportRequest request) {
        try {
            MessageReportDto report = messageReportService.createReport(messageId, request);
            return new ResponseEntity<>(report, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
