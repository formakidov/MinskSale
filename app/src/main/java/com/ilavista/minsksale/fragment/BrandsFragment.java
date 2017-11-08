package com.ilavista.minsksale.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ilavista.minsksale.database.DBManager;
import com.ilavista.minsksale.network.DownloadThread;
import com.ilavista.minsksale.database.model.Event;
import com.ilavista.minsksale.utils.InnerAnimatorListener;
import com.ilavista.minsksale.activity.MainActivity;
import com.ilavista.minsksale.ProgramConfigs;
import com.ilavista.minsksale.R;

import java.util.ArrayList;
import java.util.List;

public class BrandsFragment extends Fragment {

    private Context context;
    private BrandsAdapter mAdapter;
    private AbsListView mListView;
    private ArrayList<Event> mEvents;
    private ArrayList<Organizer> mOrganizers;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvents = new ArrayList<>();
        mOrganizers = new ArrayList<>();
        mAdapter = new BrandsAdapter(mOrganizers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brands_item_list, container, false);
        context = getActivity();

        // Set Swipe Refresh
        mSwipeRefreshLayout = view.findViewById(R.id.fragment_brands_refresh);
        mSwipeRefreshLayout = view.findViewById(R.id.fragment_brands_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (((MainActivity) context).isEventOpen()) {
                DownloadThread thread = new DownloadThread(context, null, mHandler);
                thread.start();
            } else {
                DownloadThread thread = new DownloadThread(context, ((MainActivity) context).getHandlerLeft(), mHandler);
                thread.start();
            }

        });

        mListView = view.findViewById(R.id.fragment_brands_list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            TextView textView = view1.findViewById(R.id.fragment_brands_item_text);

            Fragment fragment = new MainFragment();
            Bundle arg = new Bundle();
            arg.putString(MainFragment.EXTRA_MESSAGE_ORGANIZER, textView.getText().toString());
            fragment.setArguments(arg);
            MainActivity.MyFragmentPagerAdapter pagerAdapter = ((MainActivity) context).getPagerAdapter();
            pagerAdapter.setFragmentLeft(fragment);
            ((MainActivity) context).setPage(0);
            ((MainActivity) context).setType("by_organizer");
            ((MainActivity) context).setOrganizer(textView.getText().toString());
        });

        mHandler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DownloadThread.FINISH_DOWNLOADING_SUCCESSFULLY:
                        Log.d("logf", "BrandsFragment Handler message: FINISH_DOWNLOADING_SUCCESSFULLY");
                        mSwipeRefreshLayout.setRefreshing(false);
                        loadData(mEvents, mOrganizers);
                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                        animateListView(mListView);
                        break;
                    case DownloadThread.NO_CONNECTION:
                        Log.d("logf", "BrandsFragment Handler message: NO_CONNECTION");
                        mSwipeRefreshLayout.setRefreshing(false);
                        showSnackBar("Отсутсвует подключение к Интернету");
                        loadData(mEvents, mOrganizers);
                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                        break;
                }
            }
        };
        ((MainActivity) context).setHandlerRight(mHandler);

        if (ProgramConfigs.getInstance(context).isFirstStart()) {
            DownloadThread thread = new DownloadThread(context, ((MainActivity) context).getHandlerLeft(), mHandler);
            thread.start();
        } else {
            loadData(mEvents, mOrganizers);
        }

        Log.d("logf", "Brands fragment created View");
        return view;
    }

    private void getOrganizersFromEvent(List<Event> events, List<Organizer> mOrganizers) {
        Boolean isNew;
        mOrganizers.clear();
        for (Event event : events) {
            isNew = true;
            for (Organizer organizer : mOrganizers) {
                if (organizer.getName().equals(event.getOrganizer())) {
                    organizer.addEvent();
                    isNew = false;
                }
            }
            if (isNew)
                mOrganizers.add(new Organizer(event.getOrganizer()));
        }
    }

    private void showSnackBar(String text) {
        Snackbar snack = Snackbar.make(mSwipeRefreshLayout, text, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        view.setBackgroundResource(R.color.colorPrimary);
        TextView snackTextView = view.findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setTextColor(Color.parseColor("#6c440b"));
        snack.show();
    }

    private void loadData(List<Event> events, List<Organizer> organizers) {
        DBManager.loadEvents(events, "All");
        getOrganizersFromEvent(events, organizers);
    }

    private void animateListView(AbsListView list) {
        list.setAlpha(0.5f);
        list.animate().alpha(1f).setDuration(500).setListener(new InnerAnimatorListener(mListView)).start();
        list.setTranslationY(150);
        list.animate().translationY(0).setDuration(500).setListener(new InnerAnimatorListener(mListView)).start();
    }

    private class BrandsAdapter extends ArrayAdapter<Organizer> {

        int prevPosition;

        public BrandsAdapter(ArrayList<Organizer> organizers) {
            super(getActivity(), 0, organizers);
            prevPosition = 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Organizer organizer = getItem(position);

            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_brands_list_item, parent, false);

            TextView textViewName = convertView.findViewById(R.id.fragment_brands_item_text);
            TextView textViewCount = convertView.findViewById(R.id.fragment_brands_item_count_text);

            textViewName.setText(organizer.getName());
            textViewCount.setText("событий: " + organizer.getEventsCount());
            if (getCount() == 0) {
                Log.d("logf", "Array of events is empty");
                return null;
            }

            prevPosition = position;
            return convertView;
        }

    }

    private class Organizer {

        private String name;
        private int eventsCount;

        public Organizer(String name) {
            this.name = name;
            eventsCount = 1;
        }

        public void addEvent() {
            eventsCount++;
        }

        public String getName() {
            return name;
        }

        public int getEventsCount() {
            return eventsCount;
        }
    }

}
