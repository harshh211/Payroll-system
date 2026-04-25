package dao;

import model.Payroll;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {

    // INSERT payroll record
    public boolean insertPayroll(Payroll p) {
        String sql = "INSERT INTO payroll (pay_date, earnings, fed_tax, fed_med, fed_SS, state_tax, retire_401k, health_care, empid) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getPayDate());
            stmt.setDouble(2, p.getEarnings());
            stmt.setDouble(3, p.getFedTax());
            stmt.setDouble(4, p.getFedMed());
            stmt.setDouble(5, p.getFedSS());
            stmt.setDouble(6, p.getStateTax());
            stmt.setDouble(7, p.getRetire401k());
            stmt.setDouble(8, p.getHealthCare());
            stmt.setInt(9, p.getEmpid());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // FULL payroll detail (all employees, all records)
    public List<Object[]> getFullPayrollReport() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT e.empid, CONCAT(e.Fname,' ',e.Lname) AS name, p.pay_date, " +
                     "p.earnings, p.fed_tax, p.fed_med, p.fed_SS, p.state_tax, p.retire_401k, p.health_care, " +
                     "(p.earnings - p.fed_tax - p.fed_med - p.fed_SS - p.state_tax - p.retire_401k - p.health_care) AS net_pay " +
                     "FROM payroll p JOIN employees e ON p.empid = e.empid ORDER BY e.Lname, p.pay_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("empid"), rs.getString("name"), rs.getString("pay_date"),
                    rs.getDouble("earnings"), rs.getDouble("fed_tax"), rs.getDouble("fed_med"),
                    rs.getDouble("fed_SS"), rs.getDouble("state_tax"), rs.getDouble("retire_401k"),
                    rs.getDouble("health_care"), rs.getDouble("net_pay")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // SUMMARY: total earnings/deductions/net per employee
    public List<Object[]> getPayrollSummary() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT e.empid, CONCAT(e.Fname,' ',e.Lname) AS name, " +
                     "SUM(p.earnings) AS total_earnings, " +
                     "SUM(p.fed_tax+p.fed_med+p.fed_SS+p.state_tax+p.retire_401k+p.health_care) AS total_deductions, " +
                     "SUM(p.earnings-p.fed_tax-p.fed_med-p.fed_SS-p.state_tax-p.retire_401k-p.health_care) AS net_pay " +
                     "FROM payroll p JOIN employees e ON p.empid=e.empid GROUP BY e.empid ORDER BY e.Lname";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("empid"), rs.getString("name"),
                    rs.getDouble("total_earnings"), rs.getDouble("total_deductions"), rs.getDouble("net_pay")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // REPORT: Pay grouped by Job Title
    public List<Object[]> getPayByJobTitle() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT jt.job_title, COUNT(DISTINCT e.empid) AS emp_count, " +
                     "SUM(p.earnings) AS total_earnings, AVG(p.earnings) AS avg_earnings " +
                     "FROM payroll p " +
                     "JOIN employees e ON p.empid = e.empid " +
                     "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                     "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
                     "GROUP BY jt.job_title ORDER BY total_earnings DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("job_title"), rs.getInt("emp_count"),
                    rs.getDouble("total_earnings"), rs.getDouble("avg_earnings")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // REPORT: Pay grouped by Division
    public List<Object[]> getPayByDivision() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT d.Name AS division, d.city, COUNT(DISTINCT e.empid) AS emp_count, " +
                     "SUM(p.earnings) AS total_earnings, AVG(p.earnings) AS avg_earnings " +
                     "FROM payroll p " +
                     "JOIN employees e ON p.empid = e.empid " +
                     "JOIN employee_division ed ON e.empid = ed.empid " +
                     "JOIN division d ON ed.div_ID = d.ID " +
                     "GROUP BY d.Name, d.city ORDER BY total_earnings DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("division"), rs.getString("city"), rs.getInt("emp_count"),
                    rs.getDouble("total_earnings"), rs.getDouble("avg_earnings")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // GET payroll by employee ID
    public List<Payroll> getPayrollByEmployee(int empid) {
        List<Payroll> list = new ArrayList<>();
        String sql = "SELECT * FROM payroll WHERE empid=? ORDER BY pay_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Payroll mapRow(ResultSet rs) throws SQLException {
        return new Payroll(
            rs.getInt("payID"), rs.getString("pay_date"),
            rs.getDouble("earnings"), rs.getDouble("fed_tax"), rs.getDouble("fed_med"),
            rs.getDouble("fed_SS"), rs.getDouble("state_tax"), rs.getDouble("retire_401k"),
            rs.getDouble("health_care"), rs.getInt("empid")
        );
    }
}
