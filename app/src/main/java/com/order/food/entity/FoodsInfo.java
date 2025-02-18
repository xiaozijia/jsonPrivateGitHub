package com.order.food.entity;

import java.io.Serializable;


public class FoodsInfo implements Serializable {

    private int _id;
    private String title;
    private int price;
    private int image;
    private String detail;
    private String imageUrl;
    private String  content;


    public FoodsInfo(int _id, String title, int price, int image, String detail, String imageUrl,String content) {
        this._id = _id;
        this.title = title;
        this.price = price;
        this.image = image;
        this.detail = detail;
        this.imageUrl = imageUrl;
        this.content=content;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String imageUrl) {
        this.content = content;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImage() {
        return image;
    }


    public void setImage(int image) {
        this.image = image;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}