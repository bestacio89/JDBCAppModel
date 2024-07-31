package GUI;

import DAOs.Fourniseur.FourniseurDAO;
import Entities.Fourniseur;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FourniseurManagementUI extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(FourniseurManagementUI.class.getName());

    private final FourniseurDAO fournisseurDAO = new FourniseurDAO();
    private DefaultTableModel tableModel;
    private JTable fournisseurTable;
    private JTextField nameField;
    private JTextField contactPersonField;
    private JTextField emailField;
    private JTextField phoneNumberField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton saveButton;
    private int selectedFournisseurId = -1;

    public FourniseurManagementUI() {
        setLayout(new BorderLayout());

        // Table Model
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact Person", "Email", "Phone Number"}, 0);
        fournisseurTable = new JTable(tableModel) {
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
        fournisseurTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(fournisseurTable), BorderLayout.CENTER);

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
        formPanel.add(new JLabel("Contact Person:"), gbc);

        contactPersonField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(contactPersonField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Phone Number:"), gbc);

        phoneNumberField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(phoneNumberField, gbc);

        addButton = new JButton("Add Fournisseur");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(addButton, gbc);

        deleteButton = new JButton("Delete Fournisseur");
        gbc.gridx = 1;
        formPanel.add(deleteButton, gbc);

        editButton = new JButton("Edit Fournisseur");
        gbc.gridx = 2;
        formPanel.add(editButton, gbc);

        saveButton = new JButton("Save Changes");
        gbc.gridx = 3;
        formPanel.add(saveButton, gbc);
        saveButton.setEnabled(false); // Initially disabled

        // Add Listeners
        addButton.addActionListener(e -> addFourniseur());
        deleteButton.addActionListener(e -> deleteFourniseur());
        editButton.addActionListener(e -> editFourniseur());
        saveButton.addActionListener(e -> saveChanges());

        add(tablePanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        refreshTable(); // Initial table load
    }

    private boolean isValidInput(String name, String email, String phoneNumber) {
        return name != null && !name.trim().isEmpty() &&
               email != null && email.contains("@") &&
               phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    private void addFourniseur() {
        String name = nameField.getText();
        String contactPerson = contactPersonField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();

        if (isValidInput(name, email, phoneNumber)) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    fournisseurDAO.addFourniseur(name, contactPerson, email, phoneNumber);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    nameField.setText("");
                    contactPersonField.setText("");
                    emailField.setText("");
                    phoneNumberField.setText("");
                    JOptionPane.showMessageDialog(FourniseurManagementUI.this, "Fournisseur added successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check the fields.");
        }
    }

    private void deleteFourniseur() {
        int selectedRow = fournisseurTable.getSelectedRow();
        if (selectedRow != -1) {
            int fournisseurId = (int) tableModel.getValueAt(selectedRow, 0);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    fournisseurDAO.deleteFournisseur(fournisseurId);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    JOptionPane.showMessageDialog(FourniseurManagementUI.this, "Fournisseur deleted successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a fournisseur to delete.");
        }
    }

    private void editFourniseur() {
        int selectedRow = fournisseurTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedFournisseurId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String contactPerson = (String) tableModel.getValueAt(selectedRow, 2);
            String email = (String) tableModel.getValueAt(selectedRow, 3);
            String phoneNumber = (String) tableModel.getValueAt(selectedRow, 4);
            nameField.setText(name);
            contactPersonField.setText(contactPerson);
            emailField.setText(email);
            phoneNumberField.setText(phoneNumber);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
            saveButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a fournisseur to edit.");
        }
    }

    private void saveChanges() {
        String name = nameField.getText();
        String contactPerson = contactPersonField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();

        if (isValidInput(name, email, phoneNumber) && selectedFournisseurId != -1) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    fournisseurDAO.updateFourniseur(selectedFournisseurId, name, contactPerson, email, phoneNumber);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    nameField.setText("");
                    contactPersonField.setText("");
                    emailField.setText("");
                    phoneNumberField.setText("");
                    addButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    editButton.setEnabled(true);
                    saveButton.setEnabled(false);
                    selectedFournisseurId = -1;
                    JOptionPane.showMessageDialog(FourniseurManagementUI.this, "Fournisseur updated successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input or no fournisseur selected for update.");
        }
    }

    private void refreshTable() {
        SwingWorker<List<Fourniseur>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Fourniseur> doInBackground() throws Exception {
                return fournisseurDAO.getAllFourniseurs();
            }

            @Override
            protected void done() {
                try {
                    List<Fourniseur> fournisseurs = get();
                    tableModel.setRowCount(0);
                    for (Fourniseur fournisseur : fournisseurs) {
                        tableModel.addRow(new Object[]{fournisseur.getId(), fournisseur.getName(), fournisseur.getContactPerson(), fournisseur.getEmail(), fournisseur.getPhoneNumber()});
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to refresh table", e);
                }
            }
        };
        worker.execute();
    }
}
