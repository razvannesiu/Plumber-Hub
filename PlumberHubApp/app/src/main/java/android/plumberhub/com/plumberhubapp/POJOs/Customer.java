package android.plumberhub.com.plumberhubapp.POJOs;

import java.io.Serializable;

/**
 * Created by razva on 2017-11-26.
 */

public class Customer implements Serializable {

    private String name;
    private String address;
    private String phone;
    private String email;

    public Customer(){
    }

    public Customer(String name, String address, String phone, String email){
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Name: " + name + '\n' +
                "Address: " + address + '\n' +
                "Phone: " + phone + '\n' +
                "Email: " + email;
    }
}
