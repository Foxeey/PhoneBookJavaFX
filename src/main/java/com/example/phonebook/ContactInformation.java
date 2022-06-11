package com.example.phonebook;

public class ContactInformation {
    private int id;
    private String name;
    private String sname;
    private String number;
    private String street;
    private String city;
    private String post;

    public ContactInformation(int ID, String fName, String lName, String pNumber, String streetAdr, String cityAdr, String postAdr) {
        this.id = ID;
        this.name = fName;
        this.sname = lName;
        this.number = pNumber;
        this.street = streetAdr;
        this.city = cityAdr;
        this.post = postAdr;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSname() {
        return sname;
    }

    public String getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPost() {
        return post;
    }
}
