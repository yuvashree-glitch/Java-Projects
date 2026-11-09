import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

public class MutualFundManager extends JFrame {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/miniproj";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "mahalakshmi";

    private final CardLayout card = new CardLayout();
    private final JPanel main = new JPanel(card);

    private final Color blue = new Color(20, 75, 130);
    private final Color light = new Color(238, 246, 255);
    private final Color buttonColor = new Color(215, 232, 250);

    private final JTextField loginEmail = new JTextField();
    private final JPasswordField loginPassword = new JPasswordField();
    private final JComboBox<String> loginRole =
            new JComboBox<>(new String[]{"USER", "ADMIN"});

    private String currentEmail = "";
    private String currentRole = "USER";

    public MutualFundManager() {
        setTitle("Mutual Funds Manager");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ensureExtraTables();

        main.add(loginPage(), "Login");
        main.add(menuPage("USER"), "UserMenu");
        main.add(menuPage("ADMIN"), "AdminMenu");

        add(main);
        setVisible(true);
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
                DB_URL, DB_USER, DB_PASSWORD);
    }

    private void ensureExtraTables() {

        String[] sql = {
            "CREATE TABLE IF NOT EXISTS fund_catalog (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "fund_name VARCHAR(150) NOT NULL, " +
            "amc_name VARCHAR(150) NOT NULL, " +
            "scheme VARCHAR(50) NOT NULL, " +
            "risk_level VARCHAR(50) NOT NULL, " +
            "nav DECIMAL(14,4) NOT NULL)",

            "CREATE TABLE IF NOT EXISTS sip_catalog (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "sip_name VARCHAR(150) NOT NULL, " +
            "fund_name VARCHAR(150) NOT NULL, " +
            "frequency VARCHAR(30) NOT NULL, " +
            "minimum_amount DECIMAL(14,2) NOT NULL)",

            "CREATE TABLE IF NOT EXISTS fund_transactions (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "user_email VARCHAR(190) NOT NULL, " +
            "fund_id INT NOT NULL, " +
            "action_type VARCHAR(10) NOT NULL, " +
            "units DECIMAL(14,4) NOT NULL, " +
            "nav DECIMAL(14,4) NOT NULL, " +
            "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Connection con = connect();
             Statement st = con.createStatement()) {

            for (String query : sql) {
                st.executeUpdate(query);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not prepare the new tables.\n"
                    + ex.getMessage()
                    + "\nCheck database name, password and connection.",
                    "Database Setup",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel heading(String text) {

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 27));
        label.setForeground(blue);

        return label;
    }

    private JPanel basePage(String title) {

        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBackground(light);
        p.setBorder(BorderFactory.createEmptyBorder(
                24, 35, 24, 35));

        p.add(heading(title), BorderLayout.NORTH);

        return p;
    }

    private JPanel loginPage() {

        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(light);
        p.setBorder(BorderFactory.createEmptyBorder(
                40, 130, 40, 130));

        JLabel title = heading("MUTUAL FUNDS MANAGER");
        title.setFont(new Font("Serif", Font.BOLD, 38));

        JLabel slogan = new JLabel(
                "Invest wisely today. Build a stronger tomorrow.",
                SwingConstants.CENTER);

        slogan.setFont(new Font("Arial", Font.ITALIC, 16));

        JPanel top = new JPanel(new GridLayout(2, 1, 5, 5));
        top.setBackground(light);
        top.add(title);
        top.add(slogan);

        p.add(top, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 1, 9, 9));
        form.setBackground(light);

        form.add(heading("WELCOME!"));
        form.add(new JLabel("Email"));

        loginEmail.setFont(new Font("Arial", Font.PLAIN, 17));
        form.add(loginEmail);

        form.add(new JLabel("Password"));

        loginPassword.setFont(new Font("Arial", Font.PLAIN, 17));
        form.add(loginPassword);

        JPanel roleRow = new JPanel(new GridLayout(1, 2, 10, 0));
        roleRow.setBackground(light);

        roleRow.add(new JLabel("Login as"));
        roleRow.add(loginRole);

        form.add(roleRow);

        p.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 12, 0));
        bottom.setBackground(light);

        JButton login = styledButton("LOGIN");
        JButton create = styledButton("Create User Account");

        bottom.add(login);
        bottom.add(create);

        p.add(bottom, BorderLayout.SOUTH);

        login.addActionListener(e -> login());
        create.addActionListener(e -> showCreateAccount());

        return p;
    }

    private JButton styledButton(String text) {

        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setForeground(blue);
        b.setBackground(buttonColor);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(300, 42));

        return b;
    }

    private void login() {

        String email = loginEmail.getText().trim();
        String pass = new String(loginPassword.getPassword());
        String role = loginRole.getSelectedItem().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            message("Enter your email and password.");
            return;
        }

        String sql = "SELECT name FROM users "
                + "WHERE email=? AND PASSWORD=? "
                + "AND UPPER(CATEGORY)=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, pass);
            ps.setString(3, role);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    currentEmail = email;
                    currentRole = role;

                    if (role.equals("ADMIN")) {
                        card.show(main, "AdminMenu");
                    } else {
                        card.show(main, "UserMenu");
                    }

                } else {
                    message("Invalid email, password, or account type.");
                }
            }

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void showCreateAccount() {

        JTextField name = new JTextField();
        JTextField gender = new JTextField();
        JTextField dob = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JTextField address = new JTextField();
        JPasswordField password = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));

        String[] labels = {
            "Full Name",
            "Gender",
            "Date of Birth (YYYY-MM-DD)",
            "Email",
            "Phone",
            "Address",
            "Password"
        };

        JComponent[] fields = {
            name, gender, dob, email, phone, address, password
        };

        for (int i = 0; i < labels.length; i++) {
            form.add(new JLabel(labels[i]));
            form.add(fields[i]);
        }

        int result = JOptionPane.showConfirmDialog(
                this, form, "Create User Account",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        if (name.getText().trim().isEmpty()
                || dob.getText().trim().isEmpty()
                || email.getText().trim().isEmpty()
                || password.getPassword().length == 0) {

            message("Name, date of birth, email and password are required.");
            return;
        }

        try {

            Date.valueOf(dob.getText().trim());

            String phoneText = phone.getText().trim();

            if (!phoneText.isEmpty()
                    && !phoneText.matches("\\d{1,15}")) {

                message("Phone must contain 1 to 15 digits.");
                return;
            }

            String sql = "INSERT INTO users "
                    + "(name, gender, dob, email, phone, address, "
                    + "CATEGORY, PASSWORD) "
                    + "VALUES (?, ?, ?, ?, ?, ?, 'USER', ?)";

            try (Connection con = connect();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name.getText().trim());
                ps.setString(2, gender.getText().trim());
                ps.setDate(3, Date.valueOf(dob.getText().trim()));
                ps.setString(4, email.getText().trim());

                if (phoneText.isEmpty()) {
                    ps.setNull(5, Types.VARCHAR);
                } else {
                    ps.setString(5, phoneText);
                }

                ps.setString(6, address.getText().trim());
                ps.setString(7,
                        new String(password.getPassword()));

                ps.executeUpdate();

                message("User account created. You can now log in.");
            }

        } catch (IllegalArgumentException ex) {

            message("Enter a valid date in YYYY-MM-DD format.");

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private JPanel menuPage(String role) {

        boolean admin = role.equals("ADMIN");

        JPanel p = basePage(
                admin ? "ADMIN DASHBOARD" : "USER DASHBOARD");

        JLabel welcome = new JLabel(
                admin
                ? "Manage funds, SIP options and registered users"
                : "Explore funds and manage your investments",
                SwingConstants.CENTER);

        welcome.setFont(new Font("Arial", Font.PLAIN, 16));
        p.add(welcome, BorderLayout.NORTH);

        String[] names;

        if (admin) {

            names = new String[]{
                "Add Mutual Fund",
                "Manage Mutual Funds",
                "Add SIP Name",
                "Manage SIP Names",
                "View / Remove Users",
                "View Reports",
                "My Profile",
                "LOG OUT"
            };

        } else {

            names = new String[]{
                "View Available Funds",
                "Buy Mutual Fund",
                "Sell Mutual Fund Units",
                "View SIP Names",
                "My Transactions",
                "My Profile",
                "LOG OUT"
            };
        }

        JPanel buttons = new JPanel(
                new GridLayout(names.length, 1, 8, 8));

        buttons.setBackground(light);
        buttons.setBorder(BorderFactory.createEmptyBorder(
                10, 170, 10, 170));

        for (String name : names) {

            JButton b = styledButton(name);
            buttons.add(b);

            b.addActionListener(e -> handleMenu(name, admin));
        }

        p.add(buttons, BorderLayout.CENTER);

        return p;
    }

    private void handleMenu(String name, boolean admin) {

        if (name.equals("LOG OUT")) {

            currentEmail = "";
            currentRole = "USER";
loginPassword.setText("");
            card.show(main, "Login");
            return;
        }

        if (name.equals("My Profile")) {
            showProfile();
            return;
        }

        if (admin) {

            switch (name) {

                case "Add Mutual Fund":
                    addFund();
                    break;

                case "Manage Mutual Funds":
                    manageCatalog("fund");
                    break;

                case "Add SIP Name":
                    addSipName();
                    break;

                case "Manage SIP Names":
                    manageCatalog("sip");
                    break;

                case "View / Remove Users":
                    manageUsers();
                    break;

                case "View Reports":
                    showReport();
                    break;
            }

        } else {

            switch (name) {

                case "View Available Funds":
                    showTable(
                            "Available Mutual Funds",
                            "SELECT id AS ID, fund_name AS Fund, "
                            + "amc_name AS AMC, scheme AS Scheme, "
                            + "risk_level AS Risk, nav AS NAV "
                            + "FROM fund_catalog");
                    break;

                case "Buy Mutual Fund":
                    buyFund();
                    break;

                case "Sell Mutual Fund Units":
                    sellFund();
                    break;

                case "View SIP Names":
                    showTable(
                            "Available SIP Names",
                            "SELECT id AS ID, sip_name AS `SIP Name`, "
                            + "fund_name AS Fund, "
                            + "frequency AS Frequency, "
                            + "minimum_amount AS `Minimum Amount` "
                            + "FROM sip_catalog");
                    break;

                case "My Transactions":
                    showMyTransactions();
                    break;
            }
        }
    }

    private void addFund() {

        JTextField fund = new JTextField();
        JTextField amc = new JTextField();

        JComboBox<String> scheme = new JComboBox<>(
                new String[]{"Equity", "Debt", "Hybrid", "Index", "Other"});

        JComboBox<String> risk = new JComboBox<>(
                new String[]{"Low", "Moderate", "High", "Very High"});

        JTextField nav = new JTextField();

        JPanel p = new JPanel(new GridLayout(5, 2, 8, 8));

        p.add(new JLabel("Fund Name"));
        p.add(fund);

        p.add(new JLabel("AMC Name"));
        p.add(amc);

        p.add(new JLabel("Scheme"));
        p.add(scheme);

        p.add(new JLabel("Risk Level"));
        p.add(risk);

        p.add(new JLabel("NAV (₹ per unit)"));
        p.add(nav);

        if (JOptionPane.showConfirmDialog(
                this, p, "Add Mutual Fund",
                JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            BigDecimal navValue =
                    new BigDecimal(nav.getText().trim());

            if (fund.getText().trim().isEmpty()
                    || amc.getText().trim().isEmpty()
                    || navValue.signum() <= 0) {

                message("Enter fund name, AMC name and a positive NAV.");
                return;
            }

            String sql = "INSERT INTO fund_catalog "
                    + "(fund_name, amc_name, scheme, risk_level, nav) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (Connection con = connect();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, fund.getText().trim());
                ps.setString(2, amc.getText().trim());
                ps.setString(3, scheme.getSelectedItem().toString());
                ps.setString(4, risk.getSelectedItem().toString());
                ps.setBigDecimal(5, navValue);

                ps.executeUpdate();

                message("Mutual fund added to the available funds list.");
            }

        } catch (NumberFormatException ex) {

            message("Enter a valid number for NAV.");

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void addSipName() {

        JTextField sipName = new JTextField();
        JTextField fundName = new JTextField();

        JComboBox<String> frequency = new JComboBox<>(
                new String[]{"Monthly", "Quarterly"});

        JTextField minimum = new JTextField();

        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));

        p.add(new JLabel("SIP Name"));
        p.add(sipName);

        p.add(new JLabel("Fund Name"));
        p.add(fundName);

        p.add(new JLabel("Frequency"));
        p.add(frequency);

        p.add(new JLabel("Minimum Amount (₹)"));
        p.add(minimum);

        if (JOptionPane.showConfirmDialog(
                this, p, "Add SIP Name",
                JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            BigDecimal amount =
                    new BigDecimal(minimum.getText().trim());

            if (sipName.getText().trim().isEmpty()
                    || fundName.getText().trim().isEmpty()
                    || amount.signum() <= 0) {

                message("Enter SIP name, fund name and a positive minimum amount.");
                return;
            }

            String sql = "INSERT INTO sip_catalog "
                    + "(sip_name, fund_name, frequency, minimum_amount) "
                    + "VALUES (?, ?, ?, ?)";

            try (Connection con = connect();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, sipName.getText().trim());
                ps.setString(2, fundName.getText().trim());
                ps.setString(3, frequency.getSelectedItem().toString());
                ps.setBigDecimal(4, amount);

                ps.executeUpdate();

                message("SIP name added successfully.");
            }

        } catch (NumberFormatException ex) {

            message("Enter a valid minimum amount.");

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void manageCatalog(String type) {

        boolean funds = type.equals("fund");

        String query;

        if (funds) {

            query = "SELECT id, fund_name, amc_name, scheme, "
                    + "risk_level, nav FROM fund_catalog";

        } else {

            query = "SELECT id, sip_name, fund_name, "
                    + "frequency, minimum_amount FROM sip_catalog";
        }

        String title = funds
                ? "Manage Mutual Funds"
                : "Manage SIP Names";

        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            DefaultTableModel model = modelFrom(rs);

            JTable table = new JTable(model);
            table.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(720, 300));

            JButton delete = styledButton("Delete Selected");
            JButton close = styledButton("Close");

            JPanel bottom = new JPanel();
            bottom.add(delete);
            bottom.add(close);

            JDialog dialog = new JDialog(this, title, true);
            dialog.setLayout(new BorderLayout(8, 8));
            dialog.add(scroll, BorderLayout.CENTER);
            dialog.add(bottom, BorderLayout.SOUTH);

            delete.addActionListener(e -> {

                int row = table.getSelectedRow();

                if (row < 0) {
                    message("Select a row first.");
                    return;
                }

                int modelRow = table.convertRowIndexToModel(row);

                int id = Integer.parseInt(
                        model.getValueAt(modelRow, 0).toString());

                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Delete this entry? Existing transaction history will remain.",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                String sql = funds
                        ? "DELETE FROM fund_catalog WHERE id=?"
                        : "DELETE FROM sip_catalog WHERE id=?";

                try (Connection c = connect();
                     PreparedStatement ps = c.prepareStatement(sql)) {

                    ps.setInt(1, id);
                    ps.executeUpdate();

                    model.removeRow(modelRow);

                } catch (SQLException ex) {
                    dbError(ex);
                }
            });

            close.addActionListener(e -> dialog.dispose());

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void manageUsers() {

        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT name, email, phone, CATEGORY "
                     + "FROM users "
                     + "WHERE UPPER(CATEGORY)='USER'")) {

            DefaultTableModel model = modelFrom(rs);

            JTable table = new JTable(model);
            table.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(650, 300));

            JButton remove = styledButton("Remove Selected User");
            JButton close = styledButton("Close");

            JPanel bottom = new JPanel();
            bottom.add(remove);
            bottom.add(close);

            JDialog dialog = new JDialog(
                    this, "Registered Users", true);

            dialog.setLayout(new BorderLayout(8, 8));
            dialog.add(scroll, BorderLayout.CENTER);
            dialog.add(bottom, BorderLayout.SOUTH);

            remove.addActionListener(e -> {

                int row = table.getSelectedRow();

                if (row < 0) {
                    message("Select a user first.");
                    return;
                }

                int modelRow = table.convertRowIndexToModel(row);

                String email = model.getValueAt(
                        modelRow, 1).toString();

                int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Remove user account " + email + "?",
                        "Confirm Removal",
                        JOptionPane.YES_NO_OPTION);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                try (Connection c = connect();
                     PreparedStatement ps = c.prepareStatement(
                             "DELETE FROM users "
                             + "WHERE email=? "
                             + "AND UPPER(CATEGORY)='USER'")) {

                    ps.setString(1, email);
                    ps.executeUpdate();

                    model.removeRow(modelRow);

                    message("User account removed. Transaction history is retained.");

                } catch (SQLException ex) {
                    dbError(ex);
                }
            });

            close.addActionListener(e -> dialog.dispose());

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void buyFund() {

        Integer fundId = chooseFund();

        if (fundId == null) {
            return;
        }

        BigDecimal nav;

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT nav FROM fund_catalog WHERE id=?")) {

            ps.setInt(1, fundId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    message("Fund not found.");
                    return;
                }

                nav = rs.getBigDecimal("nav");
            }

        } catch (SQLException ex) {
            dbError(ex);
            return;
        }

        String input = JOptionPane.showInputDialog(
                this,
                "Enter units to buy:",
                "Buy Mutual Fund",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            return;
        }

        try {

            BigDecimal units = new BigDecimal(input.trim());

            if (units.signum() <= 0) {
                message("Units must be greater than zero.");
                return;
            }

            recordTransaction(fundId, "BUY", units, nav);

        } catch (NumberFormatException ex) {
            message("Enter a valid number of units.");
        }
    }

    private void sellFund() {

        Integer fundId = chooseOwnedFund();

        if (fundId == null) {
            return;
        }

        BigDecimal nav;

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT nav FROM fund_catalog WHERE id=?")) {

            ps.setInt(1, fundId);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    message("Fund not found or no longer available.");
                    return;
                }

                nav = rs.getBigDecimal("nav");
            }

        } catch (SQLException ex) {
            dbError(ex);
            return;
        }

        BigDecimal owned = getOwnedUnits(fundId);

        if (owned.signum() <= 0) {
            message("You do not own units in this fund.");
            return;
        }

        String input = JOptionPane.showInputDialog(
                this,
                "Units you own: " + owned
                + "\nEnter units to sell:",
                "Sell Mutual Fund Units",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            return;
        }

        try {

            BigDecimal units = new BigDecimal(input.trim());

            if (units.signum() <= 0) {
                message("Units must be greater than zero.");
                return;
            }

            if (units.compareTo(owned) > 0) {
                message("You cannot sell more units than you own.");
                return;
            }

            recordTransaction(fundId, "SELL", units, nav);

        } catch (NumberFormatException ex) {
            message("Enter a valid number of units.");
        }
    }

    private Integer chooseFund() {

        return chooseFundFromQuery(
                "SELECT id, fund_name, nav "
                + "FROM fund_catalog ORDER BY fund_name",
                "Choose a mutual fund");
    }

    private Integer chooseOwnedFund() {

        String sql = "SELECT f.id, f.fund_name, f.nav "
                + "FROM fund_catalog f "
                + "JOIN fund_transactions t ON t.fund_id=f.id "
                + "WHERE t.user_email=? "
                + "GROUP BY f.id, f.fund_name, f.nav "
                + "HAVING SUM(CASE WHEN t.action_type='BUY' "
                + "THEN t.units ELSE -t.units END)>0 "
                + "ORDER BY f.fund_name";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, currentEmail);

            try (ResultSet rs = ps.executeQuery()) {

                java.util.List<String> labels =
                        new java.util.ArrayList<>();

                java.util.List<Integer> ids =
                        new java.util.ArrayList<>();

                while (rs.next()) {

                    ids.add(rs.getInt("id"));

                    labels.add(
                            rs.getString("fund_name")
                            + " | NAV ₹"
                            + rs.getBigDecimal("nav"));
                }

                if (ids.isEmpty()) {
                    message("You do not currently own units in any listed fund.");
                    return null;
                }

                Object selected = JOptionPane.showInputDialog(
                        this,
                        "Select a fund:",
                        "Sell Units",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        labels.toArray(),
                        labels.get(0));

                if (selected == null) {
                    return null;
                }

                return ids.get(labels.indexOf(selected.toString()));
            }

        } catch (SQLException ex) {
            dbError(ex);
            return null;
        }
    }

    private Integer chooseFundFromQuery(
            String sql, String title) {

        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            java.util.List<String> labels =
                    new java.util.ArrayList<>();

            java.util.List<Integer> ids =
                    new java.util.ArrayList<>();

            while (rs.next()) {

                ids.add(rs.getInt("id"));

                labels.add(
                        rs.getString("fund_name")
                        + " | NAV ₹"
                        + rs.getBigDecimal("nav"));
            }

            if (ids.isEmpty()) {
                message("No mutual funds are available yet.");
                return null;
            }

            Object selected = JOptionPane.showInputDialog(
                    this,
                    title,
                    title,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    labels.toArray(),
                    labels.get(0));

            if (selected == null) {
                return null;
            }

            return ids.get(labels.indexOf(selected.toString()));

        } catch (SQLException ex) {
            dbError(ex);
            return null;
        }
    }

    private BigDecimal getOwnedUnits(int fundId) {

        String sql = "SELECT COALESCE("
                + "SUM(CASE WHEN action_type='BUY' "
                + "THEN units ELSE -units END), 0) "
                + "FROM fund_transactions "
                + "WHERE user_email=? AND fund_id=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, currentEmail);
            ps.setInt(2, fundId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }

        } catch (SQLException ex) {
            dbError(ex);
        }

        return BigDecimal.ZERO;
    }

    private void recordTransaction(
            int fundId,
            String action,
            BigDecimal units,
            BigDecimal nav) {

        String sql = "INSERT INTO fund_transactions "
                + "(user_email, fund_id, action_type, units, nav) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, currentEmail);
            ps.setInt(2, fundId);
            ps.setString(3, action);
            ps.setBigDecimal(4, units);
            ps.setBigDecimal(5, nav);

            ps.executeUpdate();

            BigDecimal total = units.multiply(nav);

            message(action + " recorded successfully.\n"
                    + "Units: " + units
                    + "\nNAV: ₹" + nav
                    + "\nApproximate value: ₹" + total);

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void showMyTransactions() {

        String sql = "SELECT t.id AS `Transaction ID`, "
                + "f.fund_name AS Fund, "
                + "t.action_type AS Action, "
                + "t.units AS Units, "
                + "t.nav AS NAV, "
                + "(t.units*t.nav) AS `Approximate Value`, "
                + "t.transaction_date AS Date "
                + "FROM fund_transactions t "
                + "LEFT JOIN fund_catalog f ON t.fund_id=f.id "
                + "WHERE t.user_email=? "
                + "ORDER BY t.transaction_date DESC";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, currentEmail);

            try (ResultSet rs = ps.executeQuery()) {

                JTable table = new JTable(modelFrom(rs));

                JScrollPane scroll = new JScrollPane(table);
                scroll.setPreferredSize(
                        new Dimension(800, 350));

                JOptionPane.showMessageDialog(
                        this,
                        scroll,
                        "My Transactions",
                        JOptionPane.PLAIN_MESSAGE);
            }

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private DefaultTableModel modelFrom(ResultSet rs)
            throws SQLException {

        ResultSetMetaData md = rs.getMetaData();

        DefaultTableModel model = new DefaultTableModel();

        for (int i = 1; i <= md.getColumnCount(); i++) {
            model.addColumn(md.getColumnLabel(i));
        }

        while (rs.next()) {

            Object[] row = new Object[md.getColumnCount()];

            for (int i = 1; i <= row.length; i++) {
                row[i - 1] = rs.getObject(i);
            }

            model.addRow(row);
        }

        return model;
    }

    private void showTable(String title, String sql) {

        try (Connection con = connect();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            JTable table = new JTable(modelFrom(rs));

            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(
                    new Dimension(850, 400));

            JOptionPane.showMessageDialog(
                    this,
                    scroll,
                    title,
                    JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void showReport() {

        StringBuilder report =
                new StringBuilder("ADMIN REPORT\n\n");

        String[] queries = {
            "SELECT COUNT(*) FROM fund_catalog",
            "SELECT COUNT(*) FROM sip_catalog",
            "SELECT COUNT(*) FROM users "
                + "WHERE UPPER(CATEGORY)='USER'",
            "SELECT COUNT(*) FROM fund_transactions"
        };

        String[] labels = {
            "Available mutual funds: ",
            "Available SIP names: ",
            "Registered users: ",
            "Buy/sell transactions: "
        };

        try (Connection con = connect();
             Statement st = con.createStatement()) {

            for (int i = 0; i < queries.length; i++) {

                try (ResultSet rs =
                        st.executeQuery(queries[i])) {

                    if (rs.next()) {
                        report.append(labels[i])
                              .append(rs.getInt(1))
                              .append('\n');
                    }
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    report.toString(),
                    "Reports",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void showProfile() {

        String sql = "SELECT name, gender, dob, email, "
                + "phone, address, CATEGORY "
                + "FROM users WHERE email=?";

        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, currentEmail);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String info =
                            "Name: " + rs.getString("name")
                            + "\nGender: " + rs.getString("gender")
                            + "\nDate of birth: " + rs.getDate("dob")
                            + "\nEmail: " + rs.getString("email")
                            + "\nPhone: " + rs.getString("phone")
                            + "\nAddress: " + rs.getString("address")
                            + "\nAccount type: "
                            + rs.getString("CATEGORY");

                    JOptionPane.showMessageDialog(
                            this,
                            info,
                            "My Profile",
                            JOptionPane.INFORMATION_MESSAGE);

                } else {
                    message("Profile not found.");
                }
            }

        } catch (SQLException ex) {
            dbError(ex);
        }
    }

    private void message(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    private void dbError(SQLException ex) {

        message("Database error:\n"
                + ex.getMessage()
                + "\n\nCheck MySQL is running, "
                + "database/table names, column names, "
                + "and Connector/J.");
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                MutualFundManager::new);
    }
}



