package com.alokdev21.foodapk;

import java.util.Objects;

public class Dish {

    private String name;
    private Long price;
    private String imageUrl;

    // Default constructor
    public Dish() {}

    // Parameterized constructor
    public Dish(String name, Long price, String imageUrl) {
        this.name = name;
        this.price = price;
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

    // Override toString() for better readability
    @Override
    public String toString() {
        return String.format("Dish{name='%s', price=%d, imageUrl='%s'}", name, price, imageUrl);
    }

    // Override equals() and hashCode() for object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(name, dish.name) &&
                Objects.equals(price, dish.price) &&
                Objects.equals(imageUrl, dish.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, imageUrl);
    }
}
