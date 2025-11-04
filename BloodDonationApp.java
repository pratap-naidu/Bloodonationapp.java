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
        JButton btnBack = new JButton("🔙 Back");
        JComboBox<String> searchBy = new JComboBox<>(new String[]{"All", "Name", "Blood Group", "Location", "Phone"});
        cmbSortBy = new JComboBox<>(new String[]{"Name", "Blood Group", "Location", "Last Donation"});
        JButton btnSort = new JButton("Sort");
        
        searchPanel.add(btnBack);
        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(searchBy);
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

        // Add input field validation listeners
        txtPhone.setToolTipText("Enter a 10-digit phone number");
        txtPhone.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validatePhone(); }

            private void validatePhone() {
                String phone = txtPhone.getText().trim();
                if (!phone.isEmpty() && !phone.matches("\\d{0,10}")) {
                    txtPhone.setForeground(Color.RED);
                } else {
                    txtPhone.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
                }
            }
        });

        txtLastDonation.setToolTipText("Enter date in DD/MM/YYYY format");
        txtLastDonation.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateDate(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateDate(); }
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateDate(); }

            private void validateDate() {
                String date = txtLastDonation.getText().trim();
                if (!date.isEmpty() && !isValidDate(date)) {
                    txtLastDonation.setForeground(Color.RED);
                } else {
                    txtLastDonation.setForeground(isDarkTheme ? Color.WHITE : Color.BLACK);
                }
            }
        });

        // Search, Sort and Back functionality
        JComboBox<String> searchByRef = searchBy;
        btnSearch.addActionListener(__ -> {
            String searchTerm = txtSearch.getText().trim().toUpperCase();
            String searchCategory = (String) searchByRef.getSelectedItem();
            updateDonorList(searchTerm, searchCategory);
        });
        
        btnSort.addActionListener(__ -> {
            String sortBy = (String) cmbSortBy.getSelectedItem();
            donors.sort((d1, d2) -> switch(sortBy) {
                case "Name" -> d1.name.compareTo(d2.name);
                case "Blood Group" -> d1.bloodGroup.compareTo(d2.bloodGroup);
                case "Location" -> d1.location.compareTo(d2.location);
                case "Last Donation" -> d1.lastDonation.compareTo(d2.lastDonation);
                default -> 0;
            });
            updateDonorList(txtSearch.getText().trim().toUpperCase(), (String) searchByRef.getSelectedItem());
        });

        // Add back button functionality to clear search and show all donors
        btnBack.addActionListener(__ -> {
            txtSearch.setText("");
            searchBy.setSelectedItem("All");
            cmbSortBy.setSelectedItem("Name");
            updateDonorList("", "All");
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

            // Validate all fields
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
            
            // Validate phone number format
            if (!phone.matches("\\d{10}")) {
                txtPhone.setBorder(BorderFactory.createLineBorder(Color.RED));
                JOptionPane.showMessageDialog(null, "Please enter a valid 10-digit phone number!");
                return;
            }
            
            // Validate date format if provided
            if (!lastDonation.isEmpty() && !isValidDate(lastDonation)) {
                txtLastDonation.setBorder(BorderFactory.createLineBorder(Color.RED));
                JOptionPane.showMessageDialog(null, "Please enter date in DD/MM/YYYY format!");
                return;
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
        Color bgColor = isDarkTheme ? new Color(45, 45, 45) : UIManager.getColor("Panel.background");
        Color fgColor = isDarkTheme ? Color.WHITE : UIManager.getColor("Label.foreground");
        
        // Update theme toggle button
        themeToggle.setText(isDarkTheme ? "☀️ Light Mode" : "🌙 Dark Mode");
        themeToggle.setToolTipText(isDarkTheme ? "Switch to Light Theme" : "Switch to Dark Theme");
        
        // Update main panel and its components
        mainPanel.setBackground(bgColor);
        
        // Update scroll panes in the application
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JScrollPane scrollPane) {
                scrollPane.getViewport().setBackground(isDarkTheme ? bgColor : UIManager.getColor("List.background"));
                scrollPane.setBorder(BorderFactory.createLineBorder(isDarkTheme ? new Color(70, 70, 70) : Color.GRAY));
            }
            updateComponentColors(comp, bgColor, fgColor);
        }
        
        // Update list and table colors if they exist
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JScrollPane scrollPane) {
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JList list) {
                    list.setBackground(isDarkTheme ? bgColor : UIManager.getColor("List.background"));
                    list.setForeground(fgColor);
                }
            }
        }
        
        // Refresh the UI
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void updateComponentColors(Component comp, Color bg, Color fg) {
        if (comp == null || bg == null || fg == null) return;
        
        try {
            if (comp instanceof JPanel || comp instanceof JTextField || 
                comp instanceof JList || comp instanceof JComboBox) {
                comp.setBackground(isDarkTheme ? bg : UIManager.getColor("TextField.background"));
            } else {
                comp.setBackground(bg);
            }
            
            if (comp instanceof JLabel || comp instanceof JButton || 
                comp instanceof JToggleButton || comp instanceof JList || 
                comp instanceof JComboBox) {
                comp.setForeground(fg);
            }
            
            if (comp instanceof JTextField textField) {
                textField.setCaretColor(fg);
                textField.setForeground(fg);
            }
            
            if (comp instanceof Container container) {
                Component[] children = container.getComponents();
                if (children != null) {
                    for (Component child : children) {
                        updateComponentColors(child, bg, fg);
                    }
                }
            }
        } catch (IllegalArgumentException | SecurityException e) {
            System.err.println("Error updating component colors: " + e.getMessage());
        }
    }

    private void resetStats() {
        for (String bg : bloodGroupStats.keySet()) {
            bloodGroupStats.put(bg, 0);
        }
    }

    private boolean isValidDate(String date) {
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }
        try {
            String[] parts = date.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            
            // Check year
            if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) {
                return false;
            }
            
            // Check month
            if (month < 1 || month > 12) {
                return false;
            }
            
            // Check day based on month
            int maxDay = 31;
            if (month == 4 || month == 6 || month == 9 || month == 11) {
                maxDay = 30;
            } else if (month == 2) {
                // Check for leap year
                boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
                maxDay = isLeapYear ? 29 : 28;
            }
            
            if (day < 1 || day > maxDay) {
                return false;
            }
            
            // Check if date is not in future
            Calendar inputDate = Calendar.getInstance();
            inputDate.set(year, month - 1, day); // Month is 0-based in Calendar
            
            return !inputDate.after(Calendar.getInstance());
            
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            return false;
        }
    }

    private void updateStatsPanel(JPanel statsPanel) {
        statsPanel.removeAll();
        statsPanel.setLayout(new GridLayout(0, 2, 10, 5));
        
        // Calculate total donors for percentage
        int totalDonors = bloodGroupStats.values().stream().mapToInt(Integer::intValue).sum();
        
        // Add header
        JLabel headerLabel = new JLabel("Blood Group Statistics", SwingConstants.CENTER);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        statsPanel.add(headerLabel);
        JLabel totalLabel = new JLabel("Total: " + totalDonors, SwingConstants.CENTER);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        statsPanel.add(totalLabel);
        
        // Add separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        statsPanel.add(separator);
        statsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        
        // Sort blood groups by count (descending) and then by name
        bloodGroupStats.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                   .thenComparing(Map.Entry.comparingByKey()))
            .forEach(entry -> {
                String bloodGroup = entry.getKey();
                int count = entry.getValue();
                double percentage = totalDonors == 0 ? 0 : (count * 100.0 / totalDonors);
                
                JPanel groupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                JLabel groupLabel = new JLabel(bloodGroup);
                groupLabel.setFont(groupLabel.getFont().deriveFont(Font.BOLD));
                groupPanel.add(groupLabel);
                statsPanel.add(groupPanel);
                
                JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                JLabel countLabel = new JLabel(String.format("%d (%.1f%%)", count, percentage));
                countPanel.add(countLabel);
                statsPanel.add(countPanel);
                
                // Color coding for critical levels
                if (count == 0) {
                    groupLabel.setForeground(Color.RED);
                    countLabel.setForeground(Color.RED);
                } else if (count < 3) {
                    groupLabel.setForeground(new Color(255, 140, 0)); // Dark Orange
                    countLabel.setForeground(new Color(255, 140, 0));
                }
            });
            
        // Add legend
        statsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        statsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
        
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.add(new JLabel("🔴 Critical (0) "));
        legendPanel.add(new JLabel("🟠 Low (<3)"));
        statsPanel.add(legendPanel);
        statsPanel.add(new JPanel()); // Empty panel for grid alignment
        
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(""),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void updateDonorList(String searchTerm, String searchCategory) {
        if (searchTerm == null) searchTerm = "";
        if (searchCategory == null) searchCategory = "All";
        
        donorListModel.clear();
        for (Donor donor : donors) {
            boolean matches = switch (searchCategory) {
                case "Name" -> donor.name.toUpperCase().contains(searchTerm);
                case "Blood Group" -> donor.bloodGroup.toUpperCase().contains(searchTerm);
                case "Location" -> donor.location.toUpperCase().contains(searchTerm);
                case "Phone" -> donor.phone.contains(searchTerm);
                default -> // "All"
                    donor.name.toUpperCase().contains(searchTerm) ||
                    donor.bloodGroup.toUpperCase().contains(searchTerm) ||
                    donor.location.toUpperCase().contains(searchTerm) ||
                    donor.phone.contains(searchTerm);
            };
            
            if (searchTerm.isEmpty() || matches) {
                donorListModel.addElement(donor.toString());
            }
        }
        
        // Show search results count
        int results = donorListModel.getSize();
        if (!searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                String.format("Found %d donor%s matching your search.", 
                    results, results == 1 ? "" : "s"));
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
