package model;

public class Address {
    private int addressID;
    private String street;
    private int cityID;
    private int stateID;
    private String zip;
    private String dob;
    private String phone;
    private String emergencyContact;
    private String emergencyContactPhone;

    public Address() {}

    public Address(int addressID, String street, int cityID, int stateID,
                   String zip, String dob, String phone,
                   String emergencyContact, String emergencyContactPhone) {
        this.addressID = addressID;
        this.street = street;
        this.cityID = cityID;
        this.stateID = stateID;
        this.zip = zip;
        this.dob = dob;
        this.phone = phone;
        this.emergencyContact = emergencyContact;
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public int getAddressID() { return addressID; }
    public void setAddressID(int addressID) { this.addressID = addressID; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public int getCityID() { return cityID; }
    public void setCityID(int cityID) { this.cityID = cityID; }

    public int getStateID() { return stateID; }
    public void setStateID(int stateID) { this.stateID = stateID; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
}
