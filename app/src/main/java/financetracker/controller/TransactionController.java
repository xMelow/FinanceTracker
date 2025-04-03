package financetracker.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

import financetracker.model.Category;
import financetracker.model.CategorySpending;
import financetracker.model.Transaction;
import financetracker.service.TransactionService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TransactionController {
    private static int transactionIdCounter = 0;
    private TransactionService transactionService;

    // UI Elements
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private Label totalAmountLabel;

    @FXML private TableView<CategorySpending> categorySpendingTableView;
    @FXML private TableColumn<CategorySpending, String> categoryNameColumn;
    @FXML private TableColumn<CategorySpending, String> categoryAmountColumn;

    @FXML private TableView<Transaction> recentTransactionTableView;
    @FXML private TableColumn<Transaction, String> recentDescriptionColumn;
    @FXML private TableColumn<Transaction, String> recentAmountColumn;
    @FXML private TableColumn<Transaction, String> recentCategoryColumn;

    @FXML private ComboBox<String> categoryComboBox;

    // Data storage
    private final ObservableMap<String, CategorySpending> categorySpendingMap = FXCollections.observableHashMap();
    private final ObservableList<CategorySpending> categorySpendingList = FXCollections.observableArrayList();

    private final DoubleProperty totalAmount = new SimpleDoubleProperty(0.0);

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @FXML
    public void initialize() {
        setupComboBox();
        setupRecentTransactions();
        setupCategorySpending();
        
        totalAmountLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            String.format("€ %.2f", totalAmount.get()), totalAmount)
        );
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
        recentDescriptionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescription())
        );

        recentAmountColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("€ %.2f", cellData.getValue().getAmount()))
        );

        recentCategoryColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategory())
        );
    }

    @FXML
    private void setupCategorySpending() {
        categoryNameColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategory())
        );

        categoryAmountColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("€ %.2f", cellData.getValue().getAmount()))
        );

        categorySpendingTableView.setItems(categorySpendingList);
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
        
        recentTransactionTableView.getItems().add(transaction);
        transactionService.addTransaction(transaction);

        updateTotalAmount(amount);
        updateSpendingPerCategory(category, amount);
    }

    private void updateTotalAmount(double amount) {
        totalAmount.set(totalAmount.get() + amount);
    }

    private void updateSpendingPerCategory(String category, double amount) {
        categorySpendingMap.compute(category, (key, existing) -> {
            if (existing == null) return new CategorySpending(category, amount);
            existing.setAmount(existing.getAmount() + amount);
            return existing;
        });

        categorySpendingList.setAll(categorySpendingMap.values());
    }
}
