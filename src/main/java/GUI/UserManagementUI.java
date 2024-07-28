package GUI;

import DAOs.UserDAO;
import Entities.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserManagementUI extends JFrame {
    private final UserDAO userDAO = new UserDAO();
    private DefaultTableModel tableModel;
    private JTable userTable;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField discordField;
    private JTextField linkedinField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton saveButton;
    private int selectedUserId = -1;

    public UserManagementUI() {
        setTitle("User Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table Model
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Discord Name", "LinkedIn URL"}, 0);
        userTable = new JTable(tableModel) {
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
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(userTable), BorderLayout.CENTER);

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
        formPanel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Discord Name:"), gbc);

        discordField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(discordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("LinkedIn URL:"), gbc);

        linkedinField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(linkedinField, gbc);

        addButton = new JButton("Add User");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(addButton, gbc);

        deleteButton = new JButton("Delete User");
        gbc.gridx = 1;
        formPanel.add(deleteButton, gbc);

        editButton = new JButton("Edit User");
        gbc.gridx = 2;
        formPanel.add(editButton, gbc);

        saveButton = new JButton("Save Changes");
        gbc.gridx = 3;
        formPanel.add(saveButton, gbc);
        saveButton.setEnabled(false); // Initially disabled

        // Add Listeners
        addButton.addActionListener(e -> addUser());
        deleteButton.addActionListener(e -> deleteUser());
        editButton.addActionListener(e -> editUser());
        saveButton.addActionListener(e -> saveChanges());

        add(tablePanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        refreshTable(); // Initial table load
    }

    private boolean isValidInput(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.contains("@");
    }

    private void addUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String discordName = discordField.getText();
        String linkedinUrl = linkedinField.getText();
        if (isValidInput(name, email)) {
            userDAO.addUser(name, email, discordName, linkedinUrl);
            refreshTable();
            nameField.setText("");
            emailField.setText("");
            discordField.setText("");
            linkedinField.setText("");
            JOptionPane.showMessageDialog(this, "User added successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check name and email format.");
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            userDAO.deleteUser(userId);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedUserId = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String email = (String) tableModel.getValueAt(selectedRow, 2);
            String discordName = (String) tableModel.getValueAt(selectedRow, 3);
            String linkedinUrl = (String) tableModel.getValueAt(selectedRow, 4);
            nameField.setText(name);
            emailField.setText(email);
            discordField.setText(discordName);
            linkedinField.setText(linkedinUrl);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
            saveButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }

    private void saveChanges() {
        String name = nameField.getText();
        String email = emailField.getText();
        String discordName = discordField.getText();
        String linkedinUrl = linkedinField.getText();
        if (isValidInput(name, email) && selectedUserId != -1) {
            userDAO.updateUser(selectedUserId, name, email, discordName, linkedinUrl);
            refreshTable();
            nameField.setText("");
            emailField.setText("");
            discordField.setText("");
            linkedinField.setText("");
            addButton.setEnabled(true);
            deleteButton.setEnabled(true);
            editButton.setEnabled(true);
            saveButton.setEnabled(false);
            selectedUserId = -1;
            JOptionPane.showMessageDialog(this, "User updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input or no user selected for update.");
        }
    }

    private void refreshTable() {
        List<User> users = userDAO.getAllUsers();
        tableModel.setRowCount(0);
        for (User user : users) {
            tableModel.addRow(new Object[]{user.getId(), user.getName(), user.getEmail(), user.getDiscordName(), user.getLinkedinUrl()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserManagementUI().setVisible(true));
    }
}
