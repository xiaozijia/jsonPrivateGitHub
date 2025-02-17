package com.order.food.utils;

import com.order.food.R;
import com.order.food.entity.FoodsInfo;

import java.util.ArrayList;
import java.util.List;


public class DataService {
    public static List<FoodsInfo> getHomeListData() {
        List<FoodsInfo> list = new ArrayList<>();
        list.add(new FoodsInfo(0, "你好", 20, R.mipmap.food_1, "如有疑问请致电我们","https://ts1.cn.mm.bing.net/th/id/R-C.f2c2c3765eebce8aa555d122b2bd07ef?rik=GAIaRb9Q5%2b1suw&riu=http%3a%2f%2fphotocdn.sohu.com%2f20111231%2fImg330857021.jpg&ehk=MTPX8SW%2bZ276MU4jhIwIfU3%2foFD5BCDwBlSFzYAxyPo%3d&risl=&pid=ImgRaw&r=0"));
        list.add(new FoodsInfo(0, "辣焖骨头饭", 20, R.mipmap.food_2, "新鲜可口，精选好店","https://x0.ifengimg.com/ucms/2022_16/3D6C3C47CC8EA99BD2AC9DFEC93B9F4275A42128_size220_w1290_h752.jpg"));
        list.add(new FoodsInfo(0, "战斧铁板鸭", 20, R.mipmap.food_3, "津贴随机减，叠加满减可再减1.8元","https://x0.ifengimg.com/ucms/2023_48/C2F68E3ABE0B0A80F4A21433A54892ED25CA0721_size64_w576_h324.jpg"));
        list.add(new FoodsInfo(0, "粤粥记", 20, R.mipmap.food_4, "本店主营粥店品类，奉行服务至上的理念","https://ts1.cn.mm.bing.net/th/id/R-C.6dd4dbca9437b54f160a18d974335471?rik=HC10J1kOa%2flwHg&riu=http%3a%2f%2fi1.sinaimg.cn%2fdy%2fw%2fp%2f2012-05-09%2f1336545958_rYzW35.jpg&ehk=SDRxll0Vx04VaNPbuzIJpdzZ3nvXroFW8sV3xRvpC%2fg%3d&risl=&pid=ImgRaw&r=0&sres=1&sresct=1"));
        list.add(new FoodsInfo(0, "叫了一只鸡", 20, R.mipmap.food_5, "欢迎各位新老用户光临我们门店！","https://x0.ifengimg.com/ucms/2023_48/C2F68E3ABE0B0A80F4A21433A54892ED25CA0721_size64_w576_h324.jpg"));
        list.add(new FoodsInfo(0, "隆江猪脚饭", 20, R.mipmap.food_6, "用餐高峰期请提前下单","https://ts1.cn.mm.bing.net/th/id/R-C.338c30feae50acf616088ab31c146f34?rik=v1eSHg5E7ryL4A&riu=http%3a%2f%2fimg2.zjolcdn.com%2fpic%2f0%2f15%2f28%2f98%2f15289847_130512.jpg&ehk=b82e2Jp5IKUAzq6teffdy8tvfsdRXfSInzFZz5HOy9E%3d&risl=&pid=ImgRaw&r=0"));
        list.add(new FoodsInfo(0, "港式炸鸡", 20, R.mipmap.food_7, "满30减1；满60减4；满100减8","https://ts1.cn.mm.bing.net/th/id/R-C.3c87b56f186dd633f8e1811512234bc8?rik=jfPGLJT2qaLP7g&riu=http%3a%2f%2fimg0.xinmin.cn%2f2019%2f04%2f23%2f6174951555974349.jpg%3fx-oss-process%3dstyle%2fw10&ehk=rvnmY8X3c9vp9%2fpB0oyG8mFamDPmk1%2b0T3R109U1YJY%3d&risl=&pid=ImgRaw&r=0"));
        list.add(new FoodsInfo(0, "兄弟土豆粉", 20, R.mipmap.food_8, "郫都区特色汤粉热销榜第一","https://ts1.cn.mm.bing.net/th/id/R-C.77cadce0d50f59d801fe492df16f04c0?rik=MCT%2f1VUmCpEIvQ&riu=http%3a%2f%2fn.sinaimg.cn%2fsinakd20230215s%2f198%2fw455h543%2f20230215%2fddc8-cd57517daf952c8d483c58e9f54f4eeb.jpg&ehk=m4eRuStD7QBVF87RRVbbXJOgVpOUYxce87Rzk8TWjvY%3d&risl=&pid=ImgRaw&r=0"));
        list.add(new FoodsInfo(0, "必胜客", 20, R.mipmap.food_9, "点评收录7年！鸡腿汉堡口味香甜","https://tse2-mm.cn.bing.net/th/id/OIP-C.wVFWuGM2CmbJSxMGehZQ2wHaFZ?rs=1&pid=ImgDetMain"));
        return list;
    }
}
