package like;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class test extends Application {
    private Map<String, Double> accounts;
    public test() {
        accounts = new HashMap<>();
    }
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Banking App");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        Label nameLabel = new Label("Name:");
        GridPane.setConstraints(nameLabel, 0, 0);
        TextField nameInput = new TextField();
        GridPane.setConstraints(nameInput, 1, 0);

        Label amountLabel = new Label("Amount:");
        GridPane.setConstraints(amountLabel, 0, 1);
        TextField amountInput = new TextField();
        GridPane.setConstraints(amountInput, 1, 1);

        Button createAccountButton = new Button("Create Account");
        GridPane.setConstraints(createAccountButton, 0, 2);
        createAccountButton.setOnAction(e -> {
            String name = nameInput.getText();
            double initialAmount = Double.parseDouble(amountInput.getText());
            accounts.put(name, initialAmount);
            showAlert(Alert.AlertType.INFORMATION, "Account Created", "Account created successfully!");
        });

        Button depositButton = new Button("Deposit");
        GridPane.setConstraints(depositButton, 1, 2);
        depositButton.setOnAction(e -> {
            String name = nameInput.getText();
            double depositAmount = Double.parseDouble(amountInput.getText());
            if (accounts.containsKey(name)) {
                double currentBalance = accounts.get(name);
                accounts.put(name, currentBalance + depositAmount);
                showAlert(Alert.AlertType.INFORMATION, "Deposit", "Deposit successful!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found!");
            }
        });

        Button withdrawButton = new Button("Withdraw");
        GridPane.setConstraints(withdrawButton, 2, 2);
        withdrawButton.setOnAction(e -> {
            String name = nameInput.getText();
            double withdrawAmount = Double.parseDouble(amountInput.getText());
            if (accounts.containsKey(name)) {
                double currentBalance = accounts.get(name);
                if (currentBalance >= withdrawAmount) {
                    accounts.put(name, currentBalance - withdrawAmount);
                    showAlert(Alert.AlertType.INFORMATION, "Withdraw", "Withdrawal successful!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Insufficient funds!");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found!");
            }
        });

        Button checkBalanceButton = new Button("Check Balance");
        GridPane.setConstraints(checkBalanceButton, 0, 3);
        checkBalanceButton.setOnAction(e -> {
            String name = nameInput.getText();
            if (accounts.containsKey(name)) {
                double balance = accounts.get(name);
                showAlert(Alert.AlertType.INFORMATION, "Balance", "Balance: $" + balance);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found!");
            }
        });

        grid.getChildren().addAll(nameLabel, nameInput, amountLabel, amountInput, createAccountButton,
                depositButton, withdrawButton, checkBalanceButton);

        Scene scene = new Scene(grid, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}