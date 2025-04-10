package financetracker.service;

import java.util.List;

import financetracker.model.Transaction;
import financetracker.repository.TransactionRepository;

public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void addTransaction(Transaction transaction) {
        transactionRepository.addTransaction(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactionRepository.loadTransactions();
    }
}
