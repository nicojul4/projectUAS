package edu.pradita.p14;

import org.junit.Test;
import static org.junit.Assert.*;

public class TransactionTest {
    @Test
    public void testTransactionCreation() {
        Transaction t = new Transaction(1, 2, 3, 4, 5000.0);
        assertEquals(1, t.getTransactionId());
        assertEquals(2, t.getUserId());
        assertEquals(3, t.getProductId());
        assertEquals(4, t.getQuantity());
        assertEquals(5000.0, t.getTotalPrice(), 0.01);
    }
} 