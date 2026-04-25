package ui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchEditPanel extends JPanel {

    private final EmployeeDAO dao = new EmployeeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField firstNameField, lastNameField, emailField, hireDateField, salaryField, ssnField;
    private JLabel empIdLabel;
    private int selectedEmpID = -1;

    public SearchEditPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        // ── Page title ──
        JLabel pageTitle = new JLabel("Search & Edit Employee");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(new Color(25, 55, 109));
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // ── Search bar ──
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.setBackground(new Color(245, 247, 252));

        JTextField fnSearch = new JTextField(12);
        JTextField lnSearch = new JTextField(12);
        fnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton searchBtn  = actionBtn("Search",   new Color(25, 118, 210));
        JButton showAllBtn = actionBtn("Show All",  new Color(76, 125, 50));

        searchBar.add(new JLabel("First Name:"));
        searchBar.add(fnSearch);
        searchBar.add(new JLabel("Last Name:"));
        searchBar.add(lnSearch);
        searchBar.add(searchBtn);
        searchBar.add(showAllBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 252));
        topPanel.add(pageTitle,  BorderLayout.NORTH);
        topPanel.add(searchBar,  BorderLayout.CENTER);

        // ── Table ──
        String[] cols = {"ID", "First Name", "Last Name", "Email", "Hire Date", "Salary"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedIntoForm();
        });

        // ── Edit Form ──
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 240)),
            BorderFactory.createEmptyBorder(15, 18, 15, 18)
        ));
        formCard.setPreferredSize(new Dimension(340, 0));

        JLabel formTitle = new JLabel("Edit Employee");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        formTitle.setForeground(new Color(25, 55, 109));
        formTitle.setAlignmentX(LEFT_ALIGNMENT);

        empIdLabel   = new JLabel("ID: —");
        empIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        empIdLabel.setForeground(Color.GRAY);
        empIdLabel.setAlignmentX(LEFT_ALIGNMENT);

        firstNameField = formField();
        lastNameField  = formField();
        emailField     = formField();
        hireDateField  = formField();
        salaryField    = formField();
        ssnField       = formField();

        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(2));
        formCard.add(empIdLabel);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(labeledField("First Name *", firstNameField));
        formCard.add(labeledField("Last Name *",  lastNameField));
        formCard.add(labeledField("Email *",       emailField));
        formCard.add(labeledField("Hire Date (YYYY-MM-DD)", hireDateField));
        formCard.add(labeledField("Salary *",      salaryField));
        formCard.add(labeledField("SSN",           ssnField));
        formCard.add(Box.createVerticalStrut(12));

        JButton updateBtn = actionBtn("Update Employee", new Color(46, 125, 50));
        JButton clearBtn  = actionBtn("Clear",           new Color(120, 120, 120));
        updateBtn.setAlignmentX(LEFT_ALIGNMENT);
        clearBtn.setAlignmentX(LEFT_ALIGNMENT);
        updateBtn.addActionListener(e -> updateEmployee());
        clearBtn.addActionListener(e  -> clearForm());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnRow.add(updateBtn);
        btnRow.add(clearBtn);
        formCard.add(btnRow);

        // ── Center: table + form side by side ──
        JPanel center = new JPanel(new BorderLayout(10, 0));
        center.setBackground(new Color(245, 247, 252));
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(formCard,               BorderLayout.EAST);

        // Wire search
        searchBtn.addActionListener(e -> {
            String fn = fnSearch.getText().trim();
            String ln = lnSearch.getText().trim();
            if (fn.isEmpty() && ln.isEmpty()) { loadAll(); return; }
            loadByName(fn, ln);
        });
        showAllBtn.addActionListener(e -> loadAll());

        add(topPanel, BorderLayout.NORTH);
        add(center,   BorderLayout.CENTER);

        loadAll();
    }

    private void loadAll() {
        tableModel.setRowCount(0);
        for (Employee e : dao.getAllEmployees()) addRow(e);
    }

    private void loadByName(String fn, String ln) {
        tableModel.setRowCount(0);
        List<Employee> results = dao.searchByName(fn, ln);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No employees found.", "Search", JOptionPane.INFORMATION_MESSAGE);
        }
        for (Employee e : results) addRow(e);
    }

    private void addRow(Employee e) {
        tableModel.addRow(new Object[]{
            e.getEmpid(), e.getFname(), e.getLname(),
            e.getEmail(), e.getHireDate(),
            String.format("$%,.2f", e.getSalary())
        });
    }

    private void loadSelectedIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedEmpID = (int) tableModel.getValueAt(row, 0);
        Employee emp = dao.getEmployeeById(selectedEmpID);
        if (emp == null) return;
        empIdLabel.setText("ID: " + selectedEmpID);
        firstNameField.setText(emp.getFname());
        lastNameField.setText(emp.getLname());
        emailField.setText(emp.getEmail());
        hireDateField.setText(emp.getHireDate());
        salaryField.setText(String.valueOf(emp.getSalary()));
        ssnField.setText(emp.getSsn() != null ? emp.getSsn() : "");
    }

    private void updateEmployee() {
        if (selectedEmpID == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String fname = firstNameField.getText().trim();
            String lname = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String hire  = hireDateField.getText().trim();
            double salary = Double.parseDouble(salaryField.getText().trim());
            String ssn   = ssnField.getText().trim();

            if (fname.isEmpty() || lname.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name, last name, and email are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Employee emp = new Employee(selectedEmpID, fname, lname, email, hire, salary, ssn, 0);
            if (dao.updateEmployee(emp)) {
                JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAll();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid number.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearForm() {
        selectedEmpID = -1;
        empIdLabel.setText("ID: —");
        for (JTextField f : new JTextField[]{firstNameField, lastNameField, emailField, hireDateField, salaryField, ssnField})
            f.setText("");
        table.clearSelection();
    }

    // ── Helpers ──
    private JTable buildTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(24);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(25, 55, 109));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(179, 212, 255));
        t.setGridColor(new Color(220, 225, 235));
        return t;
    }

    private JTextField formField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return f;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(new Color(80, 80, 80));
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        p.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return p;
    }

    private JButton actionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
