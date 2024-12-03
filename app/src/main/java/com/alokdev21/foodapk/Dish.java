package com.alokdev21.foodapk;

public class Dish {

    private String name;
    private Long price;
    private String imageUrl;

    public Dish() {}

    public Dish(String name, Long price, String imageUrl) {
        this.name = name;
        this.price=price;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
