package financetracker.controller;

import java.time.LocalDate;

import financetracker.model.Transaction;
import financetracker.service.TransactionService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class TransactionController {
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private ListView<String> transactionListView;

    private TransactionService transactionService;

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @FXML
    private void addTransaction() {
        String description = descriptionField.getText();
        double amount = Double.parseDouble(amountField.getText());
        Transaction transaction = new Transaction(0, amount, LocalDate.now(), "General", description);

        transactionListView.getItems().addAll(description + " - $" + amount);
        transactionService.addTransaction(transaction);

    }
}
