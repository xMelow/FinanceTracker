package financetracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import financetracker.controller.TransactionController;
import financetracker.repository.TransactionRepository;
import financetracker.service.TransactionService;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DashboardView.fxml"));
        AnchorPane root = loader.load();
        
        TransactionRepository transactionRepository = new TransactionRepository();
        TransactionService transactionService = new TransactionService(transactionRepository);
        TransactionController controller = loader.getController();
        controller.setTransactionService(transactionService);
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Finance Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
