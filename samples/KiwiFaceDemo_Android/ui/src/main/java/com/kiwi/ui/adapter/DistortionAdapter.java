package com.kiwi.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kiwi.tracker.KwFilterType;
import com.kiwi.ui.R;
import com.kiwi.ui.helper.FilterTypeHelper;

/**
 * Created by why8222 on 2016/3/17.
 */
public class DistortionAdapter extends RecyclerView.Adapter<DistortionAdapter.FilterHolder>{

    private KwFilterType[] filters;
    protected Context context;
    private int selectPos;

    public DistortionAdapter(Context context, KwFilterType[] filters) {
        this.filters = filters;
        this.context = context;
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_distortion,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.filter_thumb_image);
        return viewHolder;
    }

    protected void onBindViewHolder(FilterHolder holder, final int position, int thumbResId, int textResid, int color) {
        holder.thumbImage.setImageResource(thumbResId);
    }

    @Override
    public void onBindViewHolder(FilterHolder holder,final int position) {
        int thumbResId = FilterTypeHelper.FilterType2Thumb(filters[position]);
        int textResid = FilterTypeHelper.FilterType2Name(filters[position]);
        int color = context.getResources().getColor(
                FilterTypeHelper.FilterType2Color(filters[position]));

        onBindViewHolder(holder, position, thumbResId, textResid, color);

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                selectPos = position;
                onFilterChanged(position);
            }
        });
    }

    protected void onFilterChanged(int position) {
        onFilterChangeListener.onFilterChanged(filters[position]);
    }

    @Override
    public int getItemCount() {
        return filters == null ? 0 : filters.length;
    }

    public int getSelectPos() {
        return selectPos;
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface onFilterChangeListener{
        void onFilterChanged(KwFilterType filterType);
    }

    private onFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(onFilterChangeListener onFilterChangeListener){
        this.onFilterChangeListener = onFilterChangeListener;
    }
}
