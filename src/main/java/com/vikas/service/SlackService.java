package com.vikas.service;

import com.vikas.exception.OnboardingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// CHANGED:
//  1. Added @Slf4j — replaces System.out.println with structured logging
//  2. Updated OnboardingException import path to com.vikas.exception (lowercase package)

@Slf4j
@Service
public class SlackService {

    /**
     * Simulates sending a Slack workspace invite to the new hire.
     * In production, replace the body with a real call to:
     * POST https://slack.com/api/users.admin.invite
     */
    public void sendWorkspaceInvite(String workEmail, String employeeName) {
        try {
            // --- Replace with real Slack API call ---
            log.info("[SlackService] Invite sent to {} for user: {}", workEmail, employeeName);

        } catch (Exception ex) {
            log.error("[SlackService] Failed to send Slack invite to: {}", workEmail, ex);
            throw new OnboardingException(
                    "SLACK_INVITE",
                    "Failed to send Slack invite to: " + workEmail,
                    ex
            );
        }
    }
}