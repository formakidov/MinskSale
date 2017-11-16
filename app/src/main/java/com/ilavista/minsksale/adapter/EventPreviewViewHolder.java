package com.ilavista.minsksale.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ilavista.minsksale.R;
import com.ilavista.minsksale.model.Event;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventPreviewViewHolder extends BindableViewHolder<Event> {
    @BindView(R.id.image)
    ImageView image;

    public EventPreviewViewHolder(View view) {
        super(view);
        ButterKnife.bind(view);
    }

    @Override
    public void bind(Event data) {
        Glide.with(image.getContext()).load(data.getImageURL()).into(image);
    }
}
