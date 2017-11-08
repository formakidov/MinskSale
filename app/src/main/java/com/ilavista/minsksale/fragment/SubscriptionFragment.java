package com.ilavista.minsksale.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ilavista.minsksale.R;
import com.ilavista.minsksale.SubscriptionManager;

import java.util.List;

public class SubscriptionFragment extends Fragment {

    private SubscriptionManager manager;
    private List<String> subscriptions;
    private Context context;
    private LinearLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subscription, container, false);
        layout = v.findViewById(R.id.subscriptionsLayout);
        return v;
    }

    @Override
    public void onStop() {
        manager.saveAll(subscriptions);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (layout.getChildCount() > 0) {
            layout.removeAllViews();
        }
        manager = new SubscriptionManager(getActivity());
        subscriptions = manager.getAll();
        context = getActivity();

        TextView mTextView;
        LinearLayout mLayout, mLayoutLeft, mLayoutRight;
        ImageView mButton;

        for (final String single_subscription : subscriptions) {

            mLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(40, 20, 20, 0);
            mLayout.setLayoutParams(params);
            mLayout.setOrientation(LinearLayout.HORIZONTAL);

            mLayoutLeft = new LinearLayout(context);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 3;
            mLayoutLeft.setLayoutParams(params);
            mLayoutLeft.setOrientation(LinearLayout.HORIZONTAL);
            mLayoutLeft.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);


            mButton = new ImageView(context);
            mButton.setBackgroundResource(R.drawable.ic_clear_black_24dp);
            mButton.setOnClickListener(v -> {
                String s = String.valueOf(single_subscription);
                SubscriptionManager manager = new SubscriptionManager(context);
                manager.remove(s);
                Log.d("logf", "Remove " + s);
                onResume();
            });

            mLayoutLeft.addView(mButton);

            mLayoutRight = new LinearLayout(context);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 2;
            mLayoutRight.setLayoutParams(params);
            mLayoutRight.setOrientation(LinearLayout.HORIZONTAL);
            mLayoutRight.setGravity(Gravity.CENTER_VERTICAL);

            mTextView = new TextView(context);
            mTextView.setText(single_subscription);
            mTextView.setTextSize(17);

            mLayoutRight.addView(mTextView);

            mLayout.addView(mLayoutLeft);
            mLayout.addView(mLayoutRight);

            layout.addView(mLayout);

        }
    }
}
