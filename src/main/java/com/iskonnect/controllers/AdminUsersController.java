// Path: src/main/java/com/iskonnect/controllers/AdminUsersController.java
package com.iskonnect.controllers;

import com.iskonnect.models.UserDetails;
import com.iskonnect.services.AdminUserService;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;

public class AdminUsersController {
    @FXML private TableView<UserDetails> usersTable;
    @FXML private TableColumn<UserDetails, String> userIdColumn;
    @FXML private TableColumn<UserDetails, String> firstNameColumn;
    @FXML private TableColumn<UserDetails, String> lastNameColumn;
    @FXML private TableColumn<UserDetails, String> emailColumn;
    @FXML private TableColumn<UserDetails, Integer> pointsColumn;
    @FXML private TableColumn<UserDetails, Integer> reportsColumn;
    @FXML private TableColumn<UserDetails, Integer> upvotesColumn;
    @FXML private TextField searchField;
    @FXML private ProgressIndicator loadingIndicator;

    private final AdminUserService userService;
    private ObservableList<UserDetails> usersList;
    private FilteredList<UserDetails> filteredUsers;

    public AdminUsersController() {
        this.userService = new AdminUserService();
    }

    @FXML
    public void initialize() {
        setupColumns();
        setupSearch();
        loadUsers();
    }

    private void setupColumns() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        reportsColumn.setCellValueFactory(new PropertyValueFactory<>("reportCount"));
        upvotesColumn.setCellValueFactory(new PropertyValueFactory<>("upvoteCount"));

        // Add delete button column
        TableColumn<UserDetails, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");
            {
                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
                deleteBtn.getStyleClass().add("delete-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
        usersTable.getColumns().add(actionCol);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredUsers != null) {
                filteredUsers.setPredicate(user -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return user.getUserId().toLowerCase().contains(lowerCaseFilter) ||
                           user.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                           user.getLastName().toLowerCase().contains(lowerCaseFilter) ||
                           user.getEmail().toLowerCase().contains(lowerCaseFilter);
                });
            }
        });
    }

    private void loadUsers() {
        loadingIndicator.setVisible(true);
        new Thread(() -> {
            try {
                usersList = userService.getAllUsers();
                filteredUsers = new FilteredList<>(usersList, p -> true);
                
                javafx.application.Platform.runLater(() -> {
                    usersTable.setItems(filteredUsers);
                    loadingIndicator.setVisible(false);
                });
            } catch (SQLException e) {
                javafx.application.Platform.runLater(() -> {
                    showError("Error loading users", e.getMessage());
                    loadingIndicator.setVisible(false);
                });
            }
        }).start();
    }

    private void handleDelete(UserDetails user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User");
        confirm.setContentText("Are you sure you want to delete this user? This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (userService.deleteUser(user.getUserId())) {
                        usersList.remove(user);
                        showSuccess("User deleted successfully");
                    } else {
                        showError("Delete Failed", "Could not delete the user");
                    }
                } catch (SQLException e) {
                    showError("Delete Error", e.getMessage());
                }
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}