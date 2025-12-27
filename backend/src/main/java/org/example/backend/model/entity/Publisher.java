package org.example.backend.model.entity;

public class Publisher {
    private Integer publisherId;
    private String name;
    private String address;
    private String phone;

    public Publisher() {
    }

    public Publisher(Integer publisherId, String name, String address, String phone) {
        this.publisherId = publisherId;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

