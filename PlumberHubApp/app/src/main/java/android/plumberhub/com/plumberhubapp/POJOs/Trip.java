package android.plumberhub.com.plumberhubapp.POJOs;

import java.util.List;

/**
 * Created by razva on 2017-11-29.
 */

public class Trip {
    private String customerName;
    private String customerEmail;
    private long time;
    private List<String> services;
    private double totalCost;

    public Trip(){

    }

    public Trip(String customerName, String customerEmail, long time, List<String> services, double totalCost) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.time = time;
        this.services = services;
        this.totalCost = totalCost;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
