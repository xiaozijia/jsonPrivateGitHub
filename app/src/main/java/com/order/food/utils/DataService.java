package com.order.food.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.order.food.R;
import com.order.food.entity.FoodsInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DataService {
    public static List<FoodsInfo> getHomeListData(Context context) {
        String json = Utils.loadJSONFromAsset(context, "localFile.json");
        if (json != null) {
            // 使用 Gson 解析 JSON 数据
            Gson gson = new Gson();
            Type listType = new TypeToken<List<FoodsInfo>>() {}.getType();
            List<FoodsInfo> foodsInfoList = gson.fromJson(json, listType);
            Log.d("xiaozijiahaoshuai", "getHomeListData: "+foodsInfoList.get(0).getContent());
            // 输出解析结果进行验证
            if (foodsInfoList != null && !foodsInfoList.isEmpty()) {
               return foodsInfoList;
            }
        }
        return null;
    }
}
