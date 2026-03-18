package com.example.integrations.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Legacy Stripe integration using deprecated patterns.
 *
 * DEPRECATED PATTERNS:
 * 1. Stripe.apiKey as global static field — deprecated since stripe-java v22+
 *    (new: new StripeClient(apiKey))
 * 2. Charge.create(Map) — Charges API deprecated in favour of PaymentIntents (2019-02-11)
 * 3. Customer.create(Map) with raw Map — deprecated, use CustomerCreateParams builder
 * 4. Refund.create(Map) with raw Map — deprecated, use RefundCreateParams builder
 * 5. charge.getBalanceTransaction() returning String — type changed in v21
 */
@Service
public class StripeClient {

    @Value("${stripe.api.key:sk_test_placeholder}")
    private String apiKey;

    @PostConstruct
    public void init() {
        // DEPRECATED: Global static API key assignment.
        // Stripe v22+ recommends: StripeClient client = new StripeClient(apiKey);
        Stripe.apiKey = apiKey;
    }

    /**
     * DEPRECATED: Direct Charge creation.
     * Stripe deprecated Charges in favour of PaymentIntents since 2019.
     * See: https://stripe.com/docs/upgrades#2019-02-11
     */
    public Charge createLegacyCharge(long amountCents, String currency, String source)
            throws StripeException {
        // Raw Map params — deprecated, use ChargeCreateParams.builder()
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amountCents);
        params.put("currency", currency);
        params.put("source", source);
        params.put("description", "Legacy charge via deprecated API");

        return Charge.create(params);  // @Deprecated in stripe-java v21+
    }

    /**
     * Modern replacement (kept here to show contrast).
     */
    public PaymentIntent createPaymentIntent(long amountCents, String currency)
            throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency)
                .build();
        return PaymentIntent.create(params);
    }

    /**
     * DEPRECATED: Creating customer with raw Map params.
     * Use CustomerCreateParams typed builder instead.
     */
    public Customer createLegacyCustomer(String email, String name) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("name", name);
        params.put("description", "Created via deprecated customer API");

        return Customer.create(params);  // deprecated raw-Map overload
    }

    /**
     * DEPRECATED: Refund with raw Map params.
     * Use RefundCreateParams.builder() instead.
     */
    public Refund createLegacyRefund(String chargeId) throws StripeException {
        Map<String, Object> params = new HashMap<>();
        params.put("charge", chargeId);

        return Refund.create(params);  // deprecated raw-Map overload
    }

    /**
     * DEPRECATED: getBalanceTransaction() returned a String in stripe-java <v21.
     * In v21+ it returns a BalanceTransactionTypeAdapterFactory — String cast breaks at runtime.
     */
    public String getChargeBalanceTransaction(String chargeId) throws StripeException {
        Charge charge = Charge.retrieve(chargeId);
        return charge.getBalanceTransaction();  // type mismatch in newer versions
    }
}
