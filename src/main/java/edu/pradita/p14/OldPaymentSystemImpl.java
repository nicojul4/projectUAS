package edu.pradita.p14;

public class OldPaymentSystemImpl implements OldPaymentSystem {
    public void pay(int amount) {
        System.out.println("Paid with old system: " + amount);
    }
} 