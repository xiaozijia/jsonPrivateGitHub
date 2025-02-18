package com.order.food.entity;

import java.io.Serializable;

public class PictureInfo implements Serializable {
    private int id;
    private String imageUrl;

    // 构造函数
    public PictureInfo(int id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    // id的getter和setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // imageUrl的getter和setter方法
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // toString方法，用于打印Bean的信息
    @Override
    public String toString() {
        return "ImageBean{" +
                "id=" + id +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
