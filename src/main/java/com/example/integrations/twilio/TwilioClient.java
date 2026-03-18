package com.example.integrations.twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
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

    @PostConstruct
    public void init() {
        // DEPRECATED: Global static init.
        // v9+ recommendation: new TwilioRestClient.Builder(accountSid, authToken).build()
        Twilio.init(accountSid, authToken);
    }

    /**
     * DEPRECATED: PhoneNumber wrapper + positional creator args.
     * v9+ uses: Message.creator(to, from, body).create()
     * where to/from are plain Strings.
     */
    public String sendSms(String toNumber, String body) {
        Message message = Message.creator(
                new PhoneNumber(toNumber),    // deprecated wrapper
                new PhoneNumber(fromNumber),  // deprecated wrapper
                body
        ).create();

        return message.getSid();
    }

    /**
     * DEPRECATED: Call created with URI callback via positional args.
     * v9+: Call.creator(to, from, url).create() with String params.
     */
    public String makeCall(String toNumber, String callbackUrl) {
        Call call = Call.creator(
                new PhoneNumber(toNumber),    // deprecated
                new PhoneNumber(fromNumber),  // deprecated
                URI.create(callbackUrl)
        ).create();

        return call.getSid();
    }

    /**
     * DEPRECATED: Single-arg fetcher — doesn't include accountSid.
     * v9+ requires: Message.fetcher(accountSid, messageSid)
     */
    public String getMessageStatus(String messageSid) {
        // deprecated single-arg form
        Message message = Message.fetcher(messageSid).fetch();
        // getStatus() used to return String; now returns Message.Status enum
        return message.getStatus().toString();
    }

    /**
     * DEPRECATED: Static deleter with only messageSid.
     * v9+ requires: Message.deleter(accountSid, messageSid)
     */
    public boolean deleteMessage(String messageSid) {
        return Message.deleter(messageSid).delete();  // deprecated single-arg form
    }
}
