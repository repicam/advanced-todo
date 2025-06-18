package es.myprojects.notification_email_service.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${resend.api-key}") String resendApiKey) {
        this.resend = new Resend(resendApiKey);
    }

    public boolean sendEmail(String to, String subject, String htmlContent) {
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from("onboarding@resend.dev")
                    .to(to)
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            CreateEmailResponse response = resend.emails().send(request);
            log.info("Email sent successfully. Email ID: {}", response.getId());
            return true;
        } catch (ResendException e) {
            log.error("Failed to send email to {}. Error: {}", to, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending email to {}. Error: {}", to, e.getMessage(), e);
            return false;
        }
    }
}
