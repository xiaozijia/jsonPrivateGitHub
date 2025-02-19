package com.order.food;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.order.food.adapter.AddressAdapter;
import com.order.food.dao.AddressDao;
import com.order.food.entity.PictureInfo;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends AppCompatActivity {

    private RecyclerView rvAddresses;
    private AddressAdapter addressAdapter;
    private List<String> addresses;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        // 初始化数据
        addresses = new ArrayList<>();
        AddressDao addressDao = new AddressDao(this);
        List<String> queryAddresses = addressDao.queryAddressesByMobile(PictureInfo.getMobile());
        addresses.addAll(queryAddresses);

        // 初始化 RecyclerView
        rvAddresses = findViewById(R.id.rvAddresses);
        addressAdapter = new AddressAdapter(this, addresses);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(addressAdapter);

        // 清除全部按钮
        FloatingActionButton fabClearAll = findViewById(R.id.fabClearAll);
        fabClearAll.setOnClickListener(v -> {
            AddressDao addressDaoClear = new AddressDao(this);
            int deleteCount = addressDaoClear.deleteAllAddressesByMobile(PictureInfo.getMobile());
            if (deleteCount > 0) {
                addresses.clear();
                addressAdapter.notifyDataSetChanged();
                Toast.makeText(this, "所有地址已清除", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "清除失败", Toast.LENGTH_SHORT).show();
            }
        });

        // 添加地址按钮
        FloatingActionButton fabAddAddress = findViewById(R.id.fabAddAddress);
        fabAddAddress.setOnClickListener(v -> showAddAddressDialog());
    }

    // 显示添加地址对话框
    private void showAddAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加地址");
        final View inputView = getLayoutInflater().inflate(R.layout.dialog_input, null);
        builder.setView(inputView);

        builder.setPositiveButton("确定", (dialog, which) -> {
            EditText editText=inputView.findViewById(R.id.etInput);
            String newAddress = editText.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                AddressDao addressDao = new AddressDao(this);
                long insertResult = addressDao.insert(PictureInfo.getMobile(), newAddress);
                if (insertResult != -1) {
                    addresses.add(newAddress);
                    addressAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "地址添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "地址已存在", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}