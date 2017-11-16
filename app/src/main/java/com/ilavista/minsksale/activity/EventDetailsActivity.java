package com.ilavista.minsksale.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ilavista.minsksale.App;
import com.ilavista.minsksale.fragment.EventsListFragment;
import com.ilavista.minsksale.R;
import com.ilavista.minsksale.SubscriptionManager;
import com.ilavista.minsksale.database.repository.EventsRepository;
import com.ilavista.minsksale.model.Event;

import java.text.DateFormatSymbols;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDetailsActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nameTv)
    TextView textViewName;
    @BindView(R.id.organizerTv)
    TextView organizer;
    @BindView(R.id.textDate)
    TextView date;
    @BindView(R.id.locationTv)
    TextView location;
    @BindView(R.id.descriptionTv)
    TextView description;
    @BindView(R.id.layoutSubscribe)
    LinearLayout layoutSubscribe;
    @BindView(R.id.subscriptionsOrganizerTv)
    TextView subscriptionsOrganizer;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.rootLayout)
    LinearLayout rootLayout;
    @BindView(R.id.layoutFavorite)
    LinearLayout layoutFavorite;

    private Event event;
    private Boolean isFavorite = false;
    private Boolean isSubscribed = false;

    private SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        int ID = (int) getIntent().getLongExtra(EventsListFragment.EXTRA_EVENT_ID, -1);

        event = loadEvent(ID);

        subscriptionManager = SubscriptionManager.getInstance(this);
        isSubscribed = subscriptionManager.isSubscribed(event.getOrganizer());

        Bitmap img = BitmapFactory.decodeFile(getFilesDir() + event.getImageName());
        image.setImageBitmap(img);
        textViewName.setText(event.getName());
        organizer.setText(event.getOrganizer());
        date.setText(getFinishDateString());
        location.setText(event.getLocation());
        description.setText(event.getDescription());

        if (isFavorite) {
            layoutFavorite.setBackgroundResource(R.color.colorPrimary);
        } else {
            layoutFavorite.setBackgroundResource(R.drawable.my_layout_no_left);
        }

        layoutFavorite.setOnClickListener(v1 -> {
            if (isFavorite) {
                removeEventFromFavorite(event);
                layoutFavorite.setBackgroundResource(R.drawable.my_layout_no_left);
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int finalRadius = (int) Math.hypot(layoutFavorite.getWidth() / 2, layoutFavorite.getHeight() / 2);

                    Animator animator = ViewAnimationUtils.createCircularReveal(layoutFavorite,
                            layoutFavorite.getWidth() / 2, layoutFavorite.getHeight() / 2, 0, finalRadius);
                    layoutFavorite.setBackgroundResource(R.color.colorPrimary);

                    animator.start();
                } else {
                    layoutFavorite.setBackgroundResource(R.color.colorPrimary);
                }

                addEventToFavorite(event);
            }
        });

        if (isSubscribed) {
            layoutSubscribe.setBackgroundResource(R.color.colorPrimary);
        } else {
            layoutSubscribe.setBackgroundResource(R.drawable.my_layout_no_left);
        }
        subscriptionsOrganizer.setText(String.format(getString(R.string.subscribe_on_organizer), event.getOrganizer()));

        layoutSubscribe.setOnClickListener(v1 -> {
            if (isSubscribed) {
                layoutSubscribe.setBackgroundResource(R.drawable.my_layout_no_left);

                subscriptionManager.remove(organizer.getText().toString());

                isSubscribed = false;
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int finalRadius = (int) Math.hypot(layoutSubscribe.getWidth() / 2, layoutSubscribe.getHeight() / 2);
                    Animator animator = ViewAnimationUtils.createCircularReveal(layoutSubscribe,
                            layoutSubscribe.getWidth() / 2, layoutSubscribe.getHeight() / 2, 0, finalRadius);
                    layoutSubscribe.setBackgroundResource(R.color.colorPrimary);
                    animator.start();
                } else {
                    layoutSubscribe.setBackgroundResource(R.color.colorPrimary);
                }

                subscriptionManager.subscribeTo(organizer.getText().toString());

                isSubscribed = true;
            }

        });
    }

    public Event loadEvent(int ID) {
        Event event = EventsRepository.getEvent(ID);
        isFavorite = EventsRepository.isFavorite(ID);
        return event;
    }

    private String getFinishDateString() {
        // TODO: 11/13/17 refactor
        String str;
        String start_date = event.getStartDate();
        String finish_date = event.getFinishDate();
        String start_time = "";
        if (event.getStartTime().length() > 5) {
            start_time = event.getStartTime().substring(0, 5);
        }
        String finish_time = "";
        if (event.getStartTime().length() > 5) {
            finish_time = event.getFinishTime().substring(0, 5);
        }

        if (start_time.equals("00:00")) {
            start_time = "";
        }
        if (finish_time.equals("00:00")) {
            finish_time = "";
        }
        if (TextUtils.isEmpty(start_date) && (TextUtils.isEmpty(finish_date))) {
            str = "";
        } else if (start_date.equals(finish_date) && (TextUtils.isEmpty(event.getFinishTime()))) {
            str = "Время проведения: " + formatFinishDate(start_date) + " с " + start_time;
        } else if (start_date.equals(finish_date)) {
            str = "Время проведения: " + formatFinishDate(start_date) + " с " + start_time;
            if (!TextUtils.isEmpty(finish_time)) {
                str += " до " + finish_time;
            }
        } else {
            str = "Окончание: " + formatFinishDate(finish_date) + "  " + finish_time;
        }
        return str;
    }

    private String formatFinishDate(String str) {
        String result;
        int day = Integer.parseInt(str.substring(8, 10));
        int month_int = Integer.parseInt(str.substring(5, 7));
        String month = new DateFormatSymbols().getMonths()[month_int - 1];
        result = "" + day + " " + month + " " + str.substring(0, 4);
        return result;
    }

    void addEventToFavorite(Event event) {
        EventsRepository .insertFavorite(event);
        isFavorite = true;
    }

    void removeEventFromFavorite(Event event) {
        EventsRepository .removeFavorite(event);
        isFavorite = false;
    }

}
