package com.ilavista.minsksale.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ilavista.minsksale.App;
import com.ilavista.minsksale.R;
import com.ilavista.minsksale.activity.EventDetailsActivity;
import com.ilavista.minsksale.activity.MainActivity;
import com.ilavista.minsksale.database.DBManager;
import com.ilavista.minsksale.database.model.Event;
import com.ilavista.minsksale.network.ServerService;
import com.ilavista.minsksale.utils.InnerAnimatorListener;
import com.ilavista.minsksale.utils.Rx;
import com.ilavista.minsksale.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;


public class EventsListFragment extends Fragment {

    public final static String EXTRA_MESSAGE_ID = "com.ilavista.minsksale.ID";
    public final static String EXTRA_MESSAGE_POSITION = "com.ilavista.minsksale.POSITION";
    public final static String EXTRA_MESSAGE_ORGANIZER = "com.ilavista.minsksale.ORGANIZER";

    @BindView(R.id.refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.loadingTv)
    TextView loadingTv;

    @Inject
    ServerService service;

    private EventsAdapter adapter;
    private MainActivity activity;

    // TODO: 11/9/17 recycler
    private AbsListView mListView;

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
        adapter = new EventsAdapter(events);
        activity = (MainActivity) getActivity();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            updateEvents();
        });

        mListView = view.findViewById(android.R.id.list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            Intent i = new Intent(getContext(), EventDetailsActivity.class);
            i.putExtra(EXTRA_MESSAGE_ID, view1.getId());
            i.putExtra(EXTRA_MESSAGE_POSITION, view1.getTop());
            startActivity(i);
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
                    adapter.addAll(events);
                    adapter.notifyDataSetChanged();
                    animateListView(mListView);
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

    private class EventsAdapter extends ArrayAdapter<Event> {

        int prevPosition;
        boolean animate;

        float animX = 100f;
        float animY = 100f;

        public EventsAdapter(List<Event> events) {
            super(getActivity(), 0, events);
            prevPosition = 0;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            Event event = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_main_list_item, parent, false);
            }

            if (getCount() == 0) {
                Log.d("logf", "Array of events is empty");
                return convertView;
            }


            final ImageView mImage = convertView.findViewById(R.id.main_list_item_image);

            Glide.with(getContext())
                    .load(event.getImageURL())
                    .into(mImage);

            convertView.setId((int) event.getId());

            if (animate) {
                animatePostHc(position, convertView);
            }
            prevPosition = position;
            return convertView;
        }

        private void animatePostHc(int position, View v) {
            if (prevPosition < position) {
                v.setTranslationX(animX);
                v.setTranslationY(animY);
            } else {
                v.setTranslationX(-animX);
                v.setTranslationY(-animY);
            }
            v.animate().translationY(0).translationX(0).setDuration(300)
                    .setListener(new InnerAnimatorListener(v)).start();
        }
    }

    private void loadData(String type) {
        if (type.equals("selected")) {
            events = DBManager.loadFavorite(type, organizer);
        } else {
            events = DBManager.loadEvents(type, organizer);
        }
    }

    private void animateListView(AbsListView list) {
        list.setAlpha(0.5f);
        list.animate().alpha(1f).setDuration(500).setListener(new InnerAnimatorListener(mListView)).start();
        list.setTranslationY(150);
        list.animate().translationY(0).setDuration(500).setListener(new InnerAnimatorListener(mListView)).start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Rx.dispose(disposable);
    }
}
