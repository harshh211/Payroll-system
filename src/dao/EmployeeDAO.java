package dao;

import model.Address;
import model.Employee;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // INSERT employee + address in one transaction
    public boolean insertEmployee(Employee emp, Address addr) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String addrSQL = "INSERT INTO addresses (street, cityID, stateID, zip, DOB, phone, emergency_contact, emergency_contact_phone) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement addrStmt = conn.prepareStatement(addrSQL, Statement.RETURN_GENERATED_KEYS);
            addrStmt.setString(1, addr.getStreet());
            addrStmt.setInt(2, addr.getCityID());
            addrStmt.setInt(3, addr.getStateID());
            addrStmt.setString(4, addr.getZip());
            addrStmt.setString(5, addr.getDob());
            addrStmt.setString(6, addr.getPhone());
            addrStmt.setString(7, addr.getEmergencyContact());
            addrStmt.setString(8, addr.getEmergencyContactPhone());
            addrStmt.executeUpdate();

            int newAddrID = 0;
            ResultSet keys = addrStmt.getGeneratedKeys();
            if (keys.next()) newAddrID = keys.getInt(1);

            String empSQL = "INSERT INTO employees (Fname, Lname, email, HireDate, Salary, SSN, addressID) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement empStmt = conn.prepareStatement(empSQL);
            empStmt.setString(1, emp.getFname());
            empStmt.setString(2, emp.getLname());
            empStmt.setString(3, emp.getEmail());
            empStmt.setString(4, emp.getHireDate());
            empStmt.setDouble(5, emp.getSalary());
            empStmt.setString(6, emp.getSsn());
            empStmt.setInt(7, newAddrID);
            empStmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // GET all employees
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY Lname, Fname";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // GET employee by ID
    public Employee getEmployeeById(int empid) {
        String sql = "SELECT * FROM employees WHERE empid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // SEARCH by first name and/or last name
    public List<Employee> searchByName(String fname, String lname) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE Fname LIKE ? AND Lname LIKE ? ORDER BY Lname, Fname";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fname.isEmpty() ? "%" : "%" + fname + "%");
            stmt.setString(2, lname.isEmpty() ? "%" : "%" + lname + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // SEARCH by keyword (ID, name, email)
    public List<Employee> searchEmployees(String keyword) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE CAST(empid AS CHAR) LIKE ? OR Fname LIKE ? OR Lname LIKE ? OR email LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            stmt.setString(1, p); stmt.setString(2, p);
            stmt.setString(3, p); stmt.setString(4, p);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // GET new hires in date range
    public List<Employee> getNewHires(String fromDate, String toDate) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE HireDate BETWEEN ? AND ? ORDER BY HireDate DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fromDate);
            stmt.setString(2, toDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // UPDATE employee
    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE employees SET Fname=?, Lname=?, email=?, HireDate=?, Salary=?, SSN=? WHERE empid=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, emp.getFname());
            stmt.setString(2, emp.getLname());
            stmt.setString(3, emp.getEmail());
            stmt.setString(4, emp.getHireDate());
            stmt.setDouble(5, emp.getSalary());
            stmt.setString(6, emp.getSsn());
            stmt.setInt(7, emp.getEmpid());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // DELETE employee and all related records
    public boolean deleteEmployee(int empid) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            for (String sql : new String[]{
                "DELETE FROM payroll WHERE empid=?",
                "DELETE FROM employee_division WHERE empid=?",
                "DELETE FROM employee_job_titles WHERE empid=?",
                "DELETE FROM employees WHERE empid=?"
            }) {
                PreparedStatement s = conn.prepareStatement(sql);
                s.setInt(1, empid);
                s.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
            rs.getInt("empid"), rs.getString("Fname"), rs.getString("Lname"),
            rs.getString("email"), rs.getString("HireDate"),
            rs.getDouble("Salary"), rs.getString("SSN"), rs.getInt("addressID")
        );
    }
}
