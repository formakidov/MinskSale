package com.ilavista.minsksale.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ilavista.minsksale.App;
import com.ilavista.minsksale.network.ServerService;
import com.ilavista.minsksale.utils.InnerAnimatorListener;
import com.ilavista.minsksale.fragment.EventsListFragment;
import com.ilavista.minsksale.R;
import com.ilavista.minsksale.SubscriptionManager;
import com.ilavista.minsksale.database.DBManager;
import com.ilavista.minsksale.database.model.Event;

import java.text.DateFormatSymbols;

import javax.inject.Inject;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        int ID = intent.getIntExtra(EventsListFragment.EXTRA_MESSAGE_ID, -1);
        int POSITION = intent.getIntExtra(EventsListFragment.EXTRA_MESSAGE_POSITION, 0);

        event = loadEvent(ID);

        // TODO: 11/8/17
        final SubscriptionManager manager = new SubscriptionManager(this);
        isSubscribed = manager.isInSubscriptions(event.getOrganizer());

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
                } else
                    layoutFavorite.setBackgroundResource(R.color.colorPrimary);

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

                manager.remove(organizer.getText().toString());

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

                manager.add(organizer.getText().toString());

                isSubscribed = true;
            }

        });
        rootLayout.setTranslationY(POSITION);
        rootLayout.animate().translationY(0).setDuration(600).setListener(new InnerAnimatorListener(rootLayout)).start();
    }

    public Event loadEvent(int ID) {
        Event event = DBManager.getEvent(ID);
        isFavorite = DBManager.isFavorite(ID);
        return event;
    }

    private String getFinishDateString() {
        String str = "";
        String finish_date = event.getFinishDate();
        String finish_time = "";
        if (event.getFinishTime().length() > 5) {
            finish_time = event.getFinishTime().substring(0, 5);
        }

        if (finish_time.equals("00:00")) finish_time = "";
        if (!finish_date.isEmpty()) {
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
        DBManager.insertFavorite(event);
        isFavorite = true;
    }

    void removeEventFromFavorite(Event event) {
        DBManager.removeFavorite(event);
        isFavorite = false;
    }

}
