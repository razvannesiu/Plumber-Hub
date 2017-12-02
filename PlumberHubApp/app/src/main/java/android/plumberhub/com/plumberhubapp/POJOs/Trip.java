package android.plumberhub.com.plumberhubapp.POJOs;

import java.util.List;

/**
 * Created by razva on 2017-11-29.
 */

public class Trip {
    private String customerName;
    private long time;
    private List<String> services;
    private double totalCost;

    public Trip(){

    }

    public Trip(String customerName, long date, List<String> services, double totalCost) {
        this.customerName = customerName;
        this.time = date;
        this.services = services;
        this.totalCost = totalCost;
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
