package com.gilmaimon.israelposttracker.Branches;

public class Branch {
    private final int id;
    private final String name;
    private final String address;
    private final String active;
    private final String phone;

    public Branch(int id, String name, String address, String active, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.active = active;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getActive() {
        return active;
    }
}
