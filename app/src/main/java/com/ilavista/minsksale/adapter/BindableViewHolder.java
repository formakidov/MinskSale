package com.ilavista.minsksale.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BindableViewHolder<T> extends RecyclerView.ViewHolder {
    protected final Context context;

    public BindableViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        context = view.getContext();
    }

    public abstract void bind(T data);

    public void onRecycled() {
    }
}