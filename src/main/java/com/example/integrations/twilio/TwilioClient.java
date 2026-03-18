package com.example.integrations.twilio;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;

/**
 * Legacy Twilio integration using deprecated patterns.
 *
 * DEPRECATED PATTERNS:
 * 1. Twilio.init(sid, token) — global static initialisation deprecated in v9+
 *    (new: TwilioRestClient client = new TwilioRestClient.Builder(sid, token).build())
 * 2. new PhoneNumber(string) — com.twilio.type.PhoneNumber deprecated in v9;
 *    pass String directly to builder
 * 3. Message.creator(PhoneNumber, PhoneNumber, String) — positional args deprecated
 * 4. Call.creator(PhoneNumber, PhoneNumber, URI) — same positional arg deprecation
 * 5. Message.fetcher(messageSid) — single-arg form deprecated; use fetcher(accountSid, messageSid)
 * 6. message.getStatus() returned String before v9; now returns enum Message.Status
 */
@Service
public class TwilioClient {

    @Value("${twilio.account.sid:ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}")
    private String accountSid;

    @Value("${twilio.auth.token:your_auth_token}")
    private String authToken;

    @Value("${twilio.from.number:+15551234567}")
    private String fromNumber;

    private TwilioRestClient twilioRestClient;

    @PostConstruct
    public void init() {
        // Recommended v9+ initialization: create and use a TwilioRestClient instance
        this.twilioRestClient = new TwilioRestClient.Builder(accountSid, authToken).build();
    }

    /**
     * Uses recommended v9+ Message.creator builder pattern with String parameters.
     */
    public String sendSms(String toNumber, String body) {
        Message message = Message.creator(toNumber)
                .setFrom(fromNumber)
                .setBody(body)
                .setClient(this.twilioRestClient)
                .create();

        return message.getSid();
    }

    /**
     * Uses recommended v9+ Call.creator builder pattern with String parameters.
     */
    public String makeCall(String toNumber, String callbackUrl) {
        Call call = Call.creator(toNumber)
                .setFrom(fromNumber)
                .setUrl(URI.create(callbackUrl))
                .setClient(this.twilioRestClient)
                .create();

        return call.getSid();
    }

    /**
     * Uses recommended v9+ Message.fetcher with accountSid and messageSid.
     */
    public String getMessageStatus(String messageSid) {
        Message message = Message.fetcher(accountSid, messageSid)
                .setClient(this.twilioRestClient)
                .fetch();
        return message.getStatus().toString();
    }

    /**
     * Uses recommended v9+ Message.deleter with accountSid and messageSid.
     */
    public boolean deleteMessage(String messageSid) {
        return Message.deleter(accountSid, messageSid)
                .setClient(this.twilioRestClient)
                .delete();
    }
}
