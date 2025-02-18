package com.order.food.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.order.food.R;
import com.order.food.adapter.CarListAdapter;
import com.order.food.base.BaseFragment;
import com.order.food.dao.CarDao;
import com.order.food.dao.OrderDao;
import com.order.food.databinding.FragmentCarBinding;
import com.order.food.entity.CarInfo;
import com.order.food.entity.UserInfo;

import java.util.List;


public class CarFragment extends BaseFragment<FragmentCarBinding> {
    private CarListAdapter mCarListAdapter;
    private CarDao mCarDao;
    private int total;
    private String pay_method = "微信";
    private OrderDao mOrderDao;

    @Override
    protected FragmentCarBinding getViewBinding() {
        return FragmentCarBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {

        mCarListAdapter = new CarListAdapter();

        mBinding.recyclerview.setAdapter(mCarListAdapter);

        mCarListAdapter.setEmptyView(R.layout.empty_layout);


        //点击事件
        mCarListAdapter.setDeleteListener(new CarListAdapter.DeleteListener() {
            @Override
            public void delOnClick(CarInfo carInfo, int position) {
                if (mCarDao == null) {
                    mCarDao = new CarDao(getActivity());
                }
                int row = mCarDao.delete(carInfo.get_id() + "");
                if (row > 0) {
                    mCarListAdapter.removeAt(position);
                    showToast("删除成功");
                    //结算
                    setDataTotal(mCarDao.queryCarList(UserInfo.getUserInfo().getMobile()));
                } else {
                    showToast("删除失败");
                }
            }

            @Override
            public void subtract(CarInfo carInfo, int position) {
                //减
                if (carInfo.getFood_num() == 1) {
                    showToast("商量数量不能小于0");
                } else {
                    carInfo.setFood_num(carInfo.getFood_num() - 1);
                    mCarListAdapter.setData(position, carInfo);
                    //结算
                    setDataTotal(mCarListAdapter.getData());
                }
            }

            @Override
            public void plus(CarInfo carInfo, int position) {
                //加
                carInfo.setFood_num(carInfo.getFood_num() + 1);
                mCarListAdapter.setData(position, carInfo);
                //结算
                setDataTotal(mCarListAdapter.getData());
            }
        });


        //
        mBinding.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (total == 0) {
                    showToast("购物车为空，请先添加商品");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.pay_dialog, null);
                    RadioGroup radioGroup = rootView.findViewById(R.id.radioGroup);
                    TextView tv_total = rootView.findViewById(R.id.total);
                    EditText et_address = rootView.findViewById(R.id.address);
                    tv_total.setText(total + ".00");
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int checkid) {
                            switch (checkid) {
                                case R.id.wx:
                                    pay_method = "微信";
                                    break;
                                case R.id.zfb:
                                    pay_method = "支付宝";
                                    break;
                            }
                        }
                    });

                    builder.setView(rootView);
                    builder.setTitle("支付提醒");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.setPositiveButton("支付", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int index) {

                            String address = et_address.getText().toString();
                            if (TextUtils.isEmpty(address)) {
                                showToast("配置地址不能为空~~");
                            } else {
                                //生成订单
                                if (mOrderDao == null) {
                                    mOrderDao = new OrderDao(getActivity());
                                }

                                for (int i = 0; i < mCarListAdapter.getData().size(); i++) {
                                    CarInfo carInfo = mCarListAdapter.getData().get(i);
                                    int row = mOrderDao.insert(carInfo.getMobile(), carInfo.getTitle(), carInfo.getPrice(), carInfo.getImage(), carInfo.getFood_num(), carInfo.getDetail(), pay_method, address);
                                    Log.d("-------", carInfo.toString());
                                }

                                //清空购物车
                                if (mCarDao != null) {
                                    mCarDao.clear();
                                    initData();
                                }

                            }
                        }
                    });
                    builder.show();

                }
            }
        });
    }

    @Override
    protected void initData() {
        if (mCarDao == null) {
            mCarDao = new CarDao(getActivity());
        }
        List<CarInfo> list = mCarDao.queryCarList(UserInfo.getUserInfo().getMobile());
        mCarListAdapter.setList(list);
        setDataTotal(list);

    }

    private void setDataTotal(List<CarInfo> data) {
        total = 0;
        for (int i = 0; i < data.size(); i++) {
            int goods_price = data.get(i).getPrice();
            int goods_num = data.get(i).getFood_num();
            int price = goods_price * goods_num;
            total = total + price;
        }
        mBinding.total.setText(total + ".00");
    }

    public void refreshData() {
        initData();
    }
}