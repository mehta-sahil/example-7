package com.example.integrations.sendgrid;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Legacy SendGrid integration using deprecated patterns.
 *
 * DEPRECATED PATTERNS:
 * 1. new Email(address) without display name — still works but deprecated
 *    in favour of new Email(address, name) for better RFC compliance
 * 2. new Content("text/plain", body) — old-style Content constructor;
 *    v4.9+ uses Content.builder()
 * 3. Mail(from, subject, to, content) 4-arg constructor — deprecated;
 *    use Mail.Builder instead
 * 4. Personalization.addTo(Email) — deprecated in favour of Personalization.addDynamicTemplateData()
 *    for template-based emails
 * 5. sendgrid.api(request) — old API call style; v4.10+ uses sendgrid.send(mail) directly
 * 6. response.getStatusCode() instead of checking response.getBody() for errors
 */
@Service
public class SendGridClient {

    @Value("${sendgrid.api.key:SG.placeholder}")
    private String apiKey;

    /**
     * DEPRECATED: 4-arg Mail constructor and old Content constructor.
     * Should use Mail.Builder and Content.Builder instead.
     */
    public int sendLegacyEmail(String fromAddr, String toAddr, String subject, String body)
            throws IOException {
        Email from = new Email(fromAddr);       // deprecated — should include display name
        Email to   = new Email(toAddr);         // deprecated — should include display name
        Content content = new Content("text/plain", body);  // deprecated constructor

        // DEPRECATED: 4-arg constructor — use Mail.Builder
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        // DEPRECATED: sg.api(request) — use sg.send(mail) in modern SendGrid Java
        Response response = sg.api(request);
        return response.getStatusCode();
    }

    /**
     * DEPRECATED: Personalization.addTo() without dynamic template data.
     * Modern pattern uses dynamic templates with Personalization.addDynamicTemplateData().
     */
    public int sendWithPersonalization(String fromAddr, String toAddr, String subject)
            throws IOException {
        Mail mail = new Mail();
        mail.setFrom(new Email(fromAddr));    // deprecated no-name Email
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(new Email(toAddr));  // deprecated addTo with no display name
        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);  // deprecated sg.api() call
        return response.getStatusCode();
    }
}
