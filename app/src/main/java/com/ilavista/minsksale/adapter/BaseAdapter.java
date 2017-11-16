package com.ilavista.minsksale.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, VH extends BindableViewHolder<T>> extends RecyclerView.Adapter<VH> {

    protected static final int TYPE_ITEM = 1;

    protected Context mContext;
    protected List<T> data = new ArrayList<>();
    protected final LayoutInflater layoutInflater;

    protected OnItemClickListener onItemClickListener;

    public BaseAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public BaseAdapter(Context context) {
        this(context, new ArrayList<>(0));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setItems(List<T> items, boolean notify) {
        data.clear();
        data.addAll(items);
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void setItems(List<T> items) {
        setItems(items, true);
    }

    public void addItems(List<T> items, boolean notify) {
        data.addAll(items);
        if (notify) {
            notifyDataSetChanged();
        }
    }

    public void addItems(List<T> items) {
        addItems(items, true);
    }

    public T getItem(int pos) {
        return data.get(pos);
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        if (holder.itemView != null) {
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(position));
            }
        }
        holder.bind(data.get(position));
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}