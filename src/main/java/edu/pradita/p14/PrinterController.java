package edu.pradita.p14;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PrinterController {

    @FXML
    private javafx.scene.control.TextField hostField;
    @FXML
    private javafx.scene.control.TextField dbNameField;
    @FXML
    private javafx.scene.control.TextField usernameField;
    @FXML
    private javafx.scene.control.TextField userIdField;
    @FXML
    private javafx.scene.control.TextField productIdField;
    @FXML
    private javafx.scene.control.TextField quantityField;
    @FXML
    private javafx.scene.control.TextField totalPriceField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private Button connectButton;
    @FXML
    private Button printButton;
    @FXML
    private Label statusLabel;
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Integer> colId;
    @FXML
    private TableColumn<Transaction, Integer> colUser;
    @FXML
    private TableColumn<Transaction, Integer> colProduct;
    @FXML
    private TableColumn<Transaction, Integer> colQty;
    @FXML
    private TableColumn<Transaction, Double> colTotal;
    @FXML
    private javafx.scene.control.TextField searchField;

    private Connection connection;
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactions = new FilteredList<>(transactions, p -> true);
    private int nextId = 1;
    private static final String DATA_FILE = "transactions.json";
    private final Gson gson = new Gson();
    private TransactionNotifier notifier = new TransactionNotifier();
    private final DataProvider provider = new HibernateTransactionAdapter();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        transactionTable.setItems(filteredTransactions);

        transactionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                userIdField.setText(String.valueOf(newSel.getUserId()));
                productIdField.setText(String.valueOf(newSel.getProductId()));
                quantityField.setText(String.valueOf(newSel.getQuantity()));
                totalPriceField.setText(String.valueOf(newSel.getTotalPrice()));
            }
        });

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                String filter = newVal.trim();
                if (filter.isEmpty()) {
                    filteredTransactions.setPredicate(t -> true);
                } else {
                    filteredTransactions.setPredicate(t ->
                        String.valueOf(t.getUserId()).contains(filter) ||
                        String.valueOf(t.getProductId()).contains(filter)
                    );
                }
            });
        }
        loadData();
    }

    @FXML
    private void handleConnect() {
        String host = hostField.getText();
        String dbName = dbNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        String url = "jdbc:mysql://" + host + ":3306/" + dbName + "?useSSL=false&serverTimezone=UTC";
        try {
            connection = DriverManager.getConnection(url, username, password);
            statusLabel.setText("Status: Connected to Database");
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            printButton.setDisable(false);
        } catch (SQLException e) {
            statusLabel.setText("Status: Connection Failed!");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            e.printStackTrace();
        }
    }

    // Load saat koneksi sukses
    private void loadTransactions() {
        ObservableList<Transaction> data = FXCollections.observableArrayList();
        String query = "SELECT * FROM transactions";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.add(new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getInt("user_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price")));
            }
            transactionTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {
            String json = gson.toJson(transactions);
            Files.write(Paths.get(DATA_FILE), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            statusLabel.setText("Status: Failed to save data!");
        }
    }

    private void loadData() {
        try {
            if (Files.exists(Paths.get(DATA_FILE))) {
                String json = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
                Type listType = new TypeToken<ObservableList<Transaction>>(){}.getType();
                ObservableList<Transaction> loaded = gson.fromJson(json, listType);
                if (loaded != null) {
                    transactions.setAll(loaded);
                    nextId = transactions.stream().mapToInt(Transaction::getTransactionId).max().orElse(0) + 1;
                }
            }
        } catch (Exception e) {
            statusLabel.setText("Status: Failed to load data!");
        }
    }

    private void refreshTable() {
        transactions.setAll(provider.getAllTransactions());
    }

    @FXML
    private void handleAddTransaction() {
        try {
            int userId = Integer.parseInt(userIdField.getText());
            int productId = Integer.parseInt(productIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            double totalPrice = Double.parseDouble(totalPriceField.getText());
            Transaction t = new Transaction(0, userId, productId, quantity, totalPrice);
            provider.saveTransaction(t);
            refreshTable();
            clearFields();
            statusLabel.setText("Status: Transaction Added!");
        } catch (Exception e) {
            statusLabel.setText("Status: Invalid Input!");
        }
    }

    @FXML
    private void handleUpdateTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setUserId(Integer.parseInt(userIdField.getText()));
                selected.setProductId(Integer.parseInt(productIdField.getText()));
                selected.setQuantity(Integer.parseInt(quantityField.getText()));
                selected.setTotalPrice(Double.parseDouble(totalPriceField.getText()));
                provider.updateTransaction(selected);
                refreshTable();
                statusLabel.setText("Status: Transaction Updated!");
            } catch (Exception e) {
                statusLabel.setText("Status: Invalid Input!");
            }
        } else {
            statusLabel.setText("Status: Select a row to update!");
        }
    }

    @FXML
    private void handleDeleteTransaction() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            provider.deleteTransaction(selected);
            refreshTable();
            clearFields();
            statusLabel.setText("Status: Transaction Deleted!");
        } else {
            statusLabel.setText("Status: Select a row to delete!");
        }
    }

    private void clearFields() {
        userIdField.clear();
        productIdField.clear();
        quantityField.clear();
        totalPriceField.clear();
    }

    @FXML
    private void handlePrint() {
        try {
            List<Transaction> transactions = new ArrayList<>(transactionTable.getItems());
            System.out.println("=== PRINTING TABLE TRANSACTIONS ===");
            for (Transaction t : transactions) {
                System.out.println("Transaction ID: " + t.getTransactionId());
                System.out.println("User ID: " + t.getUserId());
                System.out.println("Product ID: " + t.getProductId());
                System.out.println("Quantity: " + t.getQuantity());
                System.out.println("Total Price: " + t.getTotalPrice());
                System.out.println();
                notifier.notifyObservers(t);
            }
            statusLabel.setText("Status: Print Successful!");
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
        } catch (Exception e) {
            System.err.println("Gagal print transaksi: " + e.getMessage());
            statusLabel.setText("Status: Print Failed!");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void handleExportPDF() {
        if (connection == null) {
            statusLabel.setText("Status: No Database Connection");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Add title
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
                Paragraph title = new Paragraph("Transaction Report", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                // Create table
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);

                // Add headers
                String[] headers = {"ID", "User ID", "Product ID", "Quantity", "Total Price"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(cell);
                }

                // Add data
                List<Transaction> data = transactions;
                for (Transaction t : data) {
                    table.addCell(String.valueOf(t.getTransactionId()));
                    table.addCell(String.valueOf(t.getUserId()));
                    table.addCell(String.valueOf(t.getProductId()));
                    table.addCell(String.valueOf(t.getQuantity()));
                    table.addCell(String.valueOf(t.getTotalPrice()));
                }

                document.add(table);
                document.close();

                statusLabel.setText("Status: PDF Export Successful!");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } catch (Exception e) {
                statusLabel.setText("Status: PDF Export Failed!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExportExcel() {
        if (connection == null) {
            statusLabel.setText("Status: No Database Connection");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(transactionTable.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Transactions");

                // Create header row
                Row headerRow = sheet.createRow(0);
                String[] headers = {"ID", "User ID", "Product ID", "Quantity", "Total Price"};
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }

                // Add data
                List<Transaction> data = transactions;
                int rowNum = 1;
                for (Transaction t : data) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(t.getTransactionId());
                    row.createCell(1).setCellValue(t.getUserId());
                    row.createCell(2).setCellValue(t.getProductId());
                    row.createCell(3).setCellValue(t.getQuantity());
                    row.createCell(4).setCellValue(t.getTotalPrice());
                }

                // Auto-size columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write to file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                statusLabel.setText("Status: Excel Export Successful!");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } catch (Exception e) {
                statusLabel.setText("Status: Excel Export Failed!");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                e.printStackTrace();
            }
        }
    }

    // (Opsional) kamu bisa tambahkan fungsi ini jika ingin bisa disconnect secara
    // manual
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                statusLabel.setText("Status: Disconnected");
                printButton.setDisable(true);
            }
        } catch (SQLException e) {
            statusLabel.setText("Status: Disconnect Failed");
            e.printStackTrace();
        }
    }
}
