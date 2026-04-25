package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public abstract class BaseReportPanel extends JPanel {

    protected String fmt(double v) { return String.format("$%,.2f", v); }

    protected JPanel topBar(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 247, 252));
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(25, 55, 109));
        p.add(lbl, BorderLayout.WEST);
        return p;
    }

    protected JButton reportBtn(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(new Color(25, 118, 210));
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    protected JTable buildTable(DefaultTableModel m) {
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

    protected void rightAlignCols(JTable t, int... cols) {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int c : cols) t.getColumnModel().getColumn(c).setCellRenderer(r);
    }

    protected JLabel totLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(Color.WHITE);
        return l;
    }

    protected JLabel sepLabel() {
        JLabel l = new JLabel("|");
        l.setForeground(new Color(100, 130, 170));
        return l;
    }
}
