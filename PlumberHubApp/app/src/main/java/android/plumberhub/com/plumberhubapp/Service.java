package android.plumberhub.com.plumberhubapp;

/**
 * Created by razva on 2017-11-28.
 */

public class Service {

    private String title;
    private String imageUrl;
    private String description;
    private String[] tools;
    private double price;

    public Service(){

    }

    public Service(String title, String imageUrl, String description, String[] tools, double price) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.tools = tools;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTools() {
        return tools;
    }

    public void setTools(String[] tools) {
        this.tools = tools;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
