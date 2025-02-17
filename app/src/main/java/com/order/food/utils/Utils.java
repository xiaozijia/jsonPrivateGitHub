package com.order.food.utils;

import java.util.Random;


public class Utils {

    public static  String generateOrderNumber(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
