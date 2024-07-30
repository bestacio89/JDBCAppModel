package GUI;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {

    public MainUI() {
        setTitle("Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Management", new UserManagementUI());
        tabbedPane.addTab("Product Management", new ProductManagementUI());
        tabbedPane.addTab("Supplier Management", new FourniseurManagementUI());
       // Adding the log panel tab

        // Ensure tab changes are smooth
        tabbedPane.addChangeListener(e -> {
            Component selectedComponent = tabbedPane.getSelectedComponent();
            if (selectedComponent instanceof ProductManagementUI) {
                // Trigger refresh or initialization if needed
                ((ProductManagementUI) selectedComponent).refreshTable();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainUI().setVisible(true));
    }
}
