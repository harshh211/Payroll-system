package ui;

import dao.EmployeeDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DeleteEmployeePanel extends JPanel {

    private final EmployeeDAO dao = new EmployeeDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField fnSearch, lnSearch;

    public DeleteEmployeePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initUI();
    }

    private void initUI() {
        JLabel pageTitle = new JLabel("Delete Employee");
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
        searchBar.add(new JLabel("First Name:"));
        searchBar.add(fnSearch);
        searchBar.add(new JLabel("Last Name:"));
        searchBar.add(lnSearch);
        searchBar.add(searchBtn);
        searchBar.add(showAllBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 252));
        topPanel.add(pageTitle, BorderLayout.NORTH);
        topPanel.add(searchBar, BorderLayout.CENTER);

        // Table
        String[] cols = {"ID", "First Name", "Last Name", "Email", "Hire Date", "Salary"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable(tableModel);

        // Warning label
        JLabel warning = new JLabel("⚠  Deleting an employee will also remove all their payroll records. This cannot be undone.");
        warning.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        warning.setForeground(new Color(180, 60, 40));
        warning.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Delete button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 247, 252));
        JButton deleteBtn = btn("Delete Selected Employee", new Color(198, 40, 40));
        deleteBtn.setPreferredSize(new Dimension(220, 34));
        JPanel deleteBtnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        deleteBtnRow.setBackground(new Color(245, 247, 252));
        deleteBtnRow.add(deleteBtn);
        bottomPanel.add(warning,      BorderLayout.CENTER);
        bottomPanel.add(deleteBtnRow, BorderLayout.EAST);

        // Wire
        searchBtn.addActionListener(e -> {
            String fn = fnSearch.getText().trim();
            String ln = lnSearch.getText().trim();
            tableModel.setRowCount(0);
            for (Employee emp : dao.searchByName(fn, ln)) addRow(emp);
        });
        showAllBtn.addActionListener(e -> loadAll());
        deleteBtn.addActionListener(e  -> deleteSelected());

        add(topPanel,              BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel,           BorderLayout.SOUTH);

        loadAll();
    }

    private void loadAll() {
        tableModel.setRowCount(0);
        for (Employee e : dao.getAllEmployees()) addRow(e);
    }

    private void addRow(Employee e) {
        tableModel.addRow(new Object[]{
            e.getEmpid(), e.getFname(), e.getLname(),
            e.getEmail(), e.getHireDate(),
            String.format("$%,.2f", e.getSalary())
        });
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int empID = (int) tableModel.getValueAt(row, 0);
        String name = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + name + " (ID: " + empID + ")?\nAll payroll records will also be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.deleteEmployee(empID)) {
                JOptionPane.showMessageDialog(this, name + " has been deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JTable buildTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(24);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(25, 55, 109));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(255, 200, 200));
        t.setGridColor(new Color(220, 225, 235));
        return t;
    }

    private JButton btn(String text, Color bg) {
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
