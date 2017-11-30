package android.plumberhub.com.plumberhubapp;

import java.util.Date;
import java.util.List;

/**
 * Created by razva on 2017-11-29.
 */

public class Trip {
    private String customerName;
    private Date date;
    private List<String> services;
    private double totalCost;

    public Trip(){

    }

    public Trip(String customerName, Date date, List<String> services, double totalCost) {
        this.customerName = customerName;
        this.date = date;
        this.services = services;
        this.totalCost = totalCost;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
