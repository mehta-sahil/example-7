# legacy-demo-app

A **deliberately outdated** Spring Boot project that uses deprecated Stripe, Twilio, and SendGrid API patterns.

This repo exists purely for testing the [API Drift Detector](https://github.com/your-org/api-drift-detector).

## What's deprecated in here

### Stripe (`StripeClient.java`)
| Pattern | Status |
|---|---|
| `Stripe.apiKey = "..."` global static | Deprecated — use `new StripeClient(key)` |
| `Charge.create(Map)` | Deprecated — use PaymentIntents |
| `Customer.create(Map)` raw map | Deprecated — use `CustomerCreateParams` builder |
| `Refund.create(Map)` raw map | Deprecated — use `RefundCreateParams` builder |
| `charge.getBalanceTransaction()` → String | Type changed to object in v21 |

### Twilio (`TwilioClient.java`)
| Pattern | Status |
|---|---|
| `Twilio.init(sid, token)` global init | Deprecated in v9+ |
| `new PhoneNumber(string)` wrapper | Deprecated — pass String directly |
| `Message.creator(PhoneNumber, PhoneNumber, body)` | Deprecated positional args |
| `Message.fetcher(sid)` single-arg | Deprecated — use `fetcher(accountSid, sid)` |

### SendGrid (`SendGridClient.java`)
| Pattern | Status |
|---|---|
| `new Email(address)` no display name | Deprecated |
| `new Content("text/plain", body)` constructor | Deprecated |
| `new Mail(from, subject, to, content)` 4-arg | Deprecated — use `Mail.Builder` |
| `sendgrid.api(request)` | Deprecated — use `sendgrid.send(mail)` |

## Push to GitHub

```bash
cd D:\legacy-demo-app
git init
git add .
git commit -m "initial: legacy app with deprecated API integrations"
git remote add origin https://github.com/YOUR_USERNAME/legacy-demo-app.git
git push -u origin main
```
