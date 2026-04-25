package model;

public class Payroll {
    private int payID;
    private String payDate;
    private double earnings;
    private double fedTax;
    private double fedMed;
    private double fedSS;
    private double stateTax;
    private double retire401k;
    private double healthCare;
    private int empid;

    public Payroll() {}

    public Payroll(int payID, String payDate, double earnings, double fedTax,
                   double fedMed, double fedSS, double stateTax,
                   double retire401k, double healthCare, int empid) {
        this.payID = payID;
        this.payDate = payDate;
        this.earnings = earnings;
        this.fedTax = fedTax;
        this.fedMed = fedMed;
        this.fedSS = fedSS;
        this.stateTax = stateTax;
        this.retire401k = retire401k;
        this.healthCare = healthCare;
        this.empid = empid;
    }

    public double getNetPay() {
        return earnings - fedTax - fedMed - fedSS - stateTax - retire401k - healthCare;
    }

    public double getTotalDeductions() {
        return fedTax + fedMed + fedSS + stateTax + retire401k + healthCare;
    }

    public int getPayID() { return payID; }
    public void setPayID(int payID) { this.payID = payID; }

    public String getPayDate() { return payDate; }
    public void setPayDate(String payDate) { this.payDate = payDate; }

    public double getEarnings() { return earnings; }
    public void setEarnings(double earnings) { this.earnings = earnings; }

    public double getFedTax() { return fedTax; }
    public void setFedTax(double fedTax) { this.fedTax = fedTax; }

    public double getFedMed() { return fedMed; }
    public void setFedMed(double fedMed) { this.fedMed = fedMed; }

    public double getFedSS() { return fedSS; }
    public void setFedSS(double fedSS) { this.fedSS = fedSS; }

    public double getStateTax() { return stateTax; }
    public void setStateTax(double stateTax) { this.stateTax = stateTax; }

    public double getRetire401k() { return retire401k; }
    public void setRetire401k(double retire401k) { this.retire401k = retire401k; }

    public double getHealthCare() { return healthCare; }
    public void setHealthCare(double healthCare) { this.healthCare = healthCare; }

    public int getEmpid() { return empid; }
    public void setEmpid(int empid) { this.empid = empid; }
}
