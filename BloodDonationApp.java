import java.awt.*;
import java.util.*;
import javax.swing.*;

class Donor {
    String name, bloodGroup, location, phone, lastDonation;
    
    Donor(String name, String bloodGroup, String location, String phone, String lastDonation) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.location = location;
        this.phone = phone;
        this.lastDonation = lastDonation;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | Last Donation: %s", 
            name, bloodGroup, location, phone, lastDonation);
    }
}

public class BloodDonationApp extends JFrame {
    private JTextField txtName, txtLocation, txtSearch, txtPhone;
    private JComboBox<String> cmbBloodGroup;
    private JComboBox<String> cmbSortBy;
    private DefaultListModel<String> donorListModel;
    private JToggleButton themeToggle;
    private JPanel mainPanel;
    private boolean isDarkTheme = false;
    private Map<String, Integer> bloodGroupStats;
    private JTextField txtLastDonation;

    private java.util.List<Donor> donors = new ArrayList<>();

    public BloodDonationApp() {
        setTitle("Blood Donation Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        bloodGroupStats = new HashMap<>();
        for (String bg : new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"}) {
            bloodGroupStats.put(bg, 0);
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Theme toggle
        themeToggle = new JToggleButton("🌙 Dark Mode");
        themeToggle.addActionListener(e -> toggleTheme());

        // Search and Sort panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        cmbSortBy = new JComboBox<>(new String[]{"Name", "Blood Group", "Location", "Last Donation"});
        JButton btnSort = new JButton("Sort");
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(new JLabel("Sort by:"));
        searchPanel.add(cmbSortBy);
        searchPanel.add(btnSort);

        // Blood group compatibility chart button
        JButton btnCompatibility = new JButton("Blood Compatibility Chart");
        searchPanel.add(btnCompatibility);

        // Export/Import buttons
        JButton btnExport = new JButton("Export");
        JButton btnImport = new JButton("Import");
        searchPanel.add(btnExport);
        searchPanel.add(btnImport);

        // Remove duplicate action listener declarations
        // Action listeners will be set up after components are created

        // Panel for form
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        JLabel lblName = new JLabel("Donor Name:");
        txtName = new JTextField();

        JLabel lblBloodGroup = new JLabel("Blood Group:");
        cmbBloodGroup = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});

        JLabel lblLocation = new JLabel("Location:");
        txtLocation = new JTextField();

        JLabel lblPhone = new JLabel("Phone Number:");
        txtPhone = new JTextField();

        JLabel lblLastDonation = new JLabel("Last Donation Date:");
        txtLastDonation = new JTextField();

        JButton btnRegister = new JButton("Register Donor");

        formPanel.add(lblName);
        formPanel.add(txtName);
        formPanel.add(lblBloodGroup);
        formPanel.add(cmbBloodGroup);
        formPanel.add(lblLocation);
        formPanel.add(txtLocation);
        formPanel.add(lblPhone);
        formPanel.add(txtPhone);
        formPanel.add(lblLastDonation);
        formPanel.add(txtLastDonation);
        formPanel.add(new JLabel()); // empty space
        formPanel.add(btnRegister);

        // Donor list panel
        donorListModel = new DefaultListModel<>();
        JList<String> donorList = new JList<>(donorListModel);
        JScrollPane scrollPane = new JScrollPane(donorList);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Blood Group Statistics"));
        
        // Clear All button
        JButton btnClearAll = new JButton("Clear All");
        btnClearAll.addActionListener(event -> clearAllDonors(statsPanel));

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(themeToggle, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(statsPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(btnClearAll, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        setContentPane(mainPanel);

        // Search functionality
        // Set up all action listeners
        btnSearch.addActionListener(__ -> updateDonorList(txtSearch.getText().trim().toUpperCase()));
        btnSort.addActionListener(__ -> {
            String sortBy = (String) cmbSortBy.getSelectedItem();
            donors.sort((d1, d2) -> switch(sortBy) {
                case "Name" -> d1.name.compareTo(d2.name);
                case "Blood Group" -> d1.bloodGroup.compareTo(d2.bloodGroup);
                case "Location" -> d1.location.compareTo(d2.location);
                case "Last Donation" -> d1.lastDonation.compareTo(d2.lastDonation);
                default -> 0;
            });
            updateDonorList("");
        });
        btnCompatibility.addActionListener(__ -> showBloodCompatibilityChart());
        btnExport.addActionListener(__ -> exportDonors());
        btnImport.addActionListener(__ -> importDonors());

        // Initialize stats
        updateStatsPanel(statsPanel);

        // Button action
        btnRegister.addActionListener(e -> {
            String name = txtName.getText().trim();
            String bloodGroup = (String) cmbBloodGroup.getSelectedItem();
            String location = txtLocation.getText().trim();
            String phone = txtPhone.getText().trim();
            String lastDonation = txtLastDonation.getText().trim();

            if (name.isEmpty() || location.isEmpty() || phone.isEmpty()) {
                if (name.isEmpty()) {
                    txtName.setBorder(BorderFactory.createLineBorder(Color.RED));
                    JOptionPane.showMessageDialog(null, "Please enter donor name!");
                    return;
                }
                if (location.isEmpty()) {
                    txtLocation.setBorder(BorderFactory.createLineBorder(Color.RED));
                    JOptionPane.showMessageDialog(null, "Please enter location!");
                    return;
                }
                if (phone.isEmpty()) {
                    txtPhone.setBorder(BorderFactory.createLineBorder(Color.RED));
                    JOptionPane.showMessageDialog(null, "Please enter phone number!");
                    return;
                }
            }

            txtName.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
            txtLocation.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
            txtPhone.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

            Donor donor = new Donor(name, bloodGroup, location, phone, lastDonation.isEmpty() ? "N/A" : lastDonation);
            donors.add(donor);
            donorListModel.addElement(donor.toString());
            
            // Update statistics
            bloodGroupStats.put(bloodGroup, bloodGroupStats.get(bloodGroup) + 1);
            updateStatsPanel((JPanel)mainPanel.getComponent(1));
            
            JOptionPane.showMessageDialog(null, "Donor Registered Successfully!");

            // Clear fields
            txtName.setText("");
            txtLocation.setText("");
            txtPhone.setText("");
            txtLastDonation.setText("");
        });
    }

    // Action methods have been moved inline with lambda expressions

    private void exportDonors() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                for (Donor donor : donors) {
                    writer.println(String.format("%s,%s,%s,%s,%s", 
                        donor.name, donor.bloodGroup, donor.location, donor.phone, donor.lastDonation));
                }
                JOptionPane.showMessageDialog(this, "Donors exported successfully!");
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting donors: " + ex.getMessage());
            }
        }
    }

    private void importDonors() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.util.Scanner scanner = new java.util.Scanner(fileChooser.getSelectedFile())) {
                donors.clear();
                donorListModel.clear();
                resetStats();
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    if (parts.length == 5) {
                        Donor donor = new Donor(parts[0], parts[1], parts[2], parts[3], parts[4]);
                        donors.add(donor);
                        donorListModel.addElement(donor.toString());
                        bloodGroupStats.put(parts[1], bloodGroupStats.getOrDefault(parts[1], 0) + 1);
                    }
                }
                updateStatsPanel((JPanel)mainPanel.getComponent(1));
                JOptionPane.showMessageDialog(this, "Donors imported successfully!");
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "Error importing donors: " + ex.getMessage());
            }
        }
    }

    private void clearAllDonors(JPanel statsPanel) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear all donors?",
            "Confirm Clear All",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            donors.clear();
            donorListModel.clear();
            resetStats();
            updateStatsPanel(statsPanel);
        }
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        Color bgColor = isDarkTheme ? new Color(50, 50, 50) : UIManager.getColor("Panel.background");
        Color fgColor = isDarkTheme ? Color.WHITE : UIManager.getColor("Label.foreground");
        
        mainPanel.setBackground(bgColor);
        for (Component comp : mainPanel.getComponents()) {
            updateComponentColors(comp, bgColor, fgColor);
        }
        
        themeToggle.setText(isDarkTheme ? "☀️ Light Mode" : "🌙 Dark Mode");
    }

    private void updateComponentColors(Component comp, Color bg, Color fg) {
        comp.setBackground(bg);
        if (comp instanceof JLabel || comp instanceof JButton || comp instanceof JToggleButton) {
            comp.setForeground(fg);
        }
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                updateComponentColors(child, bg, fg);
            }
        }
    }

    private void resetStats() {
        for (String bg : bloodGroupStats.keySet()) {
            bloodGroupStats.put(bg, 0);
        }
    }

    private void updateStatsPanel(JPanel statsPanel) {
        statsPanel.removeAll();
        for (Map.Entry<String, Integer> entry : bloodGroupStats.entrySet()) {
            statsPanel.add(new JLabel(entry.getKey() + ":"));
            statsPanel.add(new JLabel(entry.getValue().toString()));
        }
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void updateDonorList(String searchTerm) {
        donorListModel.clear();
        for (Donor donor : donors) {
            if (searchTerm.isEmpty() || 
                donor.bloodGroup.contains(searchTerm) || 
                donor.location.toUpperCase().contains(searchTerm) ||
                donor.name.toUpperCase().contains(searchTerm)) {
                donorListModel.addElement(donor.toString());
            }
        }
    }

    private void showBloodCompatibilityChart() {
        JDialog dialog = new JDialog(this, "Blood Type Compatibility Chart", true);
        dialog.setLayout(new BorderLayout());
        
        String[][] data = {
            {"Blood Type", "Can Give To", "Can Receive From"},
            {"A+", "A+, AB+", "A+, A-, O+, O-"},
            {"A-", "A+, A-, AB+, AB-", "A-, O-"},
            {"B+", "B+, AB+", "B+, B-, O+, O-"},
            {"B-", "B+, B-, AB+, AB-", "B-, O-"},
            {"AB+", "AB+", "All Types"},
            {"AB-", "AB+, AB-", "A-, B-, AB-, O-"},
            {"O+", "O+, A+, B+, AB+", "O+, O-"},
            {"O-", "All Types", "O-"}
        };
        
        String[] columnNames = {"Blood Type", "Can Give To", "Can Receive From"};
        JTable table = new JTable(data, columnNames);
        table.setEnabled(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(__ -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);
        
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Error setting look and feel: " + ex.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            new BloodDonationApp().setVisible(true);
        });
    }
}
