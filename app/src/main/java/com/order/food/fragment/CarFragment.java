package com.order.food.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.order.food.MainActivity;
import com.order.food.R;
import com.order.food.adapter.CarListAdapter;
import com.order.food.base.BaseFragment;
import com.order.food.dao.AddressDao;
import com.order.food.dao.CarDao;
import com.order.food.dao.OrderDao;
import com.order.food.databinding.FragmentCarBinding;
import com.order.food.entity.CarInfo;
import com.order.food.entity.PictureInfo;
import com.order.food.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;


public class CarFragment extends BaseFragment<FragmentCarBinding> {
    private CarListAdapter mCarListAdapter;
    private CarDao mCarDao;
    private int total;
    private String pay_method = "微信";
    private OrderDao mOrderDao;
    private AddressDao mAddressDao;
    private List<String> allAddresses;
    private ArrayAdapter<String> adapter;
    private  ListView addressListView;


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
                     addressListView=rootView.findViewById(R.id.addressListView);
                    mAddressDao = new AddressDao(getActivity());

                    // 获取所有本地地址
                    allAddresses = mAddressDao.queryAddressesByMobile(PictureInfo.getMobile()); // 这里的手机号根据实际情况修改

                    // 初始化适配器
                    adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_address);
                    addressListView.setAdapter(adapter);
                    et_address.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            filterAddresses(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });
                    addressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // 获取点击的地址
                            String selectedAddress = (String) parent.getItemAtPosition(position);
                            // 将选中的地址填充到输入框中
                            et_address.setText(selectedAddress);
                            // 隐藏地址列表
                            addressListView.setVisibility(View.GONE);
                        }
                    });

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
                                mAddressDao.insert(PictureInfo.getMobile(),address);
                                if (mOrderDao == null) {
                                    mOrderDao = new OrderDao(getActivity());
                                }
                                mAddressDao.insert(PictureInfo.getMobile(),address);
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

    private void filterAddresses(String keyword) {
        List<String> filteredAddresses = new ArrayList<>();
        for (String address : allAddresses) {
            if (address.contains(keyword)) {
                filteredAddresses.add(address);
            }
        }

        if (!filteredAddresses.isEmpty()) {
            // 更新适配器的数据
            adapter.clear();
            adapter.addAll(filteredAddresses);
            // 显示地址列表
            addressListView.setVisibility(View.VISIBLE);
        } else {
            // 没有匹配结果，隐藏地址列表
            addressListView.setVisibility(View.GONE);
        }
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