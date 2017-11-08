package com.ilavista.minsksale;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleFragment extends Fragment {
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

    Context context;

    private Event event;
    private Boolean isFavorite = false;
    private Boolean isSubscribed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single, container, false);
        ButterKnife.bind(v);

        context = getActivity();
        int ID = getArguments().getInt(MainFragment.EXTRA_MESSAGE_ID, -1);
        int POSITION = getArguments().getInt(MainFragment.EXTRA_MESSAGE_POSITION, 0);


        event = LoadEventFromDB(ID);

        final SubscriptionManager manager = new SubscriptionManager(getActivity());

        isSubscribed = manager.isInSubscriptions(event.getOrganizer());

        processNullText();

        Bitmap img = BitmapFactory.decodeFile(context.getFilesDir() + event.getImageName());
        image.setImageBitmap(img);
        textViewName.setText(event.getName());
        organizer.setText(event.getOrganizer());
        date.setText(getFinishDateString());
        location.setText(event.getLocation());
        description.setText(event.getDescription());

        if (isFavorite)
            layoutFavorite.setBackgroundResource(R.color.colorPrimary);
        else
            layoutFavorite.setBackgroundResource(R.drawable.my_layout_no_left);

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

        return v;
    }

    private String formatFinishDate(String str) {
        String result;
        int day = Integer.parseInt(str.substring(8, 10));
        int month_int = Integer.parseInt(str.substring(5, 7));
        String month = new DateFormatSymbols().getMonths()[month_int - 1];
        result = "" + day + " " + month + " " + str.substring(0, 4);
        return result;
    }

    public Event LoadEventFromDB(int ID) {
        Event event = DBManager.getEvent(ID);
        isFavorite = DBManager.isFavorite(ID);
        return event;
    }

    void processNullText() {
        if (event.getType() == null) event.setType("");
        if (event.getName() == null) event.setName("");
        if (event.getOrganizer() == null) event.setOrganizer("");
        if (event.getStartDate() == null) event.setStartDate("");
        if (event.getStartTime() == null) event.setStartTime("");
        if (event.getFinishDate() == null) event.setFinishDate("");
        if (event.getFinishTime() == null) event.setFinishTime("");
        if (event.getImageName() == null) event.setImageName("");
        if (event.getLocation() == null) event.setLocation("");
        if (event.getDescription() == null) event.setDescription("");
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

    void addEventToFavorite(Event event) {
        DBManager.insertFavorite(event);
        isFavorite = true;
    }

    void removeEventFromFavorite(Event event) {
        DBManager.removeFavorite(event);
        isFavorite = false;
    }
}
