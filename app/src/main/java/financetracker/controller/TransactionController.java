package financetracker.controller;

import java.time.LocalDate;

import financetracker.model.Transaction;
import financetracker.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TransactionController {
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private TableView<String> transactionTableView;
    @FXML private ComboBox<String> categoryComboBox;

    private TransactionService transactionService;

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @FXML
    public void initalize() {
        ObservableList<String> categories = FXCollections.observableArrayList("Food", "Transport", "Sport", "Going Out", "Cloths", "Extra");
        categoryComboBox.setItems(categories);
    }

    @FXML
    private void addTransaction() {
        String description = descriptionField.getText();
        double amount = Double.parseDouble(amountField.getText());
        Transaction transaction = new Transaction(0, amount, LocalDate.now(), "General", description);

        transactionTableView.getItems().addAll(transaction.getDescription() + " - $" + transaction.getAmount());
        transactionService.addTransaction(transaction);

    }
}
