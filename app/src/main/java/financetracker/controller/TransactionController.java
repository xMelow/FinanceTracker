package financetracker.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
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
    //Extra
    private static int transactionIdCounter = 0;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private TransactionService transactionService;

    // UI Elements
    @FXML private TextField descriptionField;
    @FXML private TextField amountField;
    @FXML private DatePicker dateField;

    @FXML private Label totalAmountLabel;
    @FXML private Label totalAmountMonthLabel;

    @FXML private TableView<CategorySpending> categorySpendingTableView;
    @FXML private TableColumn<CategorySpending, String> categoryNameColumn;
    @FXML private TableColumn<CategorySpending, String> categoryAmountColumn;

    @FXML private TableView<Transaction> recentTransactionTableView;
    @FXML private TableColumn<Transaction, String> recentDescriptionColumn;
    @FXML private TableColumn<Transaction, String> recentAmountColumn;
    @FXML private TableColumn<Transaction, String> recentCategoryColumn;
    @FXML private TableColumn<Transaction, String> recentDateColumn;

    @FXML private ComboBox<String> categoryComboBox;

    // Data storage
    private final ObservableMap<String, CategorySpending> categorySpendingMap = FXCollections.observableHashMap();
    private final ObservableList<CategorySpending> categorySpendingList = FXCollections.observableArrayList();

    private final DoubleProperty totalAmount = new SimpleDoubleProperty(0.0);
    private final DoubleProperty totalMonthAmount = new SimpleDoubleProperty(0.0);

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @FXML
    public void initialize() {
        setupComboBox();
        setupRecentTransactions();
        setupCategorySpending();
        setupTotals();
    }

    @FXML
    private void setupComboBox() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                Category.SPORT.name(),
                Category.FOOD.name(),
                Category.CLOTHS.name(),
                Category.GOING_OUT.name(),
                Category.TRANSPORT.name(),
                Category.EXTRA.name()
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

        recentDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate().format(DATE_FORMATTER))
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
    private void setupTotals() {
        totalAmountLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            String.format("€ %.2f", totalAmount.get()), totalAmount)
        );

        totalAmountMonthLabel.textProperty().bind(Bindings.createStringBinding(() ->
            String.format("€ %.2f", totalMonthAmount.get()), totalMonthAmount)
        );
    }

    @FXML
    private void addTransaction() {
        String description = descriptionField.getText();
        LocalDate date = dateField.getValue();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return;
        }

        String category = categoryComboBox.getValue();
        Transaction transaction = new Transaction(transactionIdCounter++, amount, date, category, description);
        
        recentTransactionTableView.getItems().add(transaction);
        transactionService.addTransaction(transaction);

        updateTotalAmount(amount, transaction.getDate());
        updateSpendingPerCategory(category, amount);
    }

    private void updateTotalAmount(double amount, LocalDate date) {
        Month currentDateMonth = LocalDate.now().getMonth();
        totalAmount.set(totalAmount.get() + amount);
        if (currentDateMonth == date.getMonth()) {
            totalMonthAmount.set(totalMonthAmount.get() + amount);
        }
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
