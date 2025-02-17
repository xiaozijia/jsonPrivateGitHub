package com.order.food.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.order.food.R;
import com.order.food.adapter.OrderListAdapter;
import com.order.food.base.BaseFragment;
import com.order.food.dao.OrderDao;
import com.order.food.databinding.FragmentOrderBinding;
import com.order.food.entity.UserInfo;

public class OrderFragment extends BaseFragment<FragmentOrderBinding> {
    private OrderListAdapter mOrderListAdapter;
    private OrderDao mOrderDao;

    @Override
    protected FragmentOrderBinding getViewBinding() {
        return FragmentOrderBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void setListener() {

        mOrderListAdapter = new OrderListAdapter();//适配器实例化
        mBinding.recyclerview.setAdapter(mOrderListAdapter);
        mOrderListAdapter.setEmptyView(R.layout.empty_layout);

        //点击事件
        mOrderListAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("操作");
                builder.setMessage("确定要删除吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (null != mOrderDao) {
                            int row = mOrderDao.delete(mOrderListAdapter.getData().get(position).get_id() + "");
                            if (row > 0) {
                                showToast("删除成功");
                                mOrderListAdapter.removeAt(position);
                            } else {
                                showToast("删除失败");
                            }
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        if (mOrderDao == null) {
            mOrderDao = new OrderDao(getActivity());
        }
        mOrderListAdapter.setList(mOrderDao.queryOrderList(UserInfo.getUserInfo().getMobile()));

    }

    public void refreshData() {
        initData();
    }
}