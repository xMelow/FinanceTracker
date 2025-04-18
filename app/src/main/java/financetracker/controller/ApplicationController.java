package financetracker.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

public class ApplicationController {
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

    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;

    // Data storage
    private final ObservableMap<String, CategorySpending> categorySpendingMap = FXCollections.observableHashMap();
    private final ObservableList<CategorySpending> categorySpendingList = FXCollections.observableArrayList();
    private HashMap<Month, Double> spendingPerMonth = new HashMap<>();

    private final DoubleProperty totalAmount = new SimpleDoubleProperty(0.0);
    private final DoubleProperty totalMonthAmount = new SimpleDoubleProperty(0.0);

    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;   
    }

    @FXML
    public void initialze() {
        //run!
    }

    @FXML
    public void initAfterInject() {
        loadTransactions();
        setupComboBox();
        setupRecentTransactions();
        setupCategorySpending();
        setupTotals();
        setupPieChart();
        setupBarChart();
    }

    @FXML
    private void loadTransactions() {
        List<Transaction> transactions = transactionService.getTransactions();

        for (Transaction transaction : transactions) {
            recentTransactionTableView.getItems().add(transaction);
            updateTotalAmount(transaction.getAmount(), transaction.getDate());
            updateSpendingPerCategory(transaction.getCategory(), transaction.getAmount());
            updateSpendingPerMonth(transaction);
        }
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

        recentDateColumn.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDate();

            String formattedDate;
            if (date != null) {
                formattedDate = date.format(DATE_FORMATTER);
            } else {
                formattedDate = LocalDate.now().format(DATE_FORMATTER);
            }

            return new SimpleStringProperty(formattedDate);
        });
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
    private void setupPieChart() {
        List<CategorySpending> categorySpendings = categorySpendingTableView.getItems();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (CategorySpending cs : categorySpendings) {
            pieChartData.add(new PieChart.Data(cs.getCategory(), cs.getAmount()));
        }

        pieChart.setData(pieChartData);
        pieChart.setTitle("Spending per category");
    }

    @FXML
    private void setupBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        LocalDate date = LocalDate.now();
        series.setName(String.format("%d", date.getYear()));

        for (Month month : Month.values()) {
            double amount = spendingPerMonth.getOrDefault(month, 0.0);
            String monthLabel = month.getDisplayName(TextStyle.SHORT, Locale.getDefault());
            series.getData().add(new XYChart.Data<>(monthLabel, amount));
        }        
        
        barChart.getData().add(series);
        barChart.setTitle("Monthly Spending");
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
        
        transactionService.addTransaction(transaction);

        updateData(transaction);
    }

    private void updateData(Transaction transaction) {
        recentTransactionTableView.getItems().add(transaction);
        updateTotalAmount(transaction.getAmount(), transaction.getDate());
        updateSpendingPerCategory(transaction.getCategory(), transaction.getAmount());
        updateSpendingPerMonth(transaction);
    }

    private void updateTotalAmount(double amount, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
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

    private void updateSpendingPerMonth(Transaction transaction) {
        Month month = transaction.getDate().getMonth();
        double amount = transaction.getAmount();
    
        if (spendingPerMonth.containsKey(month)) {
            double currentTotal = spendingPerMonth.get(month);
            spendingPerMonth.put(month, currentTotal + amount);
        } else {
            spendingPerMonth.put(month, amount);
        }
    }
    
}
