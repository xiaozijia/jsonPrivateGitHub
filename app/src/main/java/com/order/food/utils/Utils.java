package com.order.food.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
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

    public static void loadImage(String url, ImageView imageView) {
        if (url == null) {
            throw new IllegalArgumentException("Image URL cannot be null.");
        }
        if (imageView == null) {
            throw new IllegalArgumentException("ImageView cannot be null.");
        }

        // 获取上下文
        Context context = imageView.getContext();
        // 使用 Glide 加载图片
        Glide.with(context)
                .load(url)
                .centerCrop() // 缩放图片以适应 ImageView
                .into(imageView);
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}


