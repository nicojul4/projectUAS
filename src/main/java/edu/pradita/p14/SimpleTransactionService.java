package edu.pradita.p14;

public class SimpleTransactionService implements TransactionService {
    @Override
    public void processTransaction(Transaction transaction) {
        // Simpan ke database atau proses lain
        System.out.println("Processing transaction: " + transaction.getTransactionId());
    }
} 