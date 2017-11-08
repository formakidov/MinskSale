package com.ilavista.minsksale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {

    public final static String EXTRA_MESSAGE_ID = "com.ilavista.minsksale.ID";
    public final static String EXTRA_MESSAGE_POSITION = "com.ilavista.minsksale.POSITION";
    public final static String EXTRA_MESSAGE_ORGANIZER = "com.ilavista.minsksale.ORGANIZER";

    private EventAdapter mAdapter;
    private MainActivity activity;
    private AbsListView mListView;
    private ArrayList<Event> mEvents;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextDownloading;

    private String type = "All";
    private String organizer = null;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment_item, container, false);
        mEvents = new ArrayList<>();
        mAdapter = new EventAdapter(mEvents);
        activity = (MainActivity) getActivity();

        mTextDownloading = view.findViewById(R.id.main_fragment_text);

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            DownloadThread thread = new DownloadThread(activity, mHandler, activity.getHandlerRight());
            thread.start();
        });

        mListView = view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            Log.d("logf", "Click on ListView item position " + position);
            activity.setIsEventOpen(true);
            activity.setListViewPosition(position);

            Intent i = new Intent(getContext(), EventDetailsActivity.class);
            i.putExtra(EXTRA_MESSAGE_ID, view1.getId());
            i.putExtra(EXTRA_MESSAGE_POSITION, view1.getTop());
            startActivity(i);
        });

        mHandler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DownloadThread.FINISH_DOWNLOADING_SUCCESSFULLY:
                        Log.d("logf", "MainFragment Handler message: FINISH_DOWNLOADING_SUCCESSFULLY");
                        mSwipeRefreshLayout.setRefreshing(false);
                        loadData(mEvents, type);
                        animateListView(mListView);
                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                        break;

                    case DownloadThread.NO_CONNECTION:
                        Log.d("logf", "MainFragment Handler message: NO_CONNECTION");
                        mSwipeRefreshLayout.setRefreshing(false);
                        showSnackBar("Отсутсвует подключение к Интернету");
                        loadData(mEvents, type);
                        break;
                }
                mTextDownloading.setVisibility(View.GONE);
            }
        };
        activity.setHandlerLeft(mHandler);

        if (getArguments() == null) {
            type = "All";
        } else {
            type = getArguments().getString(MainActivity.EXTRA_MESSAGE_TYPE, "All");
            organizer = getArguments().getString(EXTRA_MESSAGE_ORGANIZER, null);
            int listViewPosition = getArguments().getInt(MainActivity.EXTRA_MESSAGE_LIST_POSITION, 0);
            mListView.setSelection(listViewPosition);
        }

        mTextDownloading.setVisibility(View.GONE);
        loadData(mEvents, type);

        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();

        Log.d("logf", "Main fragment created View");

        return view;
    }

    private void showSnackBar(String text) {
        Snackbar snack = Snackbar.make(mSwipeRefreshLayout, text, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        view.setBackgroundResource(R.color.colorPrimary);
        TextView snackTextView = view.findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setTextColor(Color.parseColor("#6c440b"));
        snack.show();
    }

    private class EventAdapter extends ArrayAdapter<Event> {

        int prevPosition;
        boolean animate;

        float animX = 100f;
        float animY = 100f;

        public void setAnimate(boolean animate) {
            this.animate = animate;
        }

        public EventAdapter(ArrayList<Event> events) {
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
                return null;
            }
            final ImageView mImage = convertView.findViewById(R.id.main_list_item_image);

            Bitmap img = BitmapFactory.decodeFile(activity.getFilesDir() + event.getImageName());
            mImage.setImageBitmap(img);
            convertView.setId((int) event.getID());

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

    private void loadData(List<Event> events, String type) {
        if (type.equals("selected")) {
            DBManager.loadFavorite(events, type, organizer);
        } else {
            DBManager.loadEvents(events, type, organizer);
        }
    }

    private void animateListView(AbsListView list) {
        list.setAlpha(0.5f);
        list.animate().alpha(1f).setDuration(500).setListener(new InnerAnimatorListener(mListView)).start();
        list.setTranslationY(150);
        list.animate().translationY(0).setDuration(500).setListener(new InnerAnimatorListener(mListView)).start();
    }

}
