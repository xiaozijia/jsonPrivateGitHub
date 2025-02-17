package com.order.food.entity;


public class OrderInfo {//这个类是跟数据库里面用到的表对应的结构

    private  int _id;
    private String mobile;
    private String title;
    private int price;
    private String image;
    private int food_num;
    private String detail;

    private String pay_method;
    private String address;
    private String order_num;

    public OrderInfo(int _id, String mobile, String title, int price, String image, int food_num, String detail, String pay_method, String address, String order_num) {
        this._id = _id;
        this.mobile = mobile;
        this.title = title;
        this.price = price;
        this.image = image;
        this.food_num = food_num;
        this.detail = detail;
        this.pay_method = pay_method;
        this.address = address;
        this.order_num = order_num;
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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

    public String getPay_method() {
        return pay_method;
    }

    public void setPay_method(String pay_method) {
        this.pay_method = pay_method;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
