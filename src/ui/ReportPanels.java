package ui;

import dao.EmployeeDAO;
import dao.PayrollDAO;
import model.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// ── 1. Pay by Job Title ───────────────────────────────────────────────────────
class ReportJobTitlePanel extends BaseReportPanel {
    private final PayrollDAO dao = new PayrollDAO();
    private DefaultTableModel model;

    ReportJobTitlePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = topBar("Pay by Job Title (Monthly)");
        JButton refreshBtn = reportBtn("Refresh");
        top.add(refreshBtn, BorderLayout.EAST);

        String[] cols = {"Job Title", "Employee Count", "Total Earnings", "Avg Earnings"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable table = buildTable(model);
        rightAlignCols(table, 1, 2, 3);

        refreshBtn.addActionListener(e -> load());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        load();
    }

    private void load() {
        model.setRowCount(0);
        for (Object[] row : dao.getPayByJobTitle())
            model.addRow(new Object[]{row[0], row[1], fmt((double)row[2]), fmt((double)row[3])});
    }
}

// ── 2. Pay by Division ────────────────────────────────────────────────────────
class ReportDivisionPanel extends BaseReportPanel {
    private final PayrollDAO dao = new PayrollDAO();
    private DefaultTableModel model;

    ReportDivisionPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = topBar("Pay by Division (Monthly)");
        JButton refreshBtn = reportBtn("Refresh");
        top.add(refreshBtn, BorderLayout.EAST);

        String[] cols = {"Division", "City", "Employee Count", "Total Earnings", "Avg Earnings"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable table = buildTable(model);
        rightAlignCols(table, 2, 3, 4);

        refreshBtn.addActionListener(e -> load());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        load();
    }

    private void load() {
        model.setRowCount(0);
        for (Object[] row : dao.getPayByDivision())
            model.addRow(new Object[]{row[0], row[1], row[2], fmt((double)row[3]), fmt((double)row[4])});
    }
}

// ── 3. New Hires in Date Range ────────────────────────────────────────────────
class ReportNewHiresPanel extends BaseReportPanel {
    private final EmployeeDAO dao = new EmployeeDAO();
    private DefaultTableModel model;
    private JTextField fromField, toField;

    ReportNewHiresPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("New Hires in Date Range");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(25, 55, 109));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterBar.setBackground(new Color(245, 247, 252));
        fromField = dateField("2024-01-01");
        toField   = dateField("2025-12-31");
        JButton runBtn = reportBtn("Run Report");
        filterBar.add(new JLabel("From:")); filterBar.add(fromField);
        filterBar.add(new JLabel("To:"));   filterBar.add(toField);
        filterBar.add(runBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 252));
        topPanel.add(title,     BorderLayout.NORTH);
        topPanel.add(filterBar, BorderLayout.CENTER);

        String[] cols = {"ID", "First Name", "Last Name", "Email", "Hire Date", "Salary"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable table = buildTable(model);

        runBtn.addActionListener(e -> load());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void load() {
        model.setRowCount(0);
        List<Employee> hires = dao.getNewHires(fromField.getText().trim(), toField.getText().trim());
        if (hires.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No new hires found in that date range.", "Result", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (Employee e : hires)
            model.addRow(new Object[]{e.getEmpid(), e.getFname(), e.getLname(), e.getEmail(), e.getHireDate(), fmt(e.getSalary())});
    }

    private JTextField dateField(String def) {
        JTextField f = new JTextField(def, 11);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }
}

// ── 4. Full Payroll Detail ────────────────────────────────────────────────────
class PayrollDetailPanel extends BaseReportPanel {
    private final PayrollDAO dao = new PayrollDAO();
    private DefaultTableModel model;
    private JLabel totEarnings, totDeductions, totNet;

    PayrollDetailPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = topBar("Full Payroll Detail Report");
        JButton refreshBtn = reportBtn("Refresh");
        top.add(refreshBtn, BorderLayout.EAST);

        JPanel totalsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        totalsBar.setBackground(new Color(25, 55, 109));
        totalsBar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        totEarnings   = totLabel("Total Earnings: $0.00");
        totDeductions = totLabel("Total Deductions: $0.00");
        totNet        = totLabel("Total Net Pay: $0.00");
        totNet.setForeground(new Color(130, 255, 160));
        totalsBar.add(totEarnings); totalsBar.add(sepLabel());
        totalsBar.add(totDeductions); totalsBar.add(sepLabel());
        totalsBar.add(totNet);

        String[] cols = {"Emp ID","Employee","Pay Date","Gross","Fed Tax","Medicare","Soc Sec","State Tax","401k","Health","Net Pay"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable table = buildTable(model);
        rightAlignCols(table, 3,4,5,6,7,8,9,10);

        JPanel topSection = new JPanel(new BorderLayout(0, 4));
        topSection.setBackground(new Color(245, 247, 252));
        topSection.add(top,       BorderLayout.NORTH);
        topSection.add(totalsBar, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> load());
        add(topSection, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        load();
    }

    private void load() {
        model.setRowCount(0);
        double sumE=0, sumD=0, sumN=0;
        for (Object[] row : dao.getFullPayrollReport()) {
            double e=(double)row[3],ft=(double)row[4],fm=(double)row[5],
                   fs=(double)row[6],st=(double)row[7],r=(double)row[8],
                   h=(double)row[9],net=(double)row[10];
            double d=ft+fm+fs+st+r+h;
            sumE+=e; sumD+=d; sumN+=net;
            model.addRow(new Object[]{row[0],row[1],row[2],fmt(e),fmt(ft),fmt(fm),fmt(fs),fmt(st),fmt(r),fmt(h),fmt(net)});
        }
        totEarnings.setText(String.format("Total Earnings: $%,.2f", sumE));
        totDeductions.setText(String.format("Total Deductions: $%,.2f", sumD));
        totNet.setText(String.format("Total Net Pay: $%,.2f", sumN));
    }
}

// ── 5. Payroll Summary ────────────────────────────────────────────────────────
class PayrollSummaryPanel extends BaseReportPanel {
    private final PayrollDAO dao = new PayrollDAO();
    private DefaultTableModel model;

    PayrollSummaryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = topBar("Payroll Summary by Employee");
        JButton refreshBtn = reportBtn("Refresh");
        top.add(refreshBtn, BorderLayout.EAST);

        String[] cols = {"Emp ID","Employee","Total Earnings","Total Deductions","Total Net Pay"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        JTable table = buildTable(model);
        rightAlignCols(table, 2, 3, 4);

        refreshBtn.addActionListener(e -> load());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        load();
    }

    private void load() {
        model.setRowCount(0);
        for (Object[] row : dao.getPayrollSummary())
            model.addRow(new Object[]{row[0],row[1],fmt((double)row[2]),fmt((double)row[3]),fmt((double)row[4])});
    }
}
