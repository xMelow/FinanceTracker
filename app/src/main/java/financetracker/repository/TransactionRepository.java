package financetracker.repository;

import java.util.ArrayList;
import java.util.List;

import financetracker.model.*;;

public class TransactionRepository {
    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }
}
