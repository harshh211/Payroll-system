package model;

public class Employee {
    private int empid;
    private String fname;
    private String lname;
    private String email;
    private String hireDate;
    private double salary;
    private String ssn;
    private int addressID;

    public Employee() {}

    public Employee(int empid, String fname, String lname, String email,
                    String hireDate, double salary, String ssn, int addressID) {
        this.empid = empid;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.hireDate = hireDate;
        this.salary = salary;
        this.ssn = ssn;
        this.addressID = addressID;
    }

    public int getEmpid() { return empid; }
    public void setEmpid(int empid) { this.empid = empid; }

    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }

    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

    public int getAddressID() { return addressID; }
    public void setAddressID(int addressID) { this.addressID = addressID; }

    @Override
    public String toString() {
        return empid + " - " + fname + " " + lname;
    }
}
