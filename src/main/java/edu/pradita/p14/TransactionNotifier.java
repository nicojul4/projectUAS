package edu.pradita.p14;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer pattern: Mengelola daftar observer dan mengirim notifikasi saat transaksi diproses.
 */
public class TransactionNotifier {
    private List<TransactionObserver> observers = new ArrayList<>();

    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Transaction transaction) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionProcessed(transaction);
        }
    }
} 