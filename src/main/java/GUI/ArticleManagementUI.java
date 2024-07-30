package GUI;

import DAOs.ArticleDAO;
import Entities.Article;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ArticleManagementUI extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(ArticleManagementUI.class.getName());

    private final ArticleDAO articleDAO = new ArticleDAO();
    private DefaultTableModel tableModel;
    private JTable articleTable;
    private JTextField refField;
    private JTextField designationField;
    private JTextField priceField;
    private JTextField idFouField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton saveButton;
    private int selectedArticleId = -1;

    public ArticleManagementUI() {
        setLayout(new BorderLayout());

        // Table Model
        tableModel = new DefaultTableModel(new Object[]{"ID", "Reference", "Designation", "Price", "Supplier ID"}, 0);
        articleTable = new JTable(tableModel) {
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
        articleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(articleTable), BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Reference:"), gbc);

        refField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(refField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Designation:"), gbc);

        designationField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(designationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Price:"), gbc);

        priceField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Supplier ID:"), gbc);

        idFouField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(idFouField, gbc);

        addButton = new JButton("Add Article");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(addButton, gbc);

        deleteButton = new JButton("Delete Article");
        gbc.gridx = 1;
        formPanel.add(deleteButton, gbc);

        editButton = new JButton("Edit Article");
        gbc.gridx = 2;
        formPanel.add(editButton, gbc);

        saveButton = new JButton("Save Changes");
        gbc.gridx = 3;
        formPanel.add(saveButton, gbc);
        saveButton.setEnabled(false); // Initially disabled

        // Add Listeners
        addButton.addActionListener(e -> addArticle());
        deleteButton.addActionListener(e -> deleteArticle());
        editButton.addActionListener(e -> editArticle());
        saveButton.addActionListener(e -> saveChanges());

        add(tablePanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        refreshTable(); // Initial table load
    }

    private boolean isValidInput(String ref, String designation, String priceStr, String idFouStr) {
        try {
            double price = Double.parseDouble(priceStr);
            int idFou = Integer.parseInt(idFouStr);
            return ref != null && !ref.trim().isEmpty() &&
                   designation != null && !designation.trim().isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addArticle() {
        String ref = refField.getText();
        String designation = designationField.getText();
        String priceStr = priceField.getText();
        String idFouStr = idFouField.getText();

        if (isValidInput(ref, designation, priceStr, idFouStr)) {
            double price = Double.parseDouble(priceStr);
            int idFou = Integer.parseInt(idFouStr);
            Article article = new Article(0, ref, designation, price, idFou);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    articleDAO.addArticle(article);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    refField.setText("");
                    designationField.setText("");
                    priceField.setText("");
                    idFouField.setText("");
                    JOptionPane.showMessageDialog(ArticleManagementUI.this, "Article added successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check reference, designation, price, and supplier ID format.");
        }
    }

    private void deleteArticle() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow != -1) {
            int articleId = (int) tableModel.getValueAt(selectedRow, 0);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    articleDAO.deleteArticle(articleId);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    JOptionPane.showMessageDialog(ArticleManagementUI.this, "Article deleted successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an article to delete.");
        }
    }

    private void editArticle() {
        int selectedRow = articleTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedArticleId = (int) tableModel.getValueAt(selectedRow, 0);
            String ref = (String) tableModel.getValueAt(selectedRow, 1);
            String designation = (String) tableModel.getValueAt(selectedRow, 2);
            String price = String.valueOf(tableModel.getValueAt(selectedRow, 3));
            String idFou = String.valueOf(tableModel.getValueAt(selectedRow, 4));
            refField.setText(ref);
            designationField.setText(designation);
            priceField.setText(price);
            idFouField.setText(idFou);
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
            saveButton.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an article to edit.");
        }
    }

    private void saveChanges() {
        String ref = refField.getText();
        String designation = designationField.getText();
        String priceStr = priceField.getText();
        String idFouStr = idFouField.getText();

        if (isValidInput(ref, designation, priceStr, idFouStr) && selectedArticleId != -1) {
            double price = Double.parseDouble(priceStr);
            int idFou = Integer.parseInt(idFouStr);
            Article article = new Article(selectedArticleId, ref, designation, price, idFou);
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    articleDAO.updateArticle(article);
                    return null;
                }

                @Override
                protected void done() {
                    refreshTable();
                    refField.setText("");
                    designationField.setText("");
                    priceField.setText("");
                    idFouField.setText("");
                    addButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    editButton.setEnabled(true);
                    saveButton.setEnabled(false);
                    selectedArticleId = -1;
                    JOptionPane.showMessageDialog(ArticleManagementUI.this, "Article updated successfully.");
                }
            };
            worker.execute();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid input or no article selected for update.");
        }
    }

    private void refreshTable() {
        SwingWorker<List<Article>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Article> doInBackground() throws Exception {
                return articleDAO.getAllArticles();
            }

            @Override
            protected void done() {
                try {
                    List<Article> articles = get();
                    tableModel.setRowCount(0);
                    for (Article article : articles) {
                        tableModel.addRow(new Object[]{article.getId(), article.getRef(), article.getDesignation(), article.getPrix(), article.getIdFou()});
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to refresh table", e);
                }
            }
        };
        worker.execute();
    }
}
