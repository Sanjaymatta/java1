

//jdbc:mysql://localhost:3306/restaurant","root","sanjay s3"
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class RestaurantBillApp extends JFrame {
     JComboBox<String> categoryComboBox;
     JComboBox<String> itemComboBox;
     JTextField quantityField;
     JButton addButton;
     JButton generateBillButton;
     JButton resetButton;
     JButton deleteButton;
     DefaultTableModel tableModel;
     JTable orderTable;
     JLabel priceLabel;
     Connection connection;
     Map<String, Double> itemPrices;

    public RestaurantBillApp() {
        super("Restaurant Bill Generator");
        initComponents();
        setupDatabase();
        fetchItemPrices();
        populateCategories();
        
    }

    public void initComponents() {
        categoryComboBox = new JComboBox<>();
        itemComboBox = new JComboBox<>();
        quantityField = new JTextField(5);
        addButton = new JButton("Add Item");
        generateBillButton = new JButton("Generate Bill");
        resetButton = new JButton("Reset");

        tableModel = new DefaultTableModel(new Object[]{"Item", "Price", "Quantity", "Total"}, 0);
        orderTable = new JTable(tableModel);

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Category:"));
        panel.add(categoryComboBox);
        panel.add(new JLabel("Item:"));
        panel.add(itemComboBox);
        panel.add(new JLabel("Price:")); // Add label for price
        priceLabel = new JLabel();
        panel.add(priceLabel);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(addButton);
        panel.add(generateBillButton);
        panel.add(resetButton);
        deleteButton = new JButton("Delete Selected Item");
        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(orderTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> addItemToOrder());
        generateBillButton.addActionListener(e -> generateBill());
        resetButton.addActionListener(e -> resetOrder());
  
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow != -1) {
                // Enable delete button when a row is selected
                deleteButton.setEnabled(true);
            } else {
                // Disable delete button when no row is selected
                deleteButton.setEnabled(false);
            }
        });
    
        // Add a delete button to delete selected items
        JButton deleteButton = new JButton("Delete Selected Item");
        deleteButton.addActionListener(e -> deleteSelectedItem());
        panel.add(deleteButton);
    
        // Add a modify quantity button to modify the quantity of selected item
        JButton modifyButton = new JButton("Modify Quantity");
        modifyButton.addActionListener(e -> modifyQuantity());
        panel.add(modifyButton);


        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            populateItems(selectedCategory);
        });
        itemComboBox.addActionListener(e -> displayItemPrice());
    }

    private void setupDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurant","root","sanjay s3");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!");
            System.exit(1);
        }
    }

    private void populateCategories() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT category FROM items");
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                categoryComboBox.addItem(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching categories!");
        }
    }

    private void fetchItemPrices() {
        itemPrices = new HashMap<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name, price FROM items");
            while (resultSet.next()) {
                String itemName = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                itemPrices.put(itemName, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching item prices!");
        }
    }

    private void populateItems(String category) {
        itemComboBox.removeAllItems();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM items WHERE category = ?");
            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String itemName = resultSet.getString("name");
                itemComboBox.addItem(itemName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching items!");
        }
    }

    private void addItemToOrder() {
        String item = (String) itemComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = itemPrices.get(item);
        double total = price * quantity;
    
        // Check if the item already exists in the order table
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String itemName = (String) tableModel.getValueAt(i, 0);
            if (itemName.equals(item)) {
                // Update quantity and total for existing item
                int existingQuantity = (int) tableModel.getValueAt(i, 2);
                double existingTotal = (double) tableModel.getValueAt(i, 3);
                int newQuantity = existingQuantity + quantity;
                double newTotal = existingTotal + total;
                tableModel.setValueAt(newQuantity, i, 2);
                tableModel.setValueAt(newTotal, i, 3);
                // Clear input fields after updating quantity
                itemComboBox.setSelectedIndex(-1);
                quantityField.setText("");
                return; // Exit method after updating existing item
            }
        }
    
        // If item doesn't exist, add a new row
        Object[] row = {item, price, quantity, total};
        tableModel.addRow(row);
    
        // Clear input fields after adding item to order
        itemComboBox.setSelectedIndex(-1);
        quantityField.setText("");
    }
    private void generateBill() {
        // Calculate total bill amount
        double total = 0;
        StringBuilder billDetails = new StringBuilder("Bill Details:\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            double itemTotal = (double) tableModel.getValueAt(i, 3);
            total += itemTotal;
        }
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String item = (String) tableModel.getValueAt(i, 0);
            double price = (double) tableModel.getValueAt(i, 1);
            int quantity = (int) tableModel.getValueAt(i, 2);
            double itemTotal = (double) tableModel.getValueAt(i, 3);
    
            billDetails.append(item).append(" (Qty: ").append(quantity).append(") - $").append(price).append(" each | Total: $").append(itemTotal).append("\n");
            total += itemTotal;
        }
        billDetails.append("\nTotal Bill Amount: $").append(total);
    
        // Insert order into the orders table
        if(total>0){
        try {
            String insertOrderSQL = "INSERT INTO orders (order_date, total_amount) VALUES (?, ?)";
            PreparedStatement orderStatement = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
            orderStatement.setDate(1, new java.sql.Date(System.currentTimeMillis())); // Assuming order date is current date
            orderStatement.setDouble(2, total);
            orderStatement.executeUpdate();
    
            // Get the generated order ID
            ResultSet generatedKeys = orderStatement.getGeneratedKeys();
            int orderId = -1;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve the generated order ID.");
            }
    
            // Insert order items into the order_items table
            String insertOrderItemSQL = "INSERT INTO order_items (order_id, item_id, quantity, item_price) VALUES (?, ?, ?, ?)";
            PreparedStatement orderItemStatement = connection.prepareStatement(insertOrderItemSQL);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String item = (String) tableModel.getValueAt(i, 0);
                double price = (double) tableModel.getValueAt(i, 1);
                int quantity = (int) tableModel.getValueAt(i, 2);
                double itemTotal = (double) tableModel.getValueAt(i, 3);
    
                // Get the item ID based on item name
                int itemId = getItemIdByName(item);
    
                // Insert order item
                orderItemStatement.setInt(1, orderId);
                orderItemStatement.setInt(2, itemId);
                orderItemStatement.setInt(3, quantity);
                orderItemStatement.setDouble(4, price);
                orderItemStatement.executeUpdate();
            }
    
            JOptionPane.showMessageDialog(null, billDetails);
            resetOrder(); // Reset the order table after generating the bill
    
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to generate bill. Please try again.");
        }
    }
    else JOptionPane.showMessageDialog(null, "Add atleast one item to Generate Bill");
        resetOrder();
    }
    

    private void resetOrder() {
        tableModel.setRowCount(0);
    }
    public void displayItemPrice() {
        String selectedItem = (String) itemComboBox.getSelectedItem();
        if (selectedItem != null) {
            double price = itemPrices.getOrDefault(selectedItem, 0.0);
            priceLabel.setText("$" + price);
        }
    }
    private void deleteSelectedItem() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        }
    }
    
    private void modifyQuantity() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            int newQuantity = Integer.parseInt(JOptionPane.showInputDialog("Enter new quantity:"));
            if (newQuantity > 0) {
                double price = (double) tableModel.getValueAt(selectedRow, 1);
                double total = price * newQuantity;
                tableModel.setValueAt(newQuantity, selectedRow, 2);
                tableModel.setValueAt(total, selectedRow, 3);
            } else {
                JOptionPane.showMessageDialog(null, "Quantity must be greater than 0.");
            }
        }
    }
    private int getItemIdByName(String itemName) throws SQLException {
        String getItemIdSQL = "SELECT id FROM items WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(getItemIdSQL)) {
            statement.setString(1, itemName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new SQLException("Item not found: " + itemName);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RestaurantBillApp app = new RestaurantBillApp();
            app.setSize(1200, 1200);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
        });
    }
}
