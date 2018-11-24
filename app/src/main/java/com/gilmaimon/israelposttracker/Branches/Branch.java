package com.gilmaimon.israelposttracker.Branches;

public class Branch {
    private int id;
    private String name;
    private String address;

    public Branch(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
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
}
