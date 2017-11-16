package com.ilavista.minsksale.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.ilavista.minsksale.R;
import com.ilavista.minsksale.model.Event;

public class EventsAdapter extends BaseAdapter<Event, EventPreviewViewHolder>{
    public EventsAdapter(Context context) {
        super(context);
    }

    @Override
    public EventPreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventPreviewViewHolder(getLayoutInflater().inflate(R.layout.events_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(EventPreviewViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.bind(getItem(position));
    }
}
