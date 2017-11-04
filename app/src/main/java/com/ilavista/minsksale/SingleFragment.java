package com.ilavista.minsksale;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;


public class SingleFragment extends Fragment {

    Context context;

    private MyEvent event;
    private Boolean isFavorite = false;
    private Boolean isSubscribed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single, container, false);

        context = getActivity();
        int ID = getArguments().getInt(MainFragment.EXTRA_MESSAGE_ID, -1);
        int POSITION = getArguments().getInt(MainFragment.EXTRA_MESSAGE_POSITION, 0);
        TextView textViewName       = (TextView)v.findViewById(R.id.singleFragmentTextName);
        final TextView textViewOrganizer  = (TextView)v.findViewById(R.id.singleFragmentTextOrganizer);
        TextView textViewDate  = (TextView)v.findViewById(R.id.singleFragmentTextDate);
        TextView textViewLocation   = (TextView)v.findViewById(R.id.singleFragmentTextLocation);
        TextView textViewDescription = (TextView)v.findViewById(R.id.singleFragmentTextDescription);
        final LinearLayout mButtonAddToSubscriptions = (LinearLayout)v.findViewById(R.id.layout_add_to_subscribtions);
        final TextView textAddToSubscriptions = (TextView)v.findViewById(R.id.textViewOrganizer);
        ImageView mImage = (ImageView)v.findViewById(R.id.singleFragmentImage);
        LinearLayout mLayout = (LinearLayout) v.findViewById(R.id.singleFragmentLayout);
        final LinearLayout mButtonAddToFavorite = (LinearLayout)v.findViewById(R.id.layout_add_to_favorite);

        event = LoadEventFromDB(getActivity(), ID);

        final SubscriptionManager manager = new SubscriptionManager(getActivity());

        isSubscribed = manager.isInSubscriptions(event.getOrganizer());

        processNullText();

        Bitmap img = BitmapFactory.decodeFile(context.getFilesDir() + event.getImageName());
        mImage.setImageBitmap(img);
        textViewName.setText(event.getName());
        textViewOrganizer.setText(event.getOrganizer());
        textViewDate.setText(getDataTextView());
        textViewLocation.setText(event.getLocation());
        textViewDescription.setText(event.getDescription());

        if (isFavorite)
            mButtonAddToFavorite.setBackgroundResource(R.color.colorPrimary);
        else
            mButtonAddToFavorite.setBackgroundResource(R.drawable.my_layout_no_left);

        mButtonAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    removeEventFromFavorite(event);
                    mButtonAddToFavorite.setBackgroundResource(R.drawable.my_layout_no_left);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        int finalRadius = (int) Math.hypot(mButtonAddToFavorite.getWidth() / 2, mButtonAddToFavorite.getHeight() / 2);

                        Animator animator = ViewAnimationUtils.createCircularReveal(mButtonAddToFavorite,
                                mButtonAddToFavorite.getWidth() / 2, mButtonAddToFavorite.getHeight() / 2, 0, finalRadius);
                        mButtonAddToFavorite.setBackgroundResource(R.color.colorPrimary);

                        animator.start();
                    }
                    else
                        mButtonAddToFavorite.setBackgroundResource(R.color.colorPrimary);

                    addEventToFavorite(event);
                }
            }
        });

        if (isSubscribed)
            mButtonAddToSubscriptions.setBackgroundResource(R.color.colorPrimary);
        else
            mButtonAddToSubscriptions.setBackgroundResource(R.drawable.my_layout_no_left);
        textAddToSubscriptions.setText("Подписаться на обновления от " + event.getOrganizer());
        mButtonAddToSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSubscribed) {
                    mButtonAddToSubscriptions.setBackgroundResource(R.drawable.my_layout_no_left);

                    manager.remove(textViewOrganizer.getText().toString());

                    isSubscribed = false;
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        int finalRadius = (int) Math.hypot(mButtonAddToSubscriptions.getWidth() / 2, mButtonAddToSubscriptions.getHeight() / 2);
                        Animator animator = ViewAnimationUtils.createCircularReveal(mButtonAddToSubscriptions,
                                mButtonAddToSubscriptions.getWidth() / 2, mButtonAddToSubscriptions.getHeight() / 2, 0, finalRadius);
                        mButtonAddToSubscriptions.setBackgroundResource(R.color.colorPrimary);
                        animator.start();
                    } else
                        mButtonAddToSubscriptions.setBackgroundResource(R.color.colorPrimary);

                    manager.add(textViewOrganizer.getText().toString());

                    isSubscribed = true;
                }

            }
        });
        mLayout.setTranslationY(POSITION);
        mLayout.animate().translationY(0).setDuration(600).setListener(new InnerAnimatorListener(mLayout)).start();

        Log.d("MyLog", "Single fragment created View");
        return v;
    }

    // My functions -------------------------------------------------------------------------
    public MyEvent LoadEventFromDB(Context context, int ID){
        DBManager dbManager = new DBManager(context,DBHelper.DATABASE_TABLE_EVENTS);
        MyEvent event = dbManager.getEventFromDB(ID);
        isFavorite = dbManager.isEventFavorite(ID);
        return event;
    }
    //----------------------------------------------------------------------------------------
    void processNullText(){
        if (event.getType() == null)        event.setType("");
        if (event.getName() == null)        event.setName("");
        if (event.getOrganizer() == null)   event.setOrganizer("");
        if (event.getStartDate() == null)   event.setStartDate("");
        if (event.getStartTime() == null)   event.setStartTime("");
        if (event.getFinishDate() == null)  event.setFinishDate("");
        if (event.getFinishTime() == null)  event.setFinishTime("");
        if (event.getImageName() == null)   event.setImageName("");
        if (event.getLocation() == null)    event.setLocation("");
        if (event.getDescription() == null) event.setDescription("");
    }
    //----------------------------------------------------------------------------------------
    private String getDataTextView(){
        String str;
        String start_date = event.getStartDate();
        String finish_date = event.getFinishDate();
        String start_time = "";
        if (event.getStartTime().length()>5)
            start_time = event.getStartTime().substring(0, 5);
        String finish_time = "";
        if (event.getStartTime().length()>5)
            finish_time = event.getFinishTime().substring(0,5);

        if (start_time.equals("00:00")) start_time = "";
        if (finish_time.equals("00:00")) finish_time = "";
        if ((start_date.equals(""))&&(finish_date.equals(""))) str = "";
        else
        if ((start_date.equals(finish_date))&&(event.getFinishTime().equals("")))
            str = "Время проводения: " + processTextDate(start_date) + " с " + start_time;
        else
        if (start_date.equals(finish_date)) {
            str = "Время проводения: " + processTextDate(start_date) + " с " + start_time;
            if (!finish_time.equals("")) str += " до "
                    + finish_time;
        }
        else
        if (start_date.equals("")) str = "Окончание: " + processTextDate(finish_date)
                + "  " + finish_time;
        else
            str = "Начало: " + processTextDate(start_date) + "  " + start_time + "\n"
                    + "Окончание: " + processTextDate(finish_date) + "  " + finish_time;
        return str;
    }

    private String processTextDate(String str){
        String result;
        int day = Integer.parseInt(str.substring(8,10));
        int month_int = Integer.parseInt(str.substring(5,7));
        String month = new DateFormatSymbols().getMonths()[month_int-1];
        result = "" + day + " " + month + " " +str.substring(0,4);
        return result;
    }
    //----------------------------------------------------------------------------------------
    void addEventToFavorite(MyEvent event){
        DBManager dbManager = new DBManager(context,DBHelper.DATABASE_TABLE_FAVORITE);
        dbManager.insertInDB(event);
        isFavorite = true;
    }
    //------------------------------------------------------------------------------------
    void removeEventFromFavorite(MyEvent event){
        DBManager dbManager = new DBManager(context,DBHelper.DATABASE_TABLE_FAVORITE);
        dbManager.removeFromDB(event);
        isFavorite = false;
    }
    //------------------------------------------------------------------------------------
}
