package com.kiwi.ui.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.ImageUtils;
import com.kiwi.tracker.bean.KwFilter;
import com.kiwi.ui.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by why8222 on 2016/3/17.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder> {

    private List<KwFilter> filters;
    protected Context context;
    private int selected = 0;

    public FilterAdapter(Context context, List<KwFilter> filters) {
        this.filters = filters;
        this.context = context;
    }


    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item_layout,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.filter_thumb_image);
        viewHolder.filterName = (TextView) view
                .findViewById(R.id.filter_thumb_name);
        viewHolder.filterRoot = (FrameLayout) view
                .findViewById(R.id.filter_root);
        viewHolder.thumbSelected = (FrameLayout) view
                .findViewById(R.id.filter_thumb_selected);
        viewHolder.thumbSelected_bg = view.
                findViewById(R.id.filter_thumb_selected_bg);
        return viewHolder;
    }

    protected void onBindViewHolder(FilterHolder holder, final int position, Bitmap thumbResId, String textResid, int color) {
//        holder.thumbImage.setImageResource(thumbResId);
        holder.thumbImage.setImageBitmap(thumbResId);
        holder.filterName.setText(textResid);
        holder.filterName.setBackgroundColor(color);
        if (position == selected) {
            holder.thumbSelected.setVisibility(View.VISIBLE);
            holder.thumbSelected_bg.setBackgroundColor(color);
            holder.thumbSelected_bg.setAlpha(0.7f);
        } else {
            holder.thumbSelected.setVisibility(View.GONE);
        }

        holder.filterRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                onFilterChanged(position);
            }
        });
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, final int position) {
        int color;
        KwFilter filter = filters.get(position);
        if (filter.getName().equals("NOFILTER")) {
            color = context.getResources().getColor(R.color.filter_color_grey_light);
        } else {
            color = context.getResources().getColor(R.color.filter_color_brown_dark);
        }


        String path = "filter" + File.separator + filter.getName() + File.separator + "thumb.png";
        Bitmap bgimg = getImageFromAssetsFile(path);
        if (bgimg == null) {
            bgimg = ImageUtils.getBitmap(context.getResources(), R.drawable.filter_thumb_original);
        }
        String textRes = filter.getName();

        onBindViewHolder(holder, position, bgimg, textRes, color);
    }


    private Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();

        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    protected void onFilterChanged(int position) {
        if (position == 0) {
            onFilterChangeListener.onFilterChanged(null);
        } else {
            onFilterChangeListener.onFilterChanged(filters.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return filters == null ? 0 : filters.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;
        TextView filterName;
        FrameLayout thumbSelected;
        FrameLayout filterRoot;
        View thumbSelected_bg;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onFilterChangeListener {
        void onFilterChanged(KwFilter filter);
    }

    private onFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener) {
        this.onFilterChangeListener = onFilterChangeListener;
    }
}
