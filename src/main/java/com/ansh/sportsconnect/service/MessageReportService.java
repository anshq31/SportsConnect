package com.ansh.sportsconnect.service;

import com.ansh.sportsconnect.dto.MessageReportDto;
import com.ansh.sportsconnect.dto.MessageReportRequest;
import com.ansh.sportsconnect.model.chatEntities.ChatMessage;
import com.ansh.sportsconnect.model.reportEntities.MessageReport;
import com.ansh.sportsconnect.model.userAndAuthEntities.User;
import com.ansh.sportsconnect.repository.ChatMessageRepository;
import com.ansh.sportsconnect.repository.MessageReportRepository;
import com.ansh.sportsconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageReportService {

    private static final int HIDE_THRESHOLD = 3;

    @Autowired
    private MessageReportRepository messageReportRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
    }

    @Transactional
    public MessageReportDto createReport(Long messageId, MessageReportRequest request) {
        User reporter = getAuthenticatedUser();

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));

        if (messageReportRepository.existsByMessageIdAndReporterId(messageId, reporter.getId())) {
            throw new IllegalStateException("You have already reported this message");
        }

        MessageReport report = MessageReport.builder()
                .messageId(messageId)
                .reporterId(reporter.getId())
                .reason(request.getReason())
                .build();
        MessageReport saved = messageReportRepository.save(report);

        long reportCount = messageReportRepository.countByMessageId(messageId);
        if (reportCount >= HIDE_THRESHOLD && !message.isHidden()) {
            message.setHidden(true);
            chatMessageRepository.save(message);
        }

        return mapToDto(saved);
    }

    private MessageReportDto mapToDto(MessageReport report) {
        return MessageReportDto.builder()
                .id(report.getId())
                .messageId(report.getMessageId())
                .reporterId(report.getReporterId())
                .reason(report.getReason())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
