package com.order.food.entity;

/**
 */
public class CarInfo {

    private  int _id;
    private String mobile;
    private String title;
    private int price;
    private int image;
    private int food_num;
    private String detail;

    public CarInfo(int _id, String mobile, String title, int price, int image, int food_num, String detail) {
        this._id = _id;
        this.mobile = mobile;
        this.title = title;
        this.price = price;
        this.image = image;
        this.food_num = food_num;
        this.detail = detail;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getFood_num() {
        return food_num;
    }

    public void setFood_num(int food_num) {
        this.food_num = food_num;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "CarInfo{" +
                "_id=" + _id +
                ", mobile='" + mobile + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", image=" + image +
                ", food_num=" + food_num +
                ", detail='" + detail + '\'' +
                '}';
    }
}
