package ui;

import dao.EmployeeDAO;
import java.awt.*;
import javax.swing.*;
import model.Address;
import model.Employee;

public class AddEmployeePanel extends JPanel {

    private final EmployeeDAO dao = new EmployeeDAO();
    private JTextField fnameField, lnameField, emailField, hireDateField, salaryField, ssnField;
    private JTextField streetField, cityIDField, stateIDField, zipField, dobField, phoneField, emergencyField, emergencyPhoneField;

    public AddEmployeePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JLabel pageTitle = new JLabel("Add New Employee");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(new Color(25, 55, 109));
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        // Two-column form layout
        JPanel formWrapper = new JPanel(new GridLayout(1, 2, 16, 0));
        formWrapper.setBackground(new Color(245, 247, 252));

        // Left: Employee Info
        JPanel empCard = formCard("Employee Information");
        fnameField    = addField(empCard, "First Name *");
        lnameField    = addField(empCard, "Last Name *");
        emailField    = addField(empCard, "Email *");
        hireDateField = addField(empCard, "Hire Date (MM/DD/YYYY) *");
        salaryField   = addField(empCard, "Salary *");
        ssnField      = addField(empCard, "SSN");
        empCard.add(Box.createVerticalGlue());

        // Right: Address Info
        JPanel addrCard = formCard("Address Information");
        streetField        = addField(addrCard, "Street *");
        cityIDField        = addField(addrCard, "City *");
        stateIDField       = addField(addrCard, "State *");
        zipField           = addField(addrCard, "ZIP Code *");
        dobField           = addField(addrCard, "Date of Birth (MM/DD/YYYY) *");
        phoneField         = addField(addrCard, "Phone *");
        emergencyField     = addField(addrCard, "Emergency Contact name *");
        emergencyPhoneField = addField(addrCard, "Emergency Contact Phone *");
        addrCard.add(Box.createVerticalGlue());

        formWrapper.add(empCard);
        formWrapper.add(addrCard);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setBackground(new Color(245, 247, 252));
        JButton clearBtn = btn("Clear Form",    new Color(120, 120, 120));
        JButton saveBtn  = btn("Save Employee", new Color(46, 125, 50));
        clearBtn.addActionListener(e -> clearAll());
        saveBtn.addActionListener(e  -> saveEmployee());
        btnRow.add(clearBtn);
        btnRow.add(saveBtn);

        add(pageTitle,    BorderLayout.NORTH);
        add(formWrapper,  BorderLayout.CENTER);
        add(btnRow,       BorderLayout.SOUTH);
    }

    private void saveEmployee() {
        try {
            String fname = fnameField.getText().trim();
            String lname = lnameField.getText().trim();
            String email = emailField.getText().trim();
            String hire  = hireDateField.getText().trim();
            String salStr = salaryField.getText().trim();

            if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || hire.isEmpty() || salStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields (*).", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double salary = Double.parseDouble(salStr);

            Employee emp = new Employee(0, fname, lname, email, hire, salary, ssnField.getText().trim(), 0);
            Address addr = new Address(0,
                streetField.getText().trim(),
                parseIntField(cityIDField),
                parseIntField(stateIDField),
                zipField.getText().trim(),
                dobField.getText().trim(),
                phoneField.getText().trim(),
                emergencyField.getText().trim(),
                emergencyPhoneField.getText().trim()
            );

            if (dao.insertEmployee(emp, addr)) {
                JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add employee. Check DB connection.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAll() {
        for (JTextField f : new JTextField[]{fnameField, lnameField, emailField, hireDateField,
                salaryField, ssnField, streetField, cityIDField, stateIDField,
                zipField, dobField, phoneField, emergencyField, emergencyPhoneField})
            f.setText("");
    }

    private int parseIntField(JTextField f) {
        try { return Integer.parseInt(f.getText().trim()); } catch (Exception e) { return 1; }
    }

    // ── Helpers ──
    private JPanel formCard(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 240)),
            BorderFactory.createEmptyBorder(15, 18, 15, 18)
        ));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(25, 55, 109));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        p.add(lbl);
        return p;
    }

    private JTextField addField(JPanel card, String label) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(new Color(80, 80, 80));
        l.setAlignmentX(LEFT_ALIGNMENT);
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        f.setAlignmentX(LEFT_ALIGNMENT);
        card.add(l);
        card.add(f);
        card.add(Box.createVerticalStrut(5));
        return f;
    }

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(150, 34));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
