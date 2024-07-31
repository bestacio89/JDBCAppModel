package GUI;

import DAOs.Compo.CompoDAO;
import Entities.Compo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompoUI extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(CompoUI.class.getName());

    private final CompoDAO compoDAO = new CompoDAO();
    private DefaultTableModel tableModel;
    private JTable compoTable;
    private JTextField articleIdField;
    private JTextField bonIdField;
    private JTextField quantityField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton saveButton;
    private int selectedCompoId = -1;

    public CompoUI() {
        setLayout(new BorderLayout());

        // Table Model
        tableModel = new DefaultTableModel(new Object[]{"ID", "Article ID", "Bon ID", "Quantity"}, 0);
        compoTable = new JTable(tableModel) {
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
        compoTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(compoTable), BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Article ID:"), gbc);

        articleIdField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(articleIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Bon ID:"), gbc);

        bonIdField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(bonIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Quantity:"), gbc);

        quantityField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        addButton = new JButton("Add Compo");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(addButton, gbc);

        deleteButton = new JButton("Delete Compo");
        gbc.gridx = 1;
        formPanel.add(deleteButton, gbc);

        editButton = new JButton("Edit Compo");
        gbc.gridx = 2;
        formPanel.add(editButton, gbc);

        saveButton = new JButton("Save Changes");
        gbc.gridx = 3;
        formPanel.add(saveButton, gbc);
        saveButton.setEnabled(false); // Initially disabled

        // Add Listeners
        addButton.addActionListener(e -> addCompo());
        deleteButton.addActionListener(e -> deleteCompo());
        editButton.addActionListener(e -> editCompo());
        saveButton.addActionListener(e -> saveChanges());

        add(tablePanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        refreshTable(); // Initial table load
    }

    private boolean isValidInput(String articleIdStr, String bonIdStr, String quantityStr) {
        try {
            int articleId = Integer.parseInt(articleIdStr);
            int bonId = Integer.parseInt(bonIdStr);
            int quantity = Integer.parseInt(quantityStr);
            return quantity > 0 && articleId > 0 && bonId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addCompo() {
        String articleIdStr = articleIdField.getText();
        String bonIdStr = bonIdField.getText();
        String quantityStr = quantityField.getText();

        if (isValidInput(articleIdStr, bonIdStr, quantityStr)) {
            int articleId = Integer.parseInt(articleIdStr);
            int bonId = Integer.parseInt(bonIdStr);
            int quantity = Integer.parseInt(quantityStr);
            Compo compo = new Compo(0, articleId, bonId, quantity);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    compoDAO.addCompo(compo);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    articleIdField.setText("");
                    bonIdField.setText("");
                    quantityField.setText("");
                    JOptionPane.showMessageDialog(CompoUI.this, "Compo added successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check article ID, bon ID, and quantity.");
        }
    }

    private void deleteCompo() {
        int selectedRow = compoTable.getSelectedRow();
        if (selectedRow != -1) {
            int compoId = (int) tableModel.getValueAt(selectedRow, 0);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    compoDAO.deleteCompo(compoId);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    JOptionPane.showMessageDialog(CompoUI.this, "Compo deleted successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a compo to delete.");
        }
    }

    private void editCompo() {
        int selectedRow = compoTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedCompoId = (int) tableModel.getValueAt(selectedRow, 0);
            String articleId = String.valueOf(tableModel.getValueAt(selectedRow, 1));
            String bonId = String.valueOf(tableModel.getValueAt(selectedRow, 2));
            String quantity = String.valueOf(tableModel.getValueAt(selectedRow, 3));
            articleIdField.setText(articleId);
            bonIdField.setText(bonId);
            quantityField.setText(quantity);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
            saveButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a compo to edit.");
        }
    }

    private void saveChanges() {
        String articleIdStr = articleIdField.getText();
        String bonIdStr = bonIdField.getText();
        String quantityStr = quantityField.getText();

        if (isValidInput(articleIdStr, bonIdStr, quantityStr) && selectedCompoId != -1) {
            int articleId = Integer.parseInt(articleIdStr);
            int bonId = Integer.parseInt(bonIdStr);
            int quantity = Integer.parseInt(quantityStr);
            Compo compo = new Compo(selectedCompoId, articleId, bonId, quantity);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    compoDAO.updateCompo(compo);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    articleIdField.setText("");
                    bonIdField.setText("");
                    quantityField.setText("");
                    addButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    editButton.setEnabled(true);
                    saveButton.setEnabled(false);
                    selectedCompoId = -1;
                    JOptionPane.showMessageDialog(CompoUI.this, "Compo updated successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input or no compo selected for update.");
        }
    }

    private void refreshTable() {
        SwingWorker<List<Compo>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Compo> doInBackground() throws Exception {
                return compoDAO.getAllCompos();
            }

            @Override
            protected void done() {
                try {
                    List<Compo> compos = get();
                    tableModel.setRowCount(0);
                    for (Compo compo : compos) {
                        tableModel.addRow(new Object[]{compo.getId(), compo.getIdArt(), compo.getIdBon(), compo.getQte()});
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to refresh table", e);
                }
            }
        };
        worker.execute();
    }
}
