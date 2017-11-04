package com.ilavista.minsksale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.AdapterView;
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
    private MainActivity.MyFragmentPagerAdapter pagerAdapter;
    private Context context;
    private AbsListView mListView;
    private ArrayList<MyEvent> mEvents;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextDownloading;

    private String type = "All";
    private String organizer = null;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment_item, container, false);
        mEvents = new ArrayList<>();
        mAdapter = new EventAdapter(mEvents);
        context = getActivity();
        pagerAdapter = ((MainActivity) context).getPagerAdapter();

        mTextDownloading = view.findViewById(R.id.main_fragment_text);

        // Set Swipe Refresh
        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                DownloadThread thread = new DownloadThread(context, mHandler, ((MainActivity) context).getHandlerRight());
                thread.start();
            }
        });

        mListView = view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("logf", "Click on ListView item position " + position);
                ((MainActivity) context).setIsEventOpen(true);
                // getting list view scroll position
                ((MainActivity) context).setListViewPosition(position);

                Fragment fragment = new SingleFragment();
                Bundle arg = new Bundle();
                arg.putInt(EXTRA_MESSAGE_ID, view.getId());
                arg.putInt(EXTRA_MESSAGE_POSITION, view.getTop());
                fragment.setArguments(arg);
                setFragmentInPager(fragment);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                mAdapter.setAnimate(scrollState == SCROLL_STATE_FLING || SCROLL_STATE_TOUCH_SCROLL == scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

        });

        mHandler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DownloadThread.FINISH_DOWNLOADING_SUCCESSFULLY:
                        Log.d("logf", "MainFragment Handler message: FINISH_DOWNLOADING_SUCCESSFULLY");
                        mSwipeRefreshLayout.setRefreshing(false);
                        LoadDataFromDB(context, mEvents, type);
                        animateListView(mListView);
                        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                        break;

                    case DownloadThread.NO_CONNECTION:
                        Log.d("logf", "MainFragment Handler message: NO_CONNECTION");
                        mSwipeRefreshLayout.setRefreshing(false);
                        MakeSnackBar("Отсутсвует подключение к Интернету");
                        LoadDataFromDB(context, mEvents, type);
                        break;
                }
                mTextDownloading.setVisibility(View.GONE);
            }
        };
        ((MainActivity) context).setHandlerLeft(mHandler);

        if (getArguments() == null) {
            type = "All";
        } else {
            type = getArguments().getString(MainActivity.EXTRA_MESSAGE_TYPE, "All");
            organizer = getArguments().getString(EXTRA_MESSAGE_ORGANIZER, null);
            int listViewPosition = getArguments().getInt(MainActivity.EXTRA_MESSAGE_LIST_POSITION, 0);
            mListView.setSelection(listViewPosition);
        }

        mTextDownloading.setVisibility(View.GONE);
        LoadDataFromDB(context, mEvents, type);

        ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();

        Log.d("logf", "Main fragment created View");

        return view;
    }

    private void MakeSnackBar(String text) {
        Snackbar snack = Snackbar.make(mSwipeRefreshLayout, text, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        view.setBackgroundResource(R.color.colorPrimary);
        TextView snackTextView = view.findViewById(android.support.design.R.id.snackbar_text);
        snackTextView.setTextColor(Color.parseColor("#6c440b"));
        snack.show();
    }

    //------------------------------------------------------------------------------------
    private void setFragmentInPager(Fragment newFragment) {
        pagerAdapter.setFragmentLeft(newFragment);
    }

    private class EventAdapter extends ArrayAdapter<MyEvent> {

        int prevPosition;
        boolean animate;

        float animX = 100f;
        float animY = 100f;

        public void setAnimate(boolean animate) {
            this.animate = animate;
        }

        public EventAdapter(ArrayList<MyEvent> events) {
            super(getActivity(), 0, events);
            prevPosition = 0;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            MyEvent event = getItem(position);

            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_main_list_item, parent, false);

            if (getCount() == 0) {
                Log.d("logf", "Array of events is empty");
                return null;
            }
            final ImageView mImage = convertView.findViewById(R.id.main_list_item_image);

            Bitmap img = BitmapFactory.decodeFile(context.getFilesDir() + event.getImageName());
            mImage.setImageBitmap(img);
            convertView.setId((int) event.getID());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (animate) animatePostHc(position, convertView);
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

    private void LoadDataFromDB(Context context, List<MyEvent> events, String type) {
        DBManager dbManager;
        if (type.equals("selected"))
            dbManager = new DBManager(context, "MyFavorite");
        else
            dbManager = new DBManager(context, "Events");
        dbManager.loadFromDBInMainThread(events, type, organizer);
    }

    private void animateListView(AbsListView list) {
        list.setAlpha(0.5f);
        list.animate().alpha(1f).setDuration(500).setListener(new InnerAnimatorListener(mListView)).start();
        list.setTranslationY(150);
        list.animate().translationY(0).setDuration(500).setListener(new InnerAnimatorListener(mListView)).start();
    }

}
