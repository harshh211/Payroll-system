package ui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UpdateSalaryPanel extends JPanel {

    private final EmployeeDAO dao = new EmployeeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField fnSearch, lnSearch, percentField;
    private JRadioButton increaseRb, decreaseRb;

    public UpdateSalaryPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JLabel pageTitle = new JLabel("Update Salary by Percentage");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitle.setForeground(new Color(25, 55, 109));
        pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchBar.setBackground(new Color(245, 247, 252));
        fnSearch = new JTextField(12); fnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lnSearch = new JTextField(12); lnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton searchBtn  = btn("Search",   new Color(25, 118, 210));
        JButton showAllBtn = btn("Show All", new Color(76, 125, 50));
        searchBar.add(new JLabel("First Name:")); searchBar.add(fnSearch);
        searchBar.add(new JLabel("Last Name:"));  searchBar.add(lnSearch);
        searchBar.add(searchBtn); searchBar.add(showAllBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 252));
        topPanel.add(pageTitle, BorderLayout.NORTH);
        topPanel.add(searchBar, BorderLayout.CENTER);

        // Table
        String[] cols = {"ID", "First Name", "Last Name", "Current Salary"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable(tableModel);

        // Bottom controls
        JPanel bottomCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bottomCard.setBackground(Color.WHITE);
        bottomCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 240)),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        increaseRb = new JRadioButton("Increase", true);
        decreaseRb = new JRadioButton("Decrease");
        increaseRb.setBackground(Color.WHITE);
        decreaseRb.setBackground(Color.WHITE);
        ButtonGroup bg = new ButtonGroup();
        bg.add(increaseRb); bg.add(decreaseRb);

        percentField = new JTextField("5.0", 6);
        percentField.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton applyBtn = btn("Apply to Selected", new Color(123, 31, 162));
        JButton applyAllBtn = btn("Apply to ALL",   new Color(198, 40, 40));

        bottomCard.add(new JLabel("Adjustment:"));
        bottomCard.add(increaseRb);
        bottomCard.add(decreaseRb);
        bottomCard.add(new JLabel("   Percentage:"));
        bottomCard.add(percentField);
        bottomCard.add(new JLabel("%"));
        bottomCard.add(Box.createHorizontalStrut(10));
        bottomCard.add(applyBtn);
        bottomCard.add(applyAllBtn);

        // Wire
        searchBtn.addActionListener(e -> {
            tableModel.setRowCount(0);
            for (Employee emp : dao.searchByName(fnSearch.getText().trim(), lnSearch.getText().trim())) addRow(emp);
        });
        showAllBtn.addActionListener(e -> loadAll());
        applyBtn.addActionListener(e   -> applyToSelected());
        applyAllBtn.addActionListener(e -> applyToAll());

        add(topPanel,              BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomCard,            BorderLayout.SOUTH);

        loadAll();
    }

    private void loadAll() {
        tableModel.setRowCount(0);
        for (Employee e : dao.getAllEmployees()) addRow(e);
    }

    private void addRow(Employee e) {
        tableModel.addRow(new Object[]{e.getEmpid(), e.getFname(), e.getLname(), String.format("$%,.2f", e.getSalary())});
    }

    private void applyToSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int empID = (int) tableModel.getValueAt(row, 0);
        Employee emp = dao.getEmployeeById(empID);
        if (emp == null) return;
        double pct = parsePct(); if (pct < 0) return;
        double newSalary = increaseRb.isSelected()
            ? emp.getSalary() * (1 + pct / 100)
            : emp.getSalary() * (1 - pct / 100);
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Change salary for %s %s from $%,.2f to $%,.2f?",
                emp.getFname(), emp.getLname(), emp.getSalary(), newSalary),
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            emp.setSalary(newSalary);
            if (dao.updateEmployee(emp)) {
                JOptionPane.showMessageDialog(this, "Salary updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAll();
            }
        }
    }

    private void applyToAll() {
        double pct = parsePct(); if (pct < 0) return;
        String direction = increaseRb.isSelected() ? "increase" : "decrease";
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Apply %.1f%% %s to ALL employees? This cannot be undone.", pct, direction),
            "Confirm Apply All", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            List<Employee> all = dao.getAllEmployees();
            int updated = 0;
            for (Employee emp : all) {
                double newSal = increaseRb.isSelected()
                    ? emp.getSalary() * (1 + pct / 100)
                    : emp.getSalary() * (1 - pct / 100);
                emp.setSalary(newSal);
                if (dao.updateEmployee(emp)) updated++;
            }
            JOptionPane.showMessageDialog(this, updated + " employees updated.", "Done", JOptionPane.INFORMATION_MESSAGE);
            loadAll();
        }
    }

    private double parsePct() {
        try {
            double v = Double.parseDouble(percentField.getText().trim());
            if (v <= 0 || v > 100) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid percentage (0-100).", "Validation", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
    }

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

    private JButton btn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
