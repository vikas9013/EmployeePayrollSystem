package com.vikas.service;

import com.vikas.ExceptionHandler.OnboardingException;
import org.springframework.stereotype.Service;

@Service
public class SlackService {

    /**
     * Simulates sending a Slack workspace invite to the new hire.
     * In production, replace this body with a real call to the
     * Slack Web API: POST https://slack.com/api/users.admin.invite
     *
     * @param workEmail the employee's newly created work email
     * @param employeeName the employee's display name
     * @throws OnboardingException if the Slack invite fails
     */
    public void sendWorkspaceInvite(String workEmail, String employeeName) {
        try {
            // --- Replace with real Slack API call ---
            System.out.println("[SlackService] Invite sent to " + workEmail
                    + " for user: " + employeeName);

        } catch (Exception ex) {
            throw new OnboardingException(
                    "SLACK_INVITE",
                    "Failed to send Slack invite to: " + workEmail,
                    ex
            );
        }
    }
}
