// Path: src/main/java/com/iskonnect/controllers/PaginationController.java

package com.iskonnect.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class PaginationController {
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Text pageText;

    private int currentPage = 1;
    private int totalPages = 1;
    private PaginationCallback callback;

    public interface PaginationCallback {
        void onPageChange(int newPage);
    }

    @FXML
    public void initialize() {
        updateButtonStates();
    }

    public void setCallback(PaginationCallback callback) {
        this.callback = callback;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        updatePageText();
        updateButtonStates();
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePageText();
            updateButtonStates();
            if (callback != null) {
                callback.onPageChange(currentPage);
            }
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePageText();
            updateButtonStates();
            if (callback != null) {
                callback.onPageChange(currentPage);
            }
        }
    }

    private void updatePageText() {
        pageText.setText(String.format("Page %d of %d", currentPage, totalPages));
    }

    private void updateButtonStates() {
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages);
    }

    public void reset() {
        currentPage = 1;
        updatePageText();
        updateButtonStates();
    }
}