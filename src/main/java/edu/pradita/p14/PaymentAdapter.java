package edu.pradita.p14;

/**
 * Adapter pattern: Mengadaptasi OldPaymentSystem ke NewPaymentSystem
 */
public class PaymentAdapter implements NewPaymentSystem {
    private OldPaymentSystem oldPaymentSystem;

    public PaymentAdapter(OldPaymentSystem oldPaymentSystem) {
        this.oldPaymentSystem = oldPaymentSystem;
    }

    public void makePayment(double amount) {
        oldPaymentSystem.pay((int) amount);
    }
} 