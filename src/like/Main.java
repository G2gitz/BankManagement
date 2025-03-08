package like;

import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {
    private Connection connection; // for database connection
    public static void main(String[] args) {  
        launch(args);
    }
    Scene scene;  // for use main scene in other screens
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
            createAccount(name, initialAmount);
            showAlert(Alert.AlertType.INFORMATION, "Account Created", "Account created successfully!");
        });

        Button depositButton = new Button("Deposit");
        GridPane.setConstraints(depositButton, 1, 2);
        depositButton.setOnAction(e -> {
            primaryStage.setScene(createDepositScene(primaryStage));
        });
        
        Button withdrawButton = new Button("Withdraw");
        GridPane.setConstraints(withdrawButton, 2, 2);
        withdrawButton.setOnAction(e -> {
            primaryStage.setScene(createWithdrawScene(primaryStage));
        });
        
        Button checkBalanceButton = new Button("Check Balance");
        GridPane.setConstraints(checkBalanceButton, 0, 3);
        checkBalanceButton.setOnAction(e -> {
            primaryStage.setScene(createCheckBalanceScene(primaryStage));
            
        });
        Button showDetailsButton = new Button("Show Details");
        GridPane.setConstraints(showDetailsButton, 1, 3);
        showDetailsButton.setOnAction(e -> {
            primaryStage.setScene(createShowDetailsScene(primaryStage));
        });

        Button exitButton = new Button("Exit");
        GridPane.setConstraints(exitButton, 2, 3);
        exitButton.setOnAction(e -> {System.exit(0);});

        //adding all components to scene
        grid.getChildren().addAll(nameLabel, nameInput, amountLabel, amountInput, createAccountButton,depositButton, withdrawButton, checkBalanceButton,exitButton,showDetailsButton);

        scene = new Scene(grid, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //for dopsit screen
    private Scene createDepositScene(Stage primaryStage) {
        GridPane depositGrid = new GridPane();
        depositGrid.setPadding(new Insets(20, 20, 20, 20));
        depositGrid.setVgap(10);
        depositGrid.setHgap(10);

        Label accnolabel = new Label("Account Number:");
        GridPane.setConstraints(accnolabel, 0, 0);
        TextField accno = new TextField();
        GridPane.setConstraints(accno, 1, 0);
    
        Label depositLabel = new Label("Deposit Amount:");
        GridPane.setConstraints(depositLabel, 0, 1);
        TextField depositInput = new TextField();
        GridPane.setConstraints(depositInput, 1, 1);

        Label pinnumLabel = new Label("PIN Number:");
        GridPane.setConstraints(pinnumLabel, 0, 2);
        TextField pinnum = new TextField();
        GridPane.setConstraints(pinnum, 1, 2);
    
        Button confirmDepositButton = new Button("Confirm Deposit");
        GridPane.setConstraints(confirmDepositButton, 0, 3);
        confirmDepositButton.setOnAction(e -> {
            String accountnum=accno.getText();
            double depositAmount = Double.parseDouble(depositInput.getText());
            String pin_num = pinnum.getText();
            if (verifyPIN(accountnum, pin_num)) {
                deposit(accountnum, depositAmount);
                primaryStage.setScene(scene);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Incorrect PIN. Deposit failed.");
            }
        });
    
        Button backButton = new Button("Back");
        GridPane.setConstraints(backButton, 1, 3);
        backButton.setOnAction(e -> {
            primaryStage.setScene(scene);
        });
    
        depositGrid.getChildren().addAll(pinnum,pinnumLabel,accnolabel,accno,depositLabel, depositInput, confirmDepositButton, backButton);
    
        Scene depositScene = new Scene(depositGrid, 400, 250);
        return depositScene;
    }
    //for create new account
    private void createAccount(String name, double initialAmount) {
        try {
            String url = "jdbc:mysql://localhost:3306/javafxmini";
            String user = "root";
            String password = "Mksql@123";
            connection = DriverManager.getConnection(url, user, password);
            String insertQuery = "INSERT INTO users (name, account_number, pin, balance) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            String accountNumber = generateAccountNumber();
            preparedStatement.setString(2, accountNumber);
            String pinNumber = generatePIN();
            preparedStatement.setString(3, pinNumber);
            preparedStatement.setDouble(4, initialAmount);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Account created successfully!");
                System.out.println("Account Number: " + accountNumber);
                System.out.println("PIN Number: " + pinNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //for deposit money
    private void deposit(String accountNumber, double amount) {
        try {
            String url = "jdbc:mysql://localhost:3306/javafxmini";
            String user = "root";
            String password = "Mksql@123";
            connection = DriverManager.getConnection(url, user, password);

            String depositQuery = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(depositQuery);
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, accountNumber);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Deposit", "Deposit successful!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //for withdraw money
    private void withdraw(String accountNumber, double amount) {
        try {
            String url = "jdbc:mysql://localhost:3306/javafxmini";
            String user = "root";
            String password = "Mksql@123";
            connection = DriverManager.getConnection(url, user, password);

            String withdrawQuery = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(withdrawQuery);
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, accountNumber);
            preparedStatement.setDouble(3, amount);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Withdraw", "Withdrawal successful!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Insufficient funds or Account not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //for checking balance
    private void checkBalance(String accountNumber) {
        try {
            String url = "jdbc:mysql://localhost:3306/javafxmini";
            String user = "root";
            String password = "Mksql@123";
            connection = DriverManager.getConnection(url, user, password);

            String selectQuery = "SELECT balance FROM users WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                showAlert(Alert.AlertType.INFORMATION, "Balance", "Balance: $" + balance);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Account not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //for verifying the if th pin is correct or not
    private boolean verifyPIN(String accountNumber, String enteredPIN) {
        try {
            String url = "jdbc:mysql://localhost:3306/javafxmini";
            String user = "root";
            String password = "Mksql@123";
            connection = DriverManager.getConnection(url, user, password);

            String selectQuery = "SELECT * FROM users WHERE account_number = ? AND pin = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, enteredPIN);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //alert box
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    //for generating random account number
    private String generateAccountNumber() {
        
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder("1");
        for (int i = 1; i < 10; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }
    //for generating random pin number
    private String generatePIN() {
        Random random = new Random();
        StringBuilder pinNumber = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pinNumber.append(random.nextInt(10));
        }
        return pinNumber.toString();
    }
    
    //for withdraw screen
    private Scene createWithdrawScene(Stage primaryStage) {
        GridPane withdrawGrid = new GridPane();
        withdrawGrid.setPadding(new Insets(20, 20, 20, 20));
        withdrawGrid.setVgap(10);
        withdrawGrid.setHgap(10);
    
        Label accNoLabel = new Label("Account Number:");
        GridPane.setConstraints(accNoLabel, 0, 0);
        TextField accNoInput = new TextField();
        GridPane.setConstraints(accNoInput, 1, 0);
    
        Label withdrawAmountLabel = new Label("Withdraw Amount:");
        GridPane.setConstraints(withdrawAmountLabel, 0, 1);
        TextField withdrawAmountInput = new TextField();
        GridPane.setConstraints(withdrawAmountInput, 1, 1);
    
        Label pinLabel = new Label("PIN Number:");
        GridPane.setConstraints(pinLabel, 0, 2);
        TextField pinInput = new TextField();
        GridPane.setConstraints(pinInput, 1, 2); 
    
        Button confirmWithdrawButton = new Button("Confirm Withdraw");
        GridPane.setConstraints(confirmWithdrawButton, 0, 3);
        confirmWithdrawButton.setOnAction(e -> {
            String accountNum = accNoInput.getText();
            double withdrawAmount = Double.parseDouble(withdrawAmountInput.getText());
            String pinNum = pinInput.getText();
            if (verifyPIN(accountNum, pinNum)) {
                withdraw(accountNum, withdrawAmount);
                primaryStage.setScene(scene);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Incorrect PIN. Withdraw failed.");
            }
        });
    
        Button backButton = new Button("Back");
        GridPane.setConstraints(backButton, 1, 3);
        backButton.setOnAction(e -> {
            primaryStage.setScene(scene);
        });
    
        withdrawGrid.getChildren().addAll(accNoLabel, accNoInput, withdrawAmountLabel, withdrawAmountInput, pinLabel, pinInput, confirmWithdrawButton, backButton);
    
        Scene withdrawScene = new Scene(withdrawGrid, 400, 250);
        return withdrawScene;
    }   
    //for balance checking scene
    private Scene createCheckBalanceScene(Stage primaryStage) {

        GridPane checkBalanceGrid = new GridPane();
        checkBalanceGrid.setPadding(new Insets(20, 20, 20, 20));
        checkBalanceGrid.setVgap(10);
        checkBalanceGrid.setHgap(10);
    
        Label accNoLabel = new Label("Account Number:");
        GridPane.setConstraints(accNoLabel, 0, 0);
        TextField accNoInput = new TextField();
        GridPane.setConstraints(accNoInput, 1, 0);
    
        Label pinNoLabel = new Label("PIN Number:");
        GridPane.setConstraints(pinNoLabel, 0, 1); 
        TextField pinNoInput = new TextField();
        GridPane.setConstraints(pinNoInput, 1, 1);
    
        Button checkBalanceButton = new Button("Check Balance");
        GridPane.setConstraints(checkBalanceButton, 0, 2);
        checkBalanceButton.setOnAction(e -> {
            String accountNum = accNoInput.getText();
            String pinNum = pinNoInput.getText();
            if (verifyPIN(accountNum, pinNum)) {
                checkBalance(accountNum);
                primaryStage.setScene(scene);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Incorrect PIN. Check balance failed.");
            }
        });
    
        Button backButton = new Button("Back");
        GridPane.setConstraints(backButton, 1, 2);
        backButton.setOnAction(e -> {
            primaryStage.setScene(scene);
        });
    
        checkBalanceGrid.getChildren().addAll(accNoLabel, accNoInput, pinNoLabel, pinNoInput, checkBalanceButton, backButton);
    
        Scene checkBalanceScene = new Scene(checkBalanceGrid, 400, 250);
        return checkBalanceScene;
    }
    private String retrieveUserDetails(String accountNumber) {
        StringBuilder userDetails = new StringBuilder();
    
        try {
            String url = "jdbc:mysql://localhost:3306/javafxmini";
            String user = "root";
            String password = "Mksql@123";
            connection = DriverManager.getConnection(url, user, password);
    
            String selectQuery = "SELECT name, account_number, balance FROM users WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, accountNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
    
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String accNumber = resultSet.getString("account_number");
                double balance = resultSet.getDouble("balance");
    
                userDetails.append("Name: ").append(name).append("\n");
                userDetails.append("Account Number: ").append(accNumber).append("\n");
                userDetails.append("Balance: $").append(balance);
            } else {
                userDetails.append("User details not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            userDetails.append("Error retrieving user details.");
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        return userDetails.toString();
    }
     // Create a method to display user details in a new scene
     // Create a method to display user details in a new scene
// Create a method to display user details in a new scene
private Scene createShowDetailsScene(Stage primaryStage) {
    GridPane detailsGrid = new GridPane();
    detailsGrid.setPadding(new Insets(20, 20, 20, 20));
    detailsGrid.setVgap(10);
    detailsGrid.setHgap(10);

    Label accNoLabel = new Label("Account Number:");
    GridPane.setConstraints(accNoLabel, 0, 0);
    TextField accNoInput = new TextField();
    GridPane.setConstraints(accNoInput, 1, 0);

    Label pinNoLabel = new Label("PIN Number:");
    GridPane.setConstraints(pinNoLabel, 0, 1); 
    TextField pinNoInput = new TextField();
    GridPane.setConstraints(pinNoInput, 1, 1);

    Button showDetailsButton = new Button("Show Details");
    GridPane.setConstraints(showDetailsButton, 0, 2);
    showDetailsButton.setOnAction(e -> {
        String accountNum = accNoInput.getText();
        String pinNum = pinNoInput.getText();
        if (verifyPIN(accountNum, pinNum)) {
            String userDetails = retrieveUserDetails(accountNum);
            if (!userDetails.equals("User details not found.") && !userDetails.equals("Error retrieving user details.")) {
                GridPane userDetailsGrid = new GridPane(); // Create a new GridPane for user details
                userDetailsGrid.setPadding(new Insets(20, 20, 20, 20));
                userDetailsGrid.setVgap(10);
                userDetailsGrid.setHgap(10);

                Label userDetailsLabel = new Label("User Details:");
                GridPane.setConstraints(userDetailsLabel, 0, 0);

                TextArea userDetailsTextArea = new TextArea();
                userDetailsTextArea.setText(userDetails);
                userDetailsTextArea.setEditable(false);
                userDetailsTextArea.setWrapText(true);
                userDetailsTextArea.setMaxWidth(300);
                userDetailsTextArea.setMaxHeight(200);
                GridPane.setConstraints(userDetailsTextArea, 0, 1);

                Button backButton = new Button("Back");
                GridPane.setConstraints(backButton, 0, 2);
                backButton.setOnAction(event -> {
                    primaryStage.setScene(scene); // Go back to the main screen
                });

                userDetailsGrid.getChildren().addAll(userDetailsLabel, userDetailsTextArea, backButton);
                Scene userDetailsScene = new Scene(userDetailsGrid, 400, 250);
                primaryStage.setScene(userDetailsScene); // Show user details in a new screen
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", userDetails);
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Incorrect Account Number or PIN.");
        }
    });

    Button backButton = new Button("Back");
    GridPane.setConstraints(backButton, 1, 2);
    backButton.setOnAction(e -> {
        primaryStage.setScene(scene); // Go back to the main screen
    });

    detailsGrid.getChildren().addAll(accNoLabel, accNoInput, pinNoLabel, pinNoInput, showDetailsButton, backButton);

    Scene showDetailsScene = new Scene(detailsGrid, 400, 250);
    return showDetailsScene;
}
}