package com.order.food.adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.order.food.R;
import com.order.food.dao.PictureDao;
import com.order.food.entity.PictureInfo;
import com.order.food.reBackToActivity;
import com.order.food.utils.Utils;

public class PictureListAdapter extends BaseQuickAdapter<PictureInfo, BaseViewHolder> {
    private reBackToActivity reBackToActivity;
    private final PictureDao pictureDao;
    public PictureListAdapter(Context context){
        super(R.layout.activity_list_item);
        pictureDao=new PictureDao(context);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, PictureInfo productInfo) {
        Utils.loadImage(productInfo.getImageUrl(), baseViewHolder.getView(R.id.image));
      baseViewHolder.getView(R.id.image).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              pictureDao.insertOrUpdate("1",productInfo.getImageUrl());
              reBackToActivity.reBack();
          }
      });
    }
    public void setReBackToActivityListener(reBackToActivity reBackToActivity)
    {
        this.reBackToActivity=reBackToActivity;
    }
}