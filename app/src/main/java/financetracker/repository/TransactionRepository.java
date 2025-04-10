package financetracker.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import financetracker.model.Transaction;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private static final String FILE_PATH = "data/transactions.json";
    private final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .setPrettyPrinting()
        .create();

    public void addTransaction(Transaction transaction) {
        List<Transaction> transactions = loadTransactions();
        transactions.add(transaction);
        saveTransactions(transactions);
    }

    public void saveTransactions(List<Transaction> transactions) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> loadTransactions() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<List<Transaction>>() {}.getType();
            List<Transaction> transactions = gson.fromJson(reader, listType);
            return transactions != null ? transactions : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
