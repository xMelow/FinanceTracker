package financetracker.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class CategorySpending {
    private final SimpleStringProperty category;
    private final SimpleDoubleProperty amount;

    public CategorySpending(String category, double amount) {
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getCategory() {
        return category.get();
    }

    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }
}
