package com.order.food.utils;

import com.order.food.R;
import com.order.food.entity.FoodsInfo;

import java.util.ArrayList;
import java.util.List;


public class DataService {
    public static List<FoodsInfo> getHomeListData() {
        List<FoodsInfo> list = new ArrayList<>();
        list.add(new FoodsInfo(0, "鲍汁黄焖鸡", 20, R.mipmap.food_1, "如有疑问请致电我们","https://ts1.cn.mm.bing.net/th/id/R-C.f2c2c3765eebce8aa555d122b2bd07ef?rik=GAIaRb9Q5%2b1suw&riu=http%3a%2f%2fphotocdn.sohu.com%2f20111231%2fImg330857021.jpg&ehk=MTPX8SW%2bZ276MU4jhIwIfU3%2foFD5BCDwBlSFzYAxyPo%3d&risl=&pid=ImgRaw&r=0",""));
        list.add(new FoodsInfo(0, "辣焖骨头饭", 20, R.mipmap.food_2, "新鲜可口，精选好店","https://x0.ifengimg.com/ucms/2022_16/3D6C3C47CC8EA99BD2AC9DFEC93B9F4275A42128_size220_w1290_h752.jpg",""));
        return list;
    }
}
