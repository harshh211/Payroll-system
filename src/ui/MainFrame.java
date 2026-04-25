package ui;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private final Color sidebarBg     = new Color(28, 52, 90);
    private final Color sidebarHover  = new Color(45, 80, 130);
    private final Color sidebarActive = new Color(59, 130, 246);
    private JButton activeBtn = null;

    public MainFrame() {
        setTitle("Company HRMS – HR Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(950, 600));
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ── Top Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 40, 75));
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel logo = new JLabel("Company HRMS");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Signed in as: HR Admin");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(180, 200, 230));

        header.add(logo, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);

        // ── Sidebar ──
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarBg);
        sidebar.setPreferredSize(new Dimension(215, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        sidebar.add(sectionHeader("HR Administration"));
        JButton searchEditBtn = sidebarButton("Search & Edit Employee");
        JButton addBtn        = sidebarButton("Add New Employee");
        JButton deleteBtn     = sidebarButton("Delete Employee");
        JButton updateSalBtn  = sidebarButton("Update Salary by %");
        sidebar.add(searchEditBtn);
        sidebar.add(addBtn);
        sidebar.add(deleteBtn);
        sidebar.add(updateSalBtn);

        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(sectionHeader("Reports"));
        JButton rptJobBtn     = sidebarButton("Pay by Job Title");
        JButton rptDivBtn     = sidebarButton("Pay by Division");
        JButton rptHiresBtn   = sidebarButton("New Hires in Date Range");
        JButton rptDetailBtn  = sidebarButton("Full Payroll Detail");
        JButton rptSummaryBtn = sidebarButton("Payroll Summary");
        sidebar.add(rptJobBtn);
        sidebar.add(rptDivBtn);
        sidebar.add(rptHiresBtn);
        sidebar.add(rptDetailBtn);
        sidebar.add(rptSummaryBtn);
        sidebar.add(Box.createVerticalGlue());

        // ── Content CardLayout ──
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 252));

        contentPanel.add(new WelcomePanel(),               "welcome");
        contentPanel.add(new SearchEditPanel(),            "Search & Edit Employee");
        contentPanel.add(new AddEmployeePanel(),           "Add New Employee");
        contentPanel.add(new DeleteEmployeePanel(),        "Delete Employee");
        contentPanel.add(new UpdateSalaryPanel(),          "Update Salary by %");
        contentPanel.add(new ReportJobTitlePanel(),        "Pay by Job Title");
        contentPanel.add(new ReportDivisionPanel(),        "Pay by Division");
        contentPanel.add(new ReportNewHiresPanel(),        "New Hires in Date Range");
        contentPanel.add(new PayrollDetailPanel(),         "Full Payroll Detail");
        contentPanel.add(new PayrollSummaryPanel(),        "Payroll Summary");

        // Wire buttons → panels
        searchEditBtn.addActionListener(e -> show("Search & Edit Employee", searchEditBtn));
        addBtn.addActionListener(e        -> show("Add New Employee",        addBtn));
        deleteBtn.addActionListener(e     -> show("Delete Employee",         deleteBtn));
        updateSalBtn.addActionListener(e  -> show("Update Salary by %",      updateSalBtn));
        rptJobBtn.addActionListener(e     -> show("Pay by Job Title",        rptJobBtn));
        rptDivBtn.addActionListener(e     -> show("Pay by Division",         rptDivBtn));
        rptHiresBtn.addActionListener(e   -> show("New Hires in Date Range", rptHiresBtn));
        rptDetailBtn.addActionListener(e  -> show("Full Payroll Detail",     rptDetailBtn));
        rptSummaryBtn.addActionListener(e -> show("Payroll Summary",         rptSummaryBtn));

        add(header,       BorderLayout.NORTH);
        add(sidebar,      BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void show(String card, JButton btn) {
        cardLayout.show(contentPanel, card);
        if (activeBtn != null) activeBtn.setBackground(sidebarBg);
        btn.setBackground(sidebarActive);
        activeBtn = btn;
    }

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(210, 225, 255));
        btn.setBackground(sidebarBg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(sidebarHover);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(sidebarBg);
            }
        });
        return btn;
    }

    private JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel("  " + text.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(120, 150, 190));
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return lbl;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}
