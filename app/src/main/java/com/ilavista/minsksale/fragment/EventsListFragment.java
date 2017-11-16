package com.ilavista.minsksale.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ilavista.minsksale.App;
import com.ilavista.minsksale.R;
import com.ilavista.minsksale.activity.EventDetailsActivity;
import com.ilavista.minsksale.activity.MainActivity;
import com.ilavista.minsksale.adapter.BaseAdapter;
import com.ilavista.minsksale.adapter.EventsAdapter;
import com.ilavista.minsksale.database.repository.EventsRepository;
import com.ilavista.minsksale.model.Event;
import com.ilavista.minsksale.network.ServerService;
import com.ilavista.minsksale.utils.Rx;
import com.ilavista.minsksale.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;


public class EventsListFragment extends Fragment {

    public final static String EXTRA_EVENT_ID = "com.ilavista.minsksale.ID";
    public final static String EXTRA_MESSAGE_ORGANIZER = "com.ilavista.minsksale.ORGANIZER";

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.loadingTv)
    TextView loadingTv;
    @BindView(R.id.recyclerEvents)
    RecyclerView recyclerEvents;

    @Inject
    ServerService service;

    private EventsAdapter adapter;
    private MainActivity activity;

    private List<Event> events;

    private String organizer = null;
    private Disposable disposable;
    private String type = "All";

    public static EventsListFragment newInstance(String type) {
        Bundle b = new Bundle();
        b.putString(MainActivity.EXTRA_MESSAGE_TYPE, type);
        EventsListFragment f = new EventsListFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);
        ButterKnife.bind(this, view);
        events = new ArrayList<>();
        adapter = new EventsAdapter(getContext());

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            updateEvents();
        });

        recyclerEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerEvents.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Context c = getContext();
            Intent i = new Intent(c, EventDetailsActivity.class);
            i.putExtra(EXTRA_EVENT_ID, adapter.getItem(position).getId());
            c.startActivity(i);
        });

        if (getArguments() != null) {
            type = getArguments().getString(MainActivity.EXTRA_MESSAGE_TYPE, "All");
            organizer = getArguments().getString(EXTRA_MESSAGE_ORGANIZER, null);
        }

        loadEvents();

        return view;
    }

    private void loadEvents() {
        disposable = service.events()
                .doOnEach(n -> loadingTv.setVisibility(View.GONE))
                .subscribe(r -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    events = r.body();
                    // TODO: 11/9/17 animate adding items
                    adapter.addItems(events);
                }, t -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    UIUtils.showSnackBar(mSwipeRefreshLayout, "Ошибка загрузки");
                });
    }

    private void updateEvents() {
        // TODO: 11/9/17
        Rx.dispose(disposable);
        loadEvents();
    }

    private void loadData(String type) {
        if (type.equals("favorite")) {
            events = EventsRepository.loadFavorite(type, organizer);
        } else {
            events = EventsRepository.loadEvents(type, organizer);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Rx.dispose(disposable);
    }
}
