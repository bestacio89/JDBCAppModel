package GUI;

import DAOs.BonDAO;
import Entities.Bon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class BonManagementUI extends JPanel {
    private final BonDAO bonDAO = new BonDAO();
    private DefaultTableModel tableModel;
    private JTable bonTable;
    private JTextField numeroField;
    private JTextField dateCmdeField;
    private JTextField delaiField;
    private JTextField idFouField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton saveButton;
    private int selectedBonId = -1;

    public BonManagementUI() {
        setLayout(new BorderLayout());

        // Table Model
        tableModel = new DefaultTableModel(new Object[]{"ID", "Numero", "Date Commande", "Delai", "ID_FOU"}, 0);
        bonTable = new JTable(tableModel) {
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
        bonTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(bonTable), BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Numero:"), gbc);

        numeroField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(numeroField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Date Commande:"), gbc);

        dateCmdeField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(dateCmdeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Delai:"), gbc);

        delaiField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(delaiField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("ID_FOU:"), gbc);

        idFouField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(idFouField, gbc);

        addButton = new JButton("Add Bon");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(addButton, gbc);

        deleteButton = new JButton("Delete Bon");
        gbc.gridx = 1;
        formPanel.add(deleteButton, gbc);

        editButton = new JButton("Edit Bon");
        gbc.gridx = 2;
        formPanel.add(editButton, gbc);

        saveButton = new JButton("Save Changes");
        gbc.gridx = 3;
        formPanel.add(saveButton, gbc);
        saveButton.setEnabled(false); // Initially disabled

        // Add Listeners
        addButton.addActionListener(e -> addBon());
        deleteButton.addActionListener(e -> deleteBon());
        editButton.addActionListener(e -> editBon());
        saveButton.addActionListener(e -> saveChanges());

        add(tablePanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        refreshTable(); // Initial table load
    }

    private boolean isValidInput(String numero, String dateCmde, String delai, String idFou) {
        try {
            Integer.parseInt(numero);
            Integer.parseInt(delai);
            Integer.parseInt(idFou);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.parse(dateCmde); // This will throw ParseException if the date format is wrong
            return true;
        } catch (NumberFormatException | ParseException e) {
            return false;
        }
    }

    private void addBon() {
        String numero = numeroField.getText();
        String dateCmde = dateCmdeField.getText();
        String delai = delaiField.getText();
        String idFou = idFouField.getText();

        if (isValidInput(numero, dateCmde, delai, idFou)) {
            try {
                Timestamp timestamp = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateCmde).getTime());
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        bonDAO.addBon(new Bon(0, Integer.parseInt(numero), timestamp, Integer.parseInt(delai), Integer.parseInt(idFou)));
                        return null;
                    }

                    @Override
                    protected void done() {
                        refreshTable();
                        numeroField.setText("");
                        dateCmdeField.setText("");
                        delaiField.setText("");
                        idFouField.setText("");
                        JOptionPane.showMessageDialog(BonManagementUI.this, "Bon added successfully.");
                    }
                };
                worker.execute();
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd HH:mm:ss.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check numero, date, delai, and ID_FOU format.");
        }
    }

    private void deleteBon() {
        int selectedRow = bonTable.getSelectedRow();
        if (selectedRow != -1) {
            int bonId = (int) tableModel.getValueAt(selectedRow, 0);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    bonDAO.deleteBon(bonId);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    JOptionPane.showMessageDialog(BonManagementUI.this, "Bon deleted successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bon to delete.");
        }
    }

    private void editBon() {
        int selectedRow = bonTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedBonId = (int) tableModel.getValueAt(selectedRow, 0);
            String numero = tableModel.getValueAt(selectedRow, 1).toString();
            String dateCmde = tableModel.getValueAt(selectedRow, 2).toString();
            String delai = tableModel.getValueAt(selectedRow, 3).toString();
            String idFou = tableModel.getValueAt(selectedRow, 4).toString();

            numeroField.setText(numero);
            dateCmdeField.setText(dateCmde);
            delaiField.setText(delai);
            idFouField.setText(idFou);

            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
            saveButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bon to edit.");
        }
    }

    private void saveChanges() {
        if (selectedBonId != -1) {
            String numero = numeroField.getText();
            String dateCmde = dateCmdeField.getText();
            String delai = delaiField.getText();
            String idFou = idFouField.getText();

            if (isValidInput(numero, dateCmde, delai, idFou)) {
                try {
                    Timestamp timestamp = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateCmde).getTime());
                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            bonDAO.updateBon(new Bon(selectedBonId, Integer.parseInt(numero), timestamp, Integer.parseInt(delai), Integer.parseInt(idFou)));
                            return null;
                        }

                        @Override
                        protected void done() {
                            refreshTable();
                            numeroField.setText("");
                            dateCmdeField.setText("");
                            delaiField.setText("");
                            idFouField.setText("");
                            addButton.setEnabled(true);
                            deleteButton.setEnabled(true);
                            editButton.setEnabled(true);
                            saveButton.setEnabled(false);
                            selectedBonId = -1;
                            JOptionPane.showMessageDialog(BonManagementUI.this, "Bon updated successfully.");
                        }
                    };
                    worker.execute();
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd HH:mm:ss.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check numero, date, delai, and ID_FOU format.");
            }
        }
    }

    private void refreshTable() {
        SwingWorker<List<Bon>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Bon> doInBackground() throws Exception {
                return bonDAO.getAllBons();
            }

            @Override
            protected void done() {
                try {
                    List<Bon> bons = get();
                    tableModel.setRowCount(0);
                    for (Bon bon : bons) {
                        tableModel.addRow(new Object[]{
                                bon.getId(),
                                bon.getNumero(),
                                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(bon.getDateCmde()),
                                bon.getDelai(),
                                bon.getIdFou()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
