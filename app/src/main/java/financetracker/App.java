package financetracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import java.io.IOException;
import financetracker.controller.ApplicationController;
import financetracker.repository.TransactionRepository;
import financetracker.service.TransactionService;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DashboardView.fxml"));
        TabPane root = loader.load();
        
        TransactionRepository transactionRepository = new TransactionRepository();
        TransactionService transactionService = new TransactionService(transactionRepository);
        
        ApplicationController controller = loader.getController();
        controller.setTransactionService(transactionService);
        controller.initAfterInject();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setTitle("Finance Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
