package com.zcshou.gogogo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zcshou.utils.PinyinUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支持拼音首字母分组的 Adapter
 */
public class FavoritesAdapter extends BaseAdapter implements SectionIndexer {
    private Context mContext;
    private List<Map<String, Object>> mData;
    private List<String> mSections;
    private List<Integer> mPositions;
    private LayoutInflater mInflater;

    public FavoritesAdapter(Context context, List<Map<String, Object>> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mSections = new ArrayList<>();
        this.mPositions = new ArrayList<>();
        buildSections();
    }

    private void buildSections() {
        mSections.clear();
        mPositions.clear();
        
        String lastSection = "";
        for (int i = 0; i < mData.size(); i++) {
            String name = (String) mData.get(i).get(FavoritesActivity.KEY_NAME);
            if (name == null || name.isEmpty()) continue;
            
            // 获取拼音首字母
            String section = PinyinUtils.getPinyinFirstLetter(name).toUpperCase();
            if (!section.equals(lastSection)) {
                mSections.add(section);
                mPositions.add(i);
                lastSection = section;
            }
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.favorites_item, parent, false);
            holder = new ViewHolder();
            holder.sectionView = convertView.findViewById(R.id.favorites_section);
            holder.nameView = convertView.findViewById(R.id.favorites_name);
            holder.timeView = convertView.findViewById(R.id.favorites_time);
            holder.coordView = convertView.findViewById(R.id.favorites_coord);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> item = mData.get(position);
        String name = (String) item.get(FavoritesActivity.KEY_NAME);
        String time = (String) item.get(FavoritesActivity.KEY_TIME);
        String coord = (String) item.get(FavoritesActivity.KEY_LNG_LAT_CUSTOM);

        if (name == null || name.isEmpty()) {
            name = "收藏位置";
        }

        // 检查是否需要显示分组标题
        String section = PinyinUtils.getPinyinFirstLetter(name).toUpperCase();
        String prevName = position > 0 ? (String) mData.get(position - 1).get(FavoritesActivity.KEY_NAME) : "";
        if (prevName == null || prevName.isEmpty()) prevName = "收藏位置";
        
        String prevSection = PinyinUtils.getPinyinFirstLetter(prevName).toUpperCase();
        if (position == 0 || !section.equals(prevSection)) {
            holder.sectionView.setVisibility(View.VISIBLE);
            holder.sectionView.setText(section);
        } else {
            holder.sectionView.setVisibility(View.GONE);
        }

        holder.nameView.setText(name);
        holder.timeView.setText(time);
        holder.coordView.setText(coord);

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return mSections.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (sectionIndex < 0 || sectionIndex >= mPositions.size()) {
            return 0;
        }
        return mPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mPositions.size(); i++) {
            if (position < mPositions.get(i)) {
                return i - 1;
            }
        }
        return mPositions.size() - 1;
    }

    static class ViewHolder {
        TextView sectionView;
        TextView nameView;
        TextView timeView;
        TextView coordView;
    }
}
