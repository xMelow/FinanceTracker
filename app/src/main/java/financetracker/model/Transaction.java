package financetracker.model;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private double amount;
    private LocalDate date;
    private String category;
    private String description;

    public Transaction() {
        this.date = LocalDate.now();
    }

    public Transaction(int id, double amount, LocalDate date, String category, String description) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
}
