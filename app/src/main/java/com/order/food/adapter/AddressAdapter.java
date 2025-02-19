package com.order.food.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.order.food.R;
import com.order.food.dao.AddressDao;
import com.order.food.dao.PictureDao;
import com.order.food.entity.PictureInfo;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private final Context context;
    private final List<String> addresses;

    public AddressAdapter(Context context, List<String> addresses) {
        this.context = context;
        this.addresses = addresses;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {
        String address = addresses.get(position);
        holder.tvAddress.setText(address);

        // 修改按钮点击事件
        holder.btnEdit.setOnClickListener(v -> showEditAddressDialog(position));

        // 删除按钮点击事件
        holder.btnDelete.setOnClickListener(v -> {
            AddressDao addressDao = new AddressDao(context);
            int deleteCount = addressDao.delete("1234567890", address);
            if (deleteCount > 0) {
                addresses.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "地址已删除", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddress;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // 显示修改地址对话框
    private void showEditAddressDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("修改地址");
        final View inputView = LayoutInflater.from(context).inflate(R.layout.dialog_input, null);
        builder.setView(inputView);

        builder.setPositiveButton("确定", (dialog, which) -> {
            final EditText editText=inputView.findViewById(R.id.etInput);
            String newAddress = editText.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                AddressDao addressDao = new AddressDao(context);
                int updateCount = addressDao.update(PictureInfo.getMobile(), addresses.get(position), newAddress);
                if (updateCount > 0) {
                    addresses.set(position, newAddress);
                    notifyItemChanged(position);
                    Toast.makeText(context, "地址修改成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "地址不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}