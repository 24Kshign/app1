package com.maihaoche.volvo.ui.instorage.event;

import java.io.Serializable;

/**
 * Created by gujian
 * Time is 2017/8/2
 * Email is gujian@maihaoche.com
 */

public class ContactEvent implements Serializable{

    private String name;
    private String phone;
    private String contact;

    public ContactEvent() {
    }

    public ContactEvent(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public ContactEvent(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
