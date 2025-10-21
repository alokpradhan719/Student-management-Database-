import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentRegistrationForm extends JFrame {

    private JTextField nameField, emailField, phoneField, dobField;
    private JComboBox<String> courseCombo;
    private JTextArea addressArea;
    private JRadioButton maleRadio, femaleRadio, otherRadio;
    private ButtonGroup genderGroup;
    private JButton backButton, saveButton, clearButton, viewButton, exitButton;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static class DBConfig {
        static final String HOST = "localhost";
        static final String PORT = "3306";
        static final String DB_NAME = "studentdb";
        static final String USER = "root";
        static final String PASSWORD = ""; // Set your MySQL root password if any
        static String baseUrl() {
            return "jdbc:mysql://" + HOST + ":" + PORT + "/";
        }
        static String url() {
            return baseUrl() + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }
    }

    private static void ensureDriverLoaded() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "MySQL JDBC driver not found.\nAdd mysql-connector-j JAR to the classpath.",
                    "Driver Missing", JOptionPane.ERROR_MESSAGE);
        }
    }

    public StudentRegistrationForm() {
        super("Student Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
        initListeners();

        ensureDriverLoaded();
        initDatabase();
    }

    private void initComponents() {
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(15);
        dobField = new JTextField(10);
        dobField.setToolTipText("Enter date of birth as yyyy-MM-dd");
        // modern field styling
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 227, 231), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        nameField.setBackground(Color.WHITE);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 227, 231), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        emailField.setBackground(Color.WHITE);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 227, 231), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        phoneField.setBackground(Color.WHITE);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dobField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 227, 231), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        dobField.setBackground(Color.WHITE);
        dobField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        String[] courses = {"Select course", "BSc", "BCom", "BA", "BTech", "MSc", "MBA"};
        courseCombo = new JComboBox<>(courses);
        courseCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 227, 231), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        courseCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 227, 231), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        otherRadio = new JRadioButton("Other");
        genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderGroup.add(otherRadio);
        
        backButton = new JButton("Back");
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear");
        viewButton = new JButton("View All");
        exitButton = new JButton("Exit");
    
        // modern pill style buttons
        Color primary = new Color(33, 150, 243);
        Color success = new Color(46, 204, 113);
        Color warn = new Color(255, 193, 7);
        Color danger = new Color(244, 67, 54);
        Color neutral = new Color(158, 158, 158);
        Font btnFont = new Font("Segoe UI", Font.BOLD, 13);
        backButton.setBackground(neutral); backButton.setForeground(Color.WHITE); backButton.setFocusPainted(false); backButton.setFont(btnFont); backButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(neutral.darker(), 1, true), BorderFactory.createEmptyBorder(8, 16, 8, 16))); backButton.setOpaque(true);
        saveButton.setBackground(success); saveButton.setForeground(Color.WHITE); saveButton.setFocusPainted(false); saveButton.setFont(btnFont); saveButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(success.darker(), 1, true), BorderFactory.createEmptyBorder(8, 16, 8, 16))); saveButton.setOpaque(true);
        clearButton.setBackground(warn); clearButton.setForeground(Color.WHITE); clearButton.setFocusPainted(false); clearButton.setFont(btnFont); clearButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(warn.darker(), 1, true), BorderFactory.createEmptyBorder(8, 16, 8, 16))); clearButton.setOpaque(true);
        viewButton.setBackground(primary); viewButton.setForeground(Color.WHITE); viewButton.setFocusPainted(false); viewButton.setFont(btnFont); viewButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(primary.darker(), 1, true), BorderFactory.createEmptyBorder(8, 16, 8, 16))); viewButton.setOpaque(true);
        exitButton.setBackground(danger); exitButton.setForeground(Color.WHITE); exitButton.setFocusPainted(false); exitButton.setFont(btnFont); exitButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(danger.darker(), 1, true), BorderFactory.createEmptyBorder(8, 16, 8, 16))); exitButton.setOpaque(true);
    }

    private void layoutComponents() {
        JPanel form = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Color top = new Color(249, 250, 251);
                Color bottom = new Color(232, 234, 237);
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        form.setOpaque(true);
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        int row = 0;
    
        JLabel header = new JLabel("Student Registration", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(header, gbc);
        gbc.gridwidth = 1;
        row++;
    
    
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        form.add(nameField, gbc);
        row++;
    
    
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        form.add(emailField, gbc);
        row++;
    
    
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Phone"), gbc);
        gbc.gridx = 1;
        form.add(phoneField, gbc);
        row++;
    
    
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Gender"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        genderPanel.setOpaque(false);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        genderPanel.add(otherRadio);
        form.add(genderPanel, gbc);
        row++;
    
    
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("DOB (yyyy-MM-dd)"), gbc);
        gbc.gridx = 1;
        form.add(dobField, gbc);
        row++;
    
    
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Course"), gbc);
        gbc.gridx = 1;
        form.add(courseCombo, gbc);
        row++;
    
    
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Address"), gbc);
        gbc.gridx = 1;
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setOpaque(false);
        addressScroll.getViewport().setOpaque(false);
        form.add(addressScroll, gbc);
        row++;
    
    
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        buttonsPanel.add(backButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(exitButton);
        form.add(buttonsPanel, gbc);

        setContentPane(form);
    }

    private void initListeners() {
        saveButton.addActionListener(e -> {
            if (validateInputs()) {
                saveStudent();
            }
        });

        clearButton.addActionListener(e -> clearForm());

        viewButton.addActionListener(e -> showAllStudents());

        backButton.addActionListener(e -> { new Launcher().setVisible(true); dispose(); });
        exitButton.addActionListener(e -> System.exit(0));


        Action saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButton.doClick();
            }
        };
        nameField.addActionListener(saveAction);
        emailField.addActionListener(saveAction);
        phoneField.addActionListener(saveAction);
        dobField.addActionListener(saveAction);
        addressArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "save");
        addressArea.getActionMap().put("save", saveAction);
    }

    private boolean validateInputs() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String gender = getSelectedGender();
        String dobText = dobField.getText().trim();
        String course = (String) courseCombo.getSelectedItem();

        if (name.isEmpty()) {
            showError("Please enter the student's name.");
            nameField.requestFocus();
            return false;
        }

        if (email.isEmpty() || !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            showError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }

        if (!phone.matches("^\\d{10}$")) {
            showError("Please enter a 10-digit phone number (digits only).");
            phoneField.requestFocus();
            return false;
        }

        if (gender == null) {
            showError("Please select a gender.");
            return false;
        }

        LocalDate dob;
        try {
            dob = LocalDate.parse(dobText, DATE_FMT);
            if (dob.isAfter(LocalDate.now())) {
                showError("DOB cannot be in the future.");
                dobField.requestFocus();
                return false;
            }
        } catch (DateTimeParseException ex) {
            showError("Please enter DOB in yyyy-MM-dd format.");
            dobField.requestFocus();
            return false;
        }

        if (course == null || course.equals("Select course")) {
            showError("Please select a course.");
            courseCombo.requestFocus();
            return false;
        }

        return true;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private String getSelectedGender() {
        if (maleRadio.isSelected()) return "Male";
        if (femaleRadio.isSelected()) return "Female";
        if (otherRadio.isSelected()) return "Other";
        return null;
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        dobField.setText("");
        genderGroup.clearSelection();
        courseCombo.setSelectedIndex(0);
        addressArea.setText("");
        nameField.requestFocus();
    }

    private void initDatabase() {

        try (Connection conn = DriverManager.getConnection(
                DBConfig.baseUrl() + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                DBConfig.USER, DBConfig.PASSWORD);
             Statement st = conn.createStatement()) {

            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DBConfig.DB_NAME);
        } catch (SQLException ex) {
            showError("Failed to connect to MySQL.\n" + ex.getMessage());
            return;
        }


        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {

            String createTable = "CREATE TABLE IF NOT EXISTS students ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "email VARCHAR(100) NOT NULL,"
                    + "phone VARCHAR(20) NOT NULL,"
                    + "gender ENUM('Male','Female','Other') NOT NULL,"
                    + "dob DATE NOT NULL,"
                    + "course VARCHAR(50) NOT NULL,"
                    + "address TEXT"
                    + ")";
            st.executeUpdate(createTable);
        } catch (SQLException ex) {
            showError("Failed to initialize table.\n" + ex.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBConfig.url(), DBConfig.USER, DBConfig.PASSWORD);
    }

    private void saveStudent() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String gender = getSelectedGender();
        LocalDate dob = LocalDate.parse(dobField.getText().trim(), DATE_FMT);
        String course = (String) courseCombo.getSelectedItem();
        String address = addressArea.getText().trim();

        String sql = "INSERT INTO students (name, email, phone, gender, dob, course, address)"
                + " VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, gender);
            ps.setDate(5, Date.valueOf(dob));
            ps.setString(6, course);
            ps.setString(7, address.isEmpty() ? null : address);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                int choice = JOptionPane.showOptionDialog(this, "Registered!", "Success",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        null, new Object[]{"Clear Form", "Exit App"}, "Clear Form");
                if (choice == 1) {
                    System.exit(0);
                } else {
                    clearForm();
                }
            } else {
                showError("No rows inserted. Please try again.");
            }
        } catch (SQLException ex) {
            showError("Failed to save student.\n" + ex.getMessage());
        }
    }

    public void showAllStudents() {
        String[] cols = {"ID", "Name", "Email", "Phone", "Gender", "DOB", "Course", "Address"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        String query = "SELECT id, name, email, phone, gender, dob, course, address FROM students ORDER BY id DESC";

        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("gender"),
                        rs.getDate("dob"),
                        rs.getString("course"),
                        rs.getString("address")
                };
                model.addRow(row);
            }
        } catch (SQLException ex) {
            showError("Failed to fetch records.\n" + ex.getMessage());
            return;
        }

        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);

        JDialog dialog = new JDialog(this, "All Students", true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(close);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(i.getName())) { UIManager.setLookAndFeel(i.getClassName()); break; }
            }
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            StudentRegistrationForm app = new StudentRegistrationForm();
            app.setVisible(true);
        });
    }
}
