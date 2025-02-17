package com.order.food.utils;

import com.order.food.R;
import com.order.food.entity.FoodsInfo;

import java.util.ArrayList;
import java.util.List;


public class DataService {
    public static List<FoodsInfo> getHomeListData() {
        List<FoodsInfo> list = new ArrayList<>();
        list.add(new FoodsInfo(0, "鲍汁黄焖鸡", 20, R.mipmap.food_1, "如有疑问请致电我们"));
        list.add(new FoodsInfo(0, "辣焖骨头饭", 20, R.mipmap.food_2, "新鲜可口，精选好店"));
        list.add(new FoodsInfo(0, "战斧铁板鸭", 20, R.mipmap.food_3, "津贴随机减，叠加满减可再减1.8元"));
        list.add(new FoodsInfo(0, "粤粥记", 20, R.mipmap.food_4, "本店主营粥店品类，奉行服务至上的理念"));
        list.add(new FoodsInfo(0, "叫了一只鸡", 20, R.mipmap.food_5, "欢迎各位新老用户光临我们门店！"));
        list.add(new FoodsInfo(0, "隆江猪脚饭", 20, R.mipmap.food_6, "用餐高峰期请提前下单"));
        list.add(new FoodsInfo(0, "港式炸鸡", 20, R.mipmap.food_7, "满30减1；满60减4；满100减8"));
        list.add(new FoodsInfo(0, "兄弟土豆粉", 20, R.mipmap.food_8, "郫都区特色汤粉热销榜第一"));
        list.add(new FoodsInfo(0, "必胜客", 20, R.mipmap.food_9, "点评收录7年！鸡腿汉堡口味香甜"));
        return list;
    }
}
