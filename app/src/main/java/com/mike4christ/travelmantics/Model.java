package com.mike4christ.travelmantics;

public class Model {
    String img_url;
    String holidy_title;
    String description;
    String amount;

    public Model(){

    }


    public Model(String img_url, String holidy_title, String description, String amount) {
        this.img_url = img_url;
        this.holidy_title = holidy_title;
        this.description = description;
        this.amount = amount;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getTitle() {
        return holidy_title;
    }

    public void setTitle(String holidy_title) {
        this.holidy_title = holidy_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
