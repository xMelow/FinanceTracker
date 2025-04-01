package financetracker.controller;

import java.time.LocalDate;

import financetracker.model.Category;
import financetracker.model.Transaction;
import financetracker.service.TransactionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TransactionController {
    private static int transactionIdCounter = 0;
    private TransactionService transactionService;
    
    // add Transaction
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;

    // total amount
    @FXML private Label totalAmountLabel;

    // recent transaction table
    @FXML private TableView<Transaction> transactionTableView;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> amountColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;

    // combobox
    @FXML private ComboBox<String> categoryComboBox;

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @FXML
    public void initialize() {
        setupComboBox();
        setupRecentTransactions();
    }

    @FXML
    private void addTransaction() {
        String description = descriptionField.getText();
        double amount;
    
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return;
        }
    
        String category = categoryComboBox.getValue();
        
        Transaction transaction = new Transaction(transactionIdCounter++, amount, LocalDate.now(), category, description);
    
        transactionTableView.getItems().add(transaction);
        transactionService.addTransaction(transaction);

        double currentTotal = Double.parseDouble(totalAmountLabel.getText());
        currentTotal += amount;
        totalAmountLabel.setText(String.format("%.2f", currentTotal));
    }

    @FXML
    private void setupComboBox() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                Category.SPORT.name().toLowerCase(),
                Category.FOOD.name().toLowerCase(),
                Category.CLOTHS.name().toLowerCase(),
                Category.GOING_OUT.name().toLowerCase(),
                Category.TRANSPORT.name().toLowerCase(),
                Category.EXTRA.name().toLowerCase()               
        );
        categoryComboBox.setItems(categories);
        categoryComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void setupRecentTransactions() {
        descriptionColumn.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getDescription())
        );

        amountColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(String.format("€ %.2f", cellData.getValue().getAmount()))
        );

        categoryColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategory())
        );
    }
}
