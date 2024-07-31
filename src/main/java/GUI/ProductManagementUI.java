package GUI;

import DAOs.Product.ProductDAO;
import Entities.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductManagementUI extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(ProductManagementUI.class.getName());

    private final ProductDAO productDAO = new ProductDAO();
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField priceField;
    private JTextField quantityField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton saveButton;
    private int selectedProductId = -1;

    public ProductManagementUI() {
        setLayout(new BorderLayout());

        // Table Model
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Description", "Price", "Quantity"}, 0);
        productTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }

            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? getBackground() : new Color(240, 240, 240));
                }
                return c;
            }
        };
        productTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);

        descriptionField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Price:"), gbc);

        priceField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Quantity:"), gbc);

        quantityField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        addButton = new JButton("Add Product");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(addButton, gbc);

        deleteButton = new JButton("Delete Product");
        gbc.gridx = 1;
        formPanel.add(deleteButton, gbc);

        editButton = new JButton("Edit Product");
        gbc.gridx = 2;
        formPanel.add(editButton, gbc);

        saveButton = new JButton("Save Changes");
        gbc.gridx = 3;
        formPanel.add(saveButton, gbc);
        saveButton.setEnabled(false); // Initially disabled

        // Add Listeners
        addButton.addActionListener(e -> addProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        editButton.addActionListener(e -> editProduct());
        saveButton.addActionListener(e -> saveChanges());

        add(tablePanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        refreshTable(); // Initial table load
    }

    private boolean isValidInput(String name, String price, String quantity) {
        try {
            Double.parseDouble(price);
            Integer.parseInt(quantity);
            return name != null && !name.trim().isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String price = priceField.getText();
        String quantity = quantityField.getText();

        if (isValidInput(name, price, quantity)) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        productDAO.addProduct(name, description, Double.parseDouble(price), Integer.parseInt(quantity));
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to add product", e);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        refreshTable();
                        nameField.setText("");
                        descriptionField.setText("");
                        priceField.setText("");
                        quantityField.setText("");
                        JOptionPane.showMessageDialog(ProductManagementUI.this, "Product added successfully.");
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to refresh table after adding product", e);
                    }
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check name, price, and quantity format.");
        }
    }

    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        productDAO.deleteProduct(productId);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to delete product", e);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        refreshTable();
                        JOptionPane.showMessageDialog(ProductManagementUI.this, "Product deleted successfully.");
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to refresh table after deleting product", e);
                    }
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
        }
    }

    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedProductId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String description = (String) tableModel.getValueAt(selectedRow, 2);
            String price = (String) tableModel.getValueAt(selectedRow, 3);
            String quantity = (String) tableModel.getValueAt(selectedRow, 4);
            nameField.setText(name);
            descriptionField.setText(description);
            priceField.setText(price);
            quantityField.setText(quantity);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
            saveButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.");
        }
    }

    private void saveChanges() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String price = priceField.getText();
        String quantity = quantityField.getText();

        if (isValidInput(name, price, quantity) && selectedProductId != -1) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    try {
                        productDAO.updateProduct(selectedProductId, name, description, Double.parseDouble(price), Integer.parseInt(quantity));
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to update product", e);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        refreshTable();
                        nameField.setText("");
                        descriptionField.setText("");
                        priceField.setText("");
                        quantityField.setText("");
                        addButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                        editButton.setEnabled(true);
                        saveButton.setEnabled(false);
                        selectedProductId = -1;
                        JOptionPane.showMessageDialog(ProductManagementUI.this, "Product updated successfully.");
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to refresh table after updating product", e);
                    }
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input or no product selected for update.");
        }
    }

    void refreshTable() {
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() {
                try {
                    return productDAO.getAllProducts();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to get products", e);
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    tableModel.setRowCount(0);
                    if (products != null) {
                        for (Product product : products) {
                            tableModel.addRow(new Object[]{product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getQuantity()});
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to refresh table data", e);
                }
            }
        };
        worker.execute();
    }
}
